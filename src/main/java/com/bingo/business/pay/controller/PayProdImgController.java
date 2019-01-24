package com.bingo.business.pay.controller;

import com.bingo.business.pay.model.PayProd;
import com.bingo.business.pay.model.PayProdImg;
import com.bingo.business.pay.service.PayProdImgService;
import com.bingo.business.pay.service.PayProdService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.filter.ControllerFilter;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.BaiduApiService;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.QRCodeUtils;
import com.bingo.common.utility.XJsonInfo;
import com.google.zxing.Result;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author huangtw
 * 2018-07-09 09:34:03
 * 对象功能:  Controller管理
 */
@RestController
@RequestMapping("/api/pay/payprodimg")
@ControllerFilter(LoginType = 1,UserType = 0)
public class PayProdImgController {
	private static final Logger logger = LoggerFactory.getLogger(PayProdImgController.class);

	@Resource
	private SessionCacheService sessionCache;

	@Resource
	private PayProdImgService payProdImgService;

	@Resource
	private BaiduApiService baiduApiService;





	public PayProdImgController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(PayProdImg vo) throws ServiceException, DaoException {
		XJsonInfo ret=new XJsonInfo(false);
		SessionUser loginuser = sessionCache.getLoginUser();
		if(vo.getUserId()==null||loginuser.getUsertype()!=1){
			vo.setUserId(loginuser.getUserid());
		}
		payProdImgService.saveOrUpdate(vo);
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
			PayProdImg vo = payProdImgService.get(new Long(id));
			if(loginuser.getUsertype()!=1 && !vo.getUserId().equals(loginuser.getUserid())){
				continue;
			}
			payProdImgService.delete(new Long(id));
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
        PayProdImg vo = payProdImgService.get(id);
        if(vo==null){
            vo = new PayProdImg();
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
    public XJsonInfo findPage(PayProdImg vo) throws ServiceException, DaoException {
		SessionUser loginuser = sessionCache.getLoginUser();
    	if(vo.getUserId()==null||loginuser.getUsertype()!=1){
			vo.setUserId(loginuser.getUserid());
		}
        return  new XJsonInfo().setPageData(payProdImgService.findPage(vo));
    }


	/**
	 * 上传附件信息
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ResponseBody
	@RequestMapping("/upload_qr_img")
	public XJsonInfo upload_qr_img(@RequestParam("uploadfile") MultipartFile file) throws Exception {
		XJsonInfo ret = new XJsonInfo(false);
		SessionUser loginuser = sessionCache.getLoginUser();
		if(loginuser==null){
			ret.setMsg("您还未登陆，或已登录超时，请重新登录后再试");
			return ret;
		}

		PayProdImg vo = new PayProdImg();
		vo.setUserId(loginuser.getUserid());
		//二维码内容解码
		Result reqr = QRCodeUtils.getQRresult(file.getInputStream());
		if(reqr==null){
			reqr = QRCodeUtils.getQRresultByWeb(file.getBytes());
		}
		if(reqr==null){
			ret.setMsg("当前图片无法识别，请重新生成二维码图片");
			return ret;
		}
		PayProdImg _vo = payProdImgService.findByImgContent(reqr.getText());
		if(_vo!=null){
			ret.setMsg("当前金额的二维码已存在，无需重新上传");
			return ret;
		}

		JSONObject res = baiduApiService.basicGeneral(file.getBytes(),null);
		JSONArray arr = res.getJSONArray("words_result");
		for(int i=0;i<arr.length();i++){
			String words = arr.getJSONObject(i).getString("words");
			if(words!=null && words.indexOf("￥")!=-1){
				words = words.replace("￥","");
				if(words.indexOf(".")==-1){
					words = words.substring(0,words.length()-2)+"."+words.substring(words.length()-2);
				}
				vo.setImgPrice(new Float(words));
			}
		}
		vo.setImgContent(reqr.getText());
		if(vo.getImgContent()==null||vo.getImgContent().length()<5||vo.getImgPrice()==null){
			ret.setMsg("当前上传二维码无法识别，请重新上传");
			return ret;
		}

		if(vo.getImgContent().toUpperCase().indexOf(".ALIPAY.")!=-1){
			vo.setPayType(1);
		}
		if(vo.getImgContent().toLowerCase().indexOf("wxp:")!=-1){
			vo.setPayType(2);
		}
		PayProdImg _img = payProdImgService.findByPriceAndType(vo.getUserId(),vo.getImgPrice(),vo.getPayType());
		if(_img!=null){
			ret.setMsg("当前金额的二维码已存在，无需重新上传");
			return ret;
		}
		payProdImgService.saveOrUpdate(vo);
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
	public void qr_img_view(HttpServletRequest request, HttpServletResponse response, Long cid) throws IOException, DaoException {
		OutputStream out = null;
		InputStream in = null;
		try{
			PayProdImg vo = payProdImgService.get(cid);
			if(vo==null||vo.getImgContent()==null){
				return;
			}
			out = response.getOutputStream();
			QRCodeUtils.createQRcode(vo.getImgContent(),out);
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
