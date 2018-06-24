package com.bingo.business.sys.controller;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import com.bingo.business.sys.model.*;
import com.bingo.business.sys.service.*;

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
