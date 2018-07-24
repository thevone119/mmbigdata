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

	@Value("${PAY_QR_IMG_PATH}")
	private String PAY_QR_IMG_PATH;

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
		if(vo.getUserId()==null||loginuser.getUsertype()!=1){
			vo.setUserId(loginuser.getUserid());
		}
		if(vo.getPayImgPrice()>vo.getProdPrice()){
			ret.setMsg("二维码价格不能大于商品价格");
			return ret;
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
			if(loginuser.getUsertype()!=1 && !vo.getUserId().equals(loginuser.getUserid())){
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
    	if(vo.getUserId()==null||loginuser.getUsertype()!=1){
			vo.setUserId(loginuser.getUserid());
		}
        return  new XJsonInfo().setPageData(payprodService.findPage(vo));
    }


	/**
	 * 上传附件信息
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ResponseBody
	@RequestMapping("/upload_qr_img")
	public XJsonInfo upload_qr_img(@RequestParam("uploadfile") MultipartFile file,Long prodId) throws Exception {
		PayProd vo = payprodService.get(prodId);

		//创建根目录
		File basepath = new File(PAY_QR_IMG_PATH);
		if(!basepath.isDirectory()){
			basepath.mkdirs();
		}

		String filename = file.getOriginalFilename();
		String[] fnames = filename.split("\\.");
		//文件扩展名
		String extname = fnames[fnames.length-1];
		vo.setPayImgType(extname);

		//保存文件
		String filepath =PAY_QR_IMG_PATH+"/"+vo.getPayImgPath()+"."+vo.getPayImgType();
		file.transferTo(new File(filepath));
		//二维码内容解码
		Result ret = QRCodeUtils.getQRresult(filepath);
		vo.setPayImgContent(ret.getText());

		payprodService.saveOrUpdate(vo);

		return  new XJsonInfo();
	}

	/**
	 * 直接在线查看二维码
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws DaoException
	 */
	@RequestMapping("/qr_img_view")
	public void qr_img_view(HttpServletRequest request, HttpServletResponse response, Long prodId) throws IOException, DaoException {
		OutputStream out = null;
		InputStream in = null;
		try{
			PayProd vo = payprodService.get(prodId);
			if(vo==null||vo.getPayImgContent()==null){
				return;
			}
			out = response.getOutputStream();
			QRCodeUtils.createQRcode(vo.getPayImgContent(),out);
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
