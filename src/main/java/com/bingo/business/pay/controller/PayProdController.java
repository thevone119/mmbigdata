package com.bingo.business.pay.controller;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.filter.ControllerFilter;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.GetBigFileMD5;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.QRCodeUtils;
import com.bingo.common.utility.XJsonInfo;
import com.google.zxing.Result;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.*;
import java.util.Date;

/**
 * @author huangtw
 * 2018-07-09 09:34:03
 * 对象功能:  Controller管理
 */
@RestController
@RequestMapping("/api/pay/payprod")
@ControllerFilter(LoginType = 1,UserType = 0)
public class PayProdController  {

	@Resource
	private SessionCacheService sessionCache;
    
	@Resource
	private PayProdService payprodService;




	public PayProdController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(PayProd vo) throws ServiceException, DaoException {
		XJsonInfo ret=new XJsonInfo(false);
		SessionUser loginuser = sessionCache.getLoginUser();
		if(vo.getBusId()==null||loginuser.getUsertype()!=1){
			vo.setBusId(loginuser.getUserid());
		}

        payprodService.saveOrUpdate(vo);
        return new XJsonInfo();
    }

	/**
	 * @description: <删除>
	 * @param:
	 * @throws:
	 */
    @ResponseBody
    @RequestMapping("/delete")
    public XJsonInfo delete(String[] selRows) throws ServiceException, DaoException {
		SessionUser loginuser = sessionCache.getLoginUser();
		for(String id:selRows){
			PayProd vo = payprodService.get(new Long(id));
			if(loginuser.getUsertype()!=1 && !vo.getBusId().equals(loginuser.getUserid())){
				continue;
			}
			payprodService.delete(new Long(id));
		}
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
        PayProd vo = payprodService.get(id);
        if(vo==null){
            vo = new PayProd();
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
    public XJsonInfo findPage(PayProd vo) throws ServiceException, DaoException {
		SessionUser loginuser = sessionCache.getLoginUser();
    	if(vo.getBusId()==null||loginuser.getUsertype()!=1){
			vo.setBusId(loginuser.getUserid());
		}
        return  new XJsonInfo().setPageData(payprodService.findPage(vo));
    }

	/**
	 * 查看快捷支付的二维码
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws DaoException
	 */
	@RequestMapping("/prod_qr_img")
	public void prod_qr_img(HttpServletRequest request, HttpServletResponse response, String prodid) throws IOException, DaoException {
		OutputStream out = null;
		InputStream in = null;
		try{
			out = response.getOutputStream();
			StringBuffer requrl = request.getRequestURL();
			String tempContextUrl = requrl.delete(requrl.length() - request.getRequestURI().length(), requrl.length()).toString();
			String url = tempContextUrl+"/payapi/create_quick?prodid="+prodid;

			QRCodeUtils.createQRcode(url,out);
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
