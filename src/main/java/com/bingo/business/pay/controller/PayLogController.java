package com.bingo.business.pay.controller;

import com.bingo.business.pay.parameter.PayInput;
import com.bingo.business.pay.parameter.PayReturn;
import com.bingo.business.sys.model.SysUser;
import com.bingo.business.sys.service.SysUserService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.filter.ControllerFilter;
import com.bingo.common.model.Page;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.SecurityClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.bingo.business.pay.model.*;
import com.bingo.business.pay.service.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author huangtw
 * 2018-07-09 09:34:30
 * 对象功能:  Controller管理
 */
@RestController
@RequestMapping("/api/pay/paylog")
public class PayLogController  {

	@Resource
	private SessionCacheService sessionCache;
    
	@Resource
	private PayLogService paylogService;

	@Resource
	private PayService payService;

	@Resource
	private SysUserService sysUserService;


	public PayLogController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	//@ControllerFilter(LoginType = 1,UserType = 0)
	//@ResponseBody
    //@RequestMapping("/save")
    public XJsonInfo save(PayLog vo) throws ServiceException, DaoException {
        paylogService.saveOrUpdate(vo);
        return new XJsonInfo();
    }






	/**
	 * @description: <查询>
	 * @param:
	 * @throws:
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
    @ResponseBody
    @RequestMapping("/queryone")
    public XJsonInfo queryone(Long logId) throws ServiceException, DaoException {
		PayLog vo = paylogService.get(logId);
		//权限控制，只能查看自己的
		SessionUser loginuser = sessionCache.getLoginUser();
		if(loginuser.getUsertype()!=1 && !vo.getBusId().equals(loginuser.getUserid())){
			return null;
		}
        return new XJsonInfo().setData(vo);
    }

	/**
	 * 提供外部系统接口查询，不用登陆
	 * @param id
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ResponseBody
	@RequestMapping("/query")
	public XJsonInfo query(Long id) throws ServiceException, DaoException {
		PayLog vo = paylogService.get(id);
		if(vo==null){
			//vo = new PayLog();
		}
		return new XJsonInfo().setData(vo);
	}

	/**
	 * 收款确认接口，提供给收款APP使用
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/app_pay_check")
	public XJsonInfo app_pay_check(Long logId,String orderid,String rid,String nonce_str,String uid,String sign) throws Exception {
		//1.查询商户
		XJsonInfo ret = new XJsonInfo(false);
		SysUser paybus = sysUserService.queryByUuid(uid);
		if(paybus==null){
			ret.setCode(-2);
			ret.setMsg("商户无效");
			return ret;
		}
		StringBuffer stringA =new  StringBuffer();
		stringA.append("logId="+logId+"&orderid="+orderid+"&rid="+rid+"&uid="+paybus.getUuid()+"&busId="+paybus.getUserid()+"&nonce_str="+nonce_str);
		String _sign =  SecurityClass.encryptMD5(stringA.toString()).toUpperCase();
		if(!_sign.equals(sign)){
			ret.setCode(13);
			ret.setMsg("签名错误");
			return ret;
		}
		PayLog log = paylogService.queryByAllid(logId,orderid,rid);
		XJsonInfo retj = payService.checkPay(log,2);
		return retj;
	}

	/**
	 * 提供外部系统接口查询，不用登陆
	 * @param rid
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ResponseBody
	@RequestMapping("/queryByRid")
	public XJsonInfo queryByRid(String rid) throws ServiceException, DaoException {
		PayLog vo = paylogService.queryByRid(rid);
		if(vo==null){
			return new XJsonInfo(false,"订单查询失败，请稍候再试");
		}
		return new XJsonInfo().setData(vo);
	}


	/**
	 * 前后台通用，后台查询必须要登录
	 * 前台查询，需要传uuid进行查询,
	 * @description: <分页查询>
	 * @param:
	 * @throws:
	 */
    @ResponseBody
    @RequestMapping("/findPage")
    public XJsonInfo findPage(PayLog vo) throws ServiceException, DaoException {
		SessionUser loginuser = sessionCache.getLoginUser();
		if(loginuser==null){
			if(vo.getBusId()==null || vo.getUid()==null){
				return null;
			}
		}

		if(vo.getBusId()==null){
			if(loginuser.getUsertype()!=1){
				vo.setBusId(loginuser.getUserid());
			}
		}
        return  new XJsonInfo().setPageData(paylogService.findPage(vo));
    }

