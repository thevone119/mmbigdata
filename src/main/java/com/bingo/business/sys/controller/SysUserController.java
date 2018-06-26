package com.bingo.business.sys.controller;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.service.RedisCacheService;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.RandomImg;
import com.bingo.common.utility.SecurityClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bingo.business.sys.model.*;
import com.bingo.business.sys.service.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author huangtw
 * 2018-06-24 23:55:28
 * 对象功能: 淘宝用户表 Controller管理
 */
@RestController
@RequestMapping("/api/sys/sysuser")
public class SysUserController  {
	
	@Resource
    private PubClass pubClass;
    
	@Resource
	private SysUserService sysuserService;

	@Resource
	private RedisCacheService redis;

	@Resource
	private SessionCacheService sessionCache;


	public SysUserController(){
		
	}

	/**
	 * 获取随机验证码(有效期5分钟)
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ResponseBody
	@RequestMapping("/getRandomImg")
	public void getRandomImg(HttpServletResponse response,HttpServletRequest request) throws ServiceException, DaoException {
		RandomImg draw = new RandomImg();
		String rdCode = draw.getCode();
		BufferedImage image = draw.getImg();
		OutputStream os = null;
		//验证码放入缓存中5分钟有效，关联session放入
		String sessid = sessionCache.getCurrSessionId();
		redis.set("RandomImg:"+sessid,rdCode,5);
		try {
			os = response.getOutputStream();
			ImageIO.write(image, "PNG", os);
			image.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(os != null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}


	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
	@RequestMapping("/Login")
	public XJsonInfo login(HttpServletRequest request,String acc,String pwd,String imgcode) throws ServiceException, DaoException {
		XJsonInfo ret = new XJsonInfo();
		ret.setSuccess(false);
		if(acc==null || pwd==null || imgcode==null){
			ret.setMsg("参数错误，请重新输入1");
			return ret;
		}
		String sessid = sessionCache.getCurrSessionId();
		String code = (String)redis.get("RandomImg:"+sessid);
		if(!imgcode.toUpperCase().equals(code)){
			ret.setMsg("验证码错误，请重新输入");
			return ret;
		}
		pwd= SecurityClass.encryptMD5(pwd);
		SysUser user = sysuserService.queryByUserAndPwd(acc,pwd);
		if(user==null || user.getState()!=1){
			ret.setMsg("用户名或密码错误，请重试");
			return ret;
		}
		//登录成功，把用户放入session
		sessionCache.setloginUser(user);
		return new XJsonInfo();
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(SysUser vo) throws ServiceException, DaoException {
        sysuserService.saveOrUpdate(vo);
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
		for(String id:selRows){
			sysuserService.delete(new Long(id));
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
        SysUser vo = sysuserService.get(id);
        if(vo==null){
            vo = new SysUser();
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
    public XJsonInfo findPage(SysUser vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(sysuserService.findPage(vo));
    }

	
}
