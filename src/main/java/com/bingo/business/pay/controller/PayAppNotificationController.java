package com.bingo.business.pay.controller;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.utility.XJsonInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bingo.business.pay.model.*;
import com.bingo.business.pay.service.*;

/**
 * @author huangtw
 * 2018-08-24 00:49:22
 * 对象功能:  Controller管理
 */
@RestController
@RequestMapping("/api/pay/payappnotification")
public class PayAppNotificationController  {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PayAppNotificationController.class);
	@Resource
	private PayAppNotificationService payappnotificationService;


	@Resource
	private PayService payService;



	//客户端监听通知的包名
	private static List<String> listPackageName =new ArrayList<>();

	public PayAppNotificationController(){
		if(listPackageName.size()==0){
			listPackageName.add("weixin");
			listPackageName.add("zfb");
		}
	}


	@ResponseBody
	@RequestMapping("/queryPackageName")
	public XJsonInfo queryPackageName() throws ServiceException, DaoException {
		return new XJsonInfo().setData(listPackageName);
	}

	
	/**
	 * 保存APP的通知，同时，进行相关适配订单等逻辑处理
	 *
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/saveAppNotification")
    public XJsonInfo saveAppNotification(PayAppNotification vo) throws ServiceException, DaoException, IllegalAccessException {
		XJsonInfo ret = new XJsonInfo(false);
		//先验证签名
		if(!vo.getSign().equals(vo.MarkSign())){
			ret.setSuccess(false);
			ret.setCode(-1);
			ret.setMsg("签名无效");
			logger.info("签名无效:"+vo.toString());
			return ret;
		}

		//判断通知内容是否是需要的内容，如果不是，也直接返回成功
		if(vo.getPackageName()==null){
			ret.setSuccess(false);
			ret.setCode(-1);
			ret.setMsg("数据无效");
			logger.info("数据无效:"+vo.toString());
			return ret;
		}
		//判断是否微信，支付宝的通知
		boolean ispay = false;
		for(String pn:listPackageName){
			if(vo.getPackageName().indexOf(pn)!=-1){
				ispay = true;
				break;
			}
		}

		//这种数据直接返回true
		if(!ispay){
			ret.setSuccess(true);
			ret.setCode(1);
			ret.setMsg("数据无效");
			logger.info("数据无效:"+vo.toString());
			return ret;
		}

		//判断是否已存在，已存在的直接返回已存在
		PayAppNotification _vo = payappnotificationService.get(vo.getNkey());
		if(_vo!=null){
			ret.setSuccess(true);
			ret.setCode(1);
			ret.setMsg("对象已存在");
			logger.info("对象已存在:"+vo.toString());
			return ret;
		}

		//没有存在的先保存对象
        payappnotificationService.saveOrUpdate(vo);
		ret.setSuccess(true);
		ret.setCode(0);
		ret.setMsg("ok");
		payService.doNotification(vo);
		//保存成功即代表成功，后续处理失败也不用管
        return ret;
    }






	/**
	 * @description: <查询>
	 * @param:
	 * @throws:
	 */
    @ResponseBody
    @RequestMapping("/query")
    public XJsonInfo query(Long id) throws ServiceException, DaoException {
		PayAppNotification vo = payappnotificationService.get(id);
        if(vo==null){
            vo = new PayAppNotification();
        }
        return new XJsonInfo().setData(vo);
    }

	/**
	 * @description: <分页查询>
	 * @param:
	 * @throws:
	 */
    @ResponseBody
    @RequestMapping("/findPage")
    public XJsonInfo findPage(PayAppNotification vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(payappnotificationService.findPage(vo));
    }



	
}
