package com.bingo.business.sys.controller;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.filter.ControllerFilter;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.RedisCacheService;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.RandomImg;
import com.bingo.common.utility.SecurityClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.beans.BeanUtils;
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
@ControllerFilter(LoginType = 1,UserType = 1)
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
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(SysUser vo) throws ServiceException, DaoException {
		if(vo.getUseracc()==null || vo.getUseracc().length()<5){
			return new XJsonInfo(false,"用户账号错误");
		}
		if(vo.getNikename()==null || vo.getNikename().length()<2){
			return new XJsonInfo(false,"请输入用户昵称");
		}

		SysUser _vo = sysuserService.get(vo.getUserid());
		if(_vo==null){
			_vo = new SysUser();
		}
		//修改密码
		if(vo.getPwd()!=null && vo.getPwd().length()>5){
			_vo.setPwd(SecurityClass.encryptMD5(vo.getPwd()));
		}
		//修改用户名
		_vo.setUseracc(vo.getUseracc());
		_vo.setUsertype(vo.getUsertype());
		//_vo.setGobackUrl(vo.getGobackUrl());
		_vo.setNikename(vo.getNikename());
		_vo.setEmail(vo.getEmail());
		_vo.setMobile(vo.getMobile());
		//_vo.setNotifyUrl(vo.getNotifyUrl());
		_vo.setState(vo.getState());
        sysuserService.saveOrUpdate(_vo);
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
		SysUser t = new SysUser();
		BeanUtils.copyProperties(vo,t);
		t.setPwd(null);

        return new XJsonInfo().setData(t);
    }

	/**
	 * 获取当前的用户完整信息
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ControllerFilter(LoginType = 0)
	@ResponseBody
	@RequestMapping("/queryCurrUser")
	public XJsonInfo queryCurrUser() throws ServiceException, DaoException {
		XJsonInfo ret= new XJsonInfo(false);
		SessionUser loginuser= sessionCache.getLoginUser();
		if(loginuser==null){
			ret.setMsg("您还没登录，或登录超时，无法获取当前用户信息");
			return ret;
		}
		SysUser vo = sysuserService.get(loginuser.getUserid());
		if(vo==null){
			vo = new SysUser();
		}
		SysUser t = new SysUser();
		BeanUtils.copyProperties(vo,t);
		t.setPwd(null);
		return new XJsonInfo().setData(t);
	}

	/**
	 * 保存当前用户的设置
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ControllerFilter(LoginType = 0)
	@ResponseBody
	@RequestMapping("/saveCurrUser")
	public XJsonInfo saveCurrUser(SysUser vo) throws ServiceException, DaoException {
		XJsonInfo ret= new XJsonInfo(false);
		SessionUser loginuser= sessionCache.getLoginUser();
		if(loginuser==null){
			ret.setMsg("您还没登录，或登录超时，无法获取当前用户信息");
			return ret;
		}
		SysUser user = sysuserService.get(loginuser.getUserid());
		//修改用户名
		//user.setGobackUrl(vo.getGobackUrl());
		user.setNikename(vo.getNikename());
		user.setEmail(vo.getEmail());
		user.setMobile(vo.getMobile());
		//user.setNotifyUrl(vo.getNotifyUrl());
		user.setQq(vo.getQq());
		sysuserService.saveOrUpdate(user);

		return new XJsonInfo();
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
