package com.bingo.business.pay.controller;

import com.bingo.business.sys.model.SysUser;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.filter.ControllerFilter;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.QRCodeUtils;
import com.bingo.common.utility.XJsonInfo;
import com.google.zxing.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.bingo.business.pay.model.*;
import com.bingo.business.pay.service.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author huangtw
 * 2019-04-18 20:13:29
 * 对象功能: 子账户 Controller管理
 */
@RestController
@RequestMapping("/api/pay/paysubaccount")
public class PaySubAccountController  {
	@Resource
	private SessionCacheService sessionCache;

	@Resource
    private PubClass pubClass;
    
	@Resource
	private PaySubAccountService paysubaccountService;

	public PaySubAccountController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(PaySubAccount vo) throws ServiceException, DaoException {
		SessionUser loginuser = sessionCache.getLoginUser();
		vo.setBusId(loginuser.getUserid());
		//如果是新增，不允许保存相同的子账号哦
		if(vo.getSid()<=0){
			List<PaySubAccount>  listsub =paysubaccountService.queryByAccount(loginuser.getUserid(),vo.getSubaccount());
			if(listsub!=null && listsub.size()>0){
				return new XJsonInfo(false,"已存在的子账号，请勿添加相同的子账号");
			}
		}

		paysubaccountService.saveOrUpdate(vo);
        return new XJsonInfo();
    }

	/**
	 * @description: <删除>
	 * @param:
	 * @throws:
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
    @ResponseBody
    @RequestMapping("/delete")
    public XJsonInfo delete(String[] selRows) throws ServiceException, DaoException {
		for(String id:selRows){
			paysubaccountService.delete(new Long(id));
		}
        return new XJsonInfo();
    }

	/**
	 * @description: <查询>
	 * @param:
	 * @throws:
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
    @ResponseBody
    @RequestMapping("/query")
    public XJsonInfo query(Long sid) throws ServiceException, DaoException {
		PaySubAccount vo = paysubaccountService.get(sid);
        if(vo==null){
            vo = new PaySubAccount();
        }
        return new XJsonInfo().setData(vo);
    }

	/**
	 * @description: <分页查询>
	 * @param:
	 * @throws:
	 * 只查询当前商户的子账户
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
    @ResponseBody
    @RequestMapping("/findPage")
    public XJsonInfo findPage(PaySubAccount vo) throws ServiceException, DaoException {
		//权限控制，只能处理自己的
		SessionUser loginuser = sessionCache.getLoginUser();
		vo.setBusId(loginuser.getUserid());
        return  new XJsonInfo().setPageData(paysubaccountService.findPage(vo));
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
	public XJsonInfo upload_qr_img(@RequestParam("uploadfile") MultipartFile file, Long sid) throws Exception {
		SessionUser loginuser = sessionCache.getLoginUser();
		PaySubAccount acc = paysubaccountService.get(sid);
		if(acc.getBusId()!=loginuser.getUserid()){
			return null;
		}
		//二维码内容解码
		Result ret = QRCodeUtils.getQRresult(file.getInputStream());
		acc.setPayImgContent(ret.getText());
		paysubaccountService.saveOrUpdate(acc);
		return  new XJsonInfo();
	}

	
}
