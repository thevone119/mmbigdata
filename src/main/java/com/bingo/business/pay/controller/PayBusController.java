package com.bingo.business.pay.controller;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.filter.ControllerFilter;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.QRCodeUtils;
import com.bingo.common.utility.XJsonInfo;
import com.google.zxing.Result;
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

/**
 * @author huangtw
 * 2018-07-09 09:33:38
 * 对象功能:  Controller管理
 */
@RestController
@RequestMapping("/api/pay/paybus")
public class PayBusController  {

	@Resource
	private SessionCacheService sessionCache;


	@Resource
	private PayBusService paybusService;

	public PayBusController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(PayBus vo) throws ServiceException, DaoException {
		//权限控制，只能保存自己的
		SessionUser loginuser = sessionCache.getLoginUser();
		if(loginuser.getUsertype()!=1 && !vo.getBusId().equals(loginuser.getUserid())){
			return null;
		}
        paybusService.saveOrUpdate(vo);
        return new XJsonInfo();
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
	public XJsonInfo saveCurrBus(PayBus vo) throws ServiceException, DaoException {
		//权限控制，只能保存自己的
		SessionUser loginuser = sessionCache.getLoginUser();
		PayBus bus = paybusService.get(loginuser.getUserid());
		bus.setGobackUrl(vo.getGobackUrl());
		bus.setNotifyUrl(vo.getNotifyUrl());
		bus.setAutoReFee(vo.getAutoReFee());
		paybusService.saveOrUpdate(bus);
		return new XJsonInfo();
	}



	/**
	 * @description: <查询>
	 * @param:
	 * @throws:
	 */
    @ResponseBody
    @RequestMapping("/query")
    public XJsonInfo query(Long id) throws ServiceException, DaoException {
        PayBus vo = paybusService.get(id);
		//权限控制，只能查看自己的
		SessionUser loginuser = sessionCache.getLoginUser();
		if(loginuser.getUsertype()!=1 && !vo.getBusId().equals(loginuser.getUserid())){
			return null;
		}
        return new XJsonInfo().setData(vo);
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
		PayBus vo = paybusService.get(loginuser.getUserid());
		return new XJsonInfo().setData(vo);
	}

	/**
	 * @description: <分页查询>
	 * @param:
	 * @throws:
	 */
    @ResponseBody
    @RequestMapping("/findPage")
    public XJsonInfo findPage(PayBus vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(paybusService.findPage(vo));
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
		PayBus vo = paybusService.get(loginuser.getUserid());
		//二维码内容解码
		Result ret = QRCodeUtils.getQRresult(file.getInputStream());
		if(payType==1){
			vo.setPayImgContentWx(ret.getText());
		}
		if(payType==2){
			vo.setPayImgContentZfb(ret.getText());
		}
		paybusService.saveOrUpdate(vo);
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
		PayBus vo = paybusService.get(loginuser.getUserid());
		OutputStream out = null;
		InputStream in = null;
		try{
			String text = null;
			if(payType==1){
				text = vo.getPayImgContentWx();
			}
			if(payType==2){
				text = vo.getPayImgContentZfb();
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

	
}
