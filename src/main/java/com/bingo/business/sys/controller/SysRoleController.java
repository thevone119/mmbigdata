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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huangtw
 * 2018-06-25 00:31:07
 * 对象功能: ç³»ç»è§è² Controller管理
 */
@RestController
@RequestMapping("/api/sys/sysrole")
public class SysRoleController  {
	
	@Resource
    private PubClass pubClass;
    
	@Resource
	private SysRoleService sysroleService;

	public SysRoleController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(SysRole vo) throws ServiceException, DaoException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		vo.setCreatetime(format.format(new Date()));
		vo.setUpdatetime(format.format(new Date()));

        sysroleService.saveOrUpdate(vo);
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
			sysroleService.delete(new Long(id));
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
        SysRole vo = sysroleService.get(id);
        if(vo==null){
            vo = new SysRole();
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
    public XJsonInfo findPage(SysRole vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(sysroleService.findPage(vo));
    }

	
}
