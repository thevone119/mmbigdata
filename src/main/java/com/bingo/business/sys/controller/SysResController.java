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

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangtw
 * 2018-06-25 00:31:29
 * 对象功能: 系统资源 Controller管理
 */
@RestController
@RequestMapping("/api/sys/sysres")
public class SysResController  {
	
	@Resource
    private PubClass pubClass;
    
	@Resource
	private SysResService sysresService;

	public SysResController(){
		
	}


	/**
	 * 查询根菜单
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/listByPid")
	@ResponseBody
	public List<SysRes> listByPid(Long pid) throws Exception{
		if (pid==null){
			pid=0L;
		}
		List<SysRes> nodeList =sysresService.queryByPid(pid);
		return nodeList;
	}

	/**
	 * 删除某些菜单下的子菜单
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/deleteByPid")
	@ResponseBody
	public XJsonInfo deleteByPid(Long pid) throws Exception{
		sysresService.deleteByPid(pid);
		return new XJsonInfo();
	}


	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(SysRes vo) throws ServiceException, DaoException {
		SysRes res = sysresService.saveOrUpdate(vo);

		XJsonInfo ret = new XJsonInfo();
		//把ID返回
		ret.setData(res.getResid());
        return ret;
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
			sysresService.delete(new Long(id));
			//同时删除此节点下的子节点
			sysresService.deleteByPid(new Long(id));
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
        SysRes vo = sysresService.get(id);
        if(vo==null){
            vo = new SysRes();
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
    public XJsonInfo findPage(SysRes vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(sysresService.findPage(vo));
    }

	
}
