package com.bingo.business.sys.controller;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.filter.ControllerFilter;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import com.bingo.business.sys.model.*;
import com.bingo.business.sys.service.*;

import java.util.List;

/**
 * @author huangtw
 * 2018-06-25 00:30:49
 * 对象功能: 角色资源关联 Controller管理
 */
@RestController
@RequestMapping("/api/sys/sysroleres")
@ControllerFilter(LoginType = 1,UserType = 1)
public class SysRoleResController  {
	
	@Resource
    private PubClass pubClass;
    
	@Resource
	private SysRoleResService sysroleresService;

	public SysRoleResController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(SysRoleRes vo) throws ServiceException, DaoException {
        sysroleresService.saveOrUpdate(vo);
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
			sysroleresService.delete(new Long(id));
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
        SysRoleRes vo = sysroleresService.get(id);
        if(vo==null){
            vo = new SysRoleRes();
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
    public XJsonInfo findPage(SysRoleRes vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(sysroleresService.findPage(vo));
    }

	/**
	 * @description: <查询>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
	@RequestMapping("/queryByRole")
	public List<SysRoleRes> queryByRole(Long roleid) throws ServiceException, DaoException {
		List<SysRoleRes> list = sysroleresService.queryByRole(roleid);
		return list;
	}


	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
	@RequestMapping("/save2")
	public XJsonInfo save2(Long roleid,String  checkids) throws ServiceException, DaoException {
		sysroleresService.deleteByRole(roleid);
		String[] resids = checkids.split(";");
		for(String resid:resids){
			if(resid==null||resid.length()<=0){
				continue;
			}
			SysRoleRes r = new SysRoleRes();
			r.setRoleid(roleid);
			r.setResid(new Long(resid));
			sysroleresService.saveOrUpdate(r);
		}
		return new XJsonInfo();
	}




}