	/**
	 * 导出txt
	 * @param vo
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
	@ResponseBody
	@RequestMapping("/Export")
	public void Export(HttpServletResponse response, PayLog vo) throws ServiceException, DaoException, IOException {
		//一次最多允许导出1000条
		SessionUser loginuser = sessionCache.getLoginUser();
		if(vo.getBusId()==null||loginuser.getUsertype()!=1){
			vo.setBusId(loginuser.getUserid());
		}
		vo.setPageSize(1000);
		vo.setTotalCount(10000);
		Page<PayLog> page=  paylogService.findPage(vo);
		List<PayLog> list = page.getResult();
		if(list==null||list.size()==0){
			return;
		}
		//导出到txt
		//清空response
		response.reset();
		response.setContentType("text/plain");
		//response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition","attachment; filename=" +  java.net.URLEncoder.encode("支付记录下载", "UTF-8")       + ".txt");//导出中文名称
		//通知浏览器下载文件而不是打开网页
		//response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(downloadFileName, "UTF-8"));

		BufferedOutputStream buff=null;

		try{
			OutputStream outSTr = response.getOutputStream();// 建立
			//return  new XJsonInfo().setPageData(paylogService.findPage(vo));
			buff = new BufferedOutputStream(outSTr);
			String tab = "\r\n";
			String tcb = "|";
			//先输出头
			StringBuilder write = new StringBuilder();
			write.append("商户uid"+tcb);
			write.append("商户账号"+tcb);

			write.append("支付订单名称"+tcb);
			write.append("支付订单备注"+tcb);

			write.append("业务订单号"+tcb);
			write.append("平台订单号"+tcb);

			write.append("订单金额"+tcb);
			write.append("支付金额"+tcb);

			write.append("支付类型"+tcb);
			write.append("支付状态"+tcb);

			write.append("创建时间"+tcb);
			write.append("支付时间"+tcb);

			write.append("支付扩展字段1"+tcb);
			write.append("支付扩展字段2"+tcb);

			write.append("收货人姓名"+tcb);
			write.append("收货人手机"+tcb);
			write.append("收货地址"+tcb);

			write.append(tab);


			buff.write(write.toString().getBytes("UTF-8"));

			for(PayLog log:list){
				write = new StringBuilder();
				write.append(log.getUid()+tcb);
				write.append(log.getBusAcc()+tcb);

				write.append(log.getPayName()+tcb);
				write.append(log.getPayDemo()+tcb);

				write.append(log.getOrderid()+tcb);
				write.append(log.getLogId()+tcb);

				write.append(log.getProdPrice()+tcb);
				write.append(log.getPayImgPrice()+tcb);

				write.append(log.getPayTypeStr()+tcb);
				write.append(log.getPayStateStr()+tcb);

				write.append(log.getCreatetime()+tcb);
				write.append(log.getPayTime()+tcb);

				write.append(log.getPayExt1()+tcb);
				write.append(log.getPayExt2()+tcb);

				write.append(log.getUserName()+tcb);
				write.append(log.getUserPhone()+tcb);
				write.append(log.getUserAddress()+tcb);
				write.append(tab);

				buff.write(write.toString().getBytes("UTF-8"));
			}
			buff.flush();
			buff.close();
		}catch (Exception e){
			e.printStackTrace();
			if(buff!=null){
				buff.close();
			}
		}
	}



	/**
	 * 我已收款,后台使用，需要登录
	 * @param rid
	 * @param uid
	 * @param checkType
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
	@ResponseBody
	@RequestMapping("/checkPay")
	public XJsonInfo checkPay(String rid,String uid,int checkType) throws Exception {
		PayLog log = paylogService.queryByRidUid(rid,uid);
		return payService.checkPay(log,checkType);
	}

	/**
	 * 发起通知，后台，手机APP通用
	 * @param rid
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/payNotify")
	public XJsonInfo payNotify(String rid,String uid) throws Exception {
		PayLog log = paylogService.queryByRidUid(rid,uid);
		SysUser bus = sysUserService.queryByUuid(uid);
		return payService.payNotify(log,bus);
	}

	
}
