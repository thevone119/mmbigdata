package com.bingo.business.sys.controller;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
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
		if(vo.getUsername()==null || vo.getUsername().length()<5){
			return new XJsonInfo(false,"用户账号错误");
		}
		if(vo.getNikename()==null || vo.getNikename().length()<2){
			return new XJsonInfo(false,"请输入用户昵称");
		}


		SysUser _vo = sysuserService.get(vo.getUserid());
		if(vo.getPwd()!=null && vo.getPwd().length()>5){
			vo.setPwd(SecurityClass.encryptMD5(vo.getPwd()));
		}
		if(_vo!=null){
			if(vo.getPwd()==null || vo.getPwd().length()<6){
				vo.setPwd(_vo.getPwd());
			}
		}
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
		SysUser t = new SysUser();
		BeanUtils.copyProperties(vo,t);
		t.setPwd(null);

        return new XJsonInfo().setData(t);
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
