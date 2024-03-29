package com.bingo.business.sys.controller;

import com.bingo.business.sys.model.SysRole;
import com.bingo.business.sys.model.SysRoleRes;
import com.bingo.business.sys.model.SysRoleUser;
import com.bingo.business.sys.model.SysUser;
import com.bingo.business.sys.service.SysRoleResService;
import com.bingo.business.sys.service.SysRoleUserService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author huangtw
 * 2018-06-25 00:30:49
 * 对象功能: 角色用户关联 Controller管理
 */
@RestController
@RequestMapping("/api/sys/sysroleuser")
public class SysRoleUserController {

	@Resource
    private PubClass pubClass;

	@Resource
	private SysRoleUserService sysRoleUserService;

	public SysRoleUserController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(SysRoleUser vo) throws ServiceException, DaoException {
		sysRoleUserService.saveOrUpdate(vo);
        return new XJsonInfo();
    }


	@ResponseBody
	@RequestMapping("/save_role_user")
	public XJsonInfo save_role_user(Long roleid,String[] selRows) throws ServiceException, DaoException {
		for(String id:selRows){
			SysRoleUser r = sysRoleUserService.query(roleid,new Long(id));
			if(r!=null){
				return new XJsonInfo();
			}
			r = new SysRoleUser();
			r.setRoleid(roleid);
			r.setUserid(new Long(id));
			sysRoleUserService.saveOrUpdate(r);
		}
		return new XJsonInfo();
	}

	@ResponseBody
	@RequestMapping("/save_user_role")
	public XJsonInfo save_user_role(Long userid,String[] selRows) throws ServiceException, DaoException {
		for(String id:selRows){
			Long roleid = new Long(id);
			SysRoleUser r = sysRoleUserService.query(roleid,userid);
			if(r!=null){
				return new XJsonInfo();
			}
			r = new SysRoleUser();
			r.setRoleid(roleid);
			r.setUserid(userid);
			sysRoleUserService.saveOrUpdate(r);
		}
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
			sysRoleUserService.delete(new Long(id));
		}
        return new XJsonInfo();
    }

	@ResponseBody
	@RequestMapping("/delete_role_user")
	public XJsonInfo delete_role_user(Long roleid,String[] selRows) throws ServiceException, DaoException {
		for(String id:selRows){
			sysRoleUserService.delete(roleid,new Long(id));
		}
		return new XJsonInfo();
	}

	@ResponseBody
	@RequestMapping("/delete_user_role")
	public XJsonInfo delete_user_role(Long userid,String[] selRows) throws ServiceException, DaoException {
		for(String id:selRows){
			Long roleid = new Long(id);
			sysRoleUserService.delete(roleid,userid);
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
		SysRoleUser vo = sysRoleUserService.get(id);
        if(vo==null){
            vo = new SysRoleUser();
        }
        return new XJsonInfo().setData(vo);
    }

	/**
	 * 查询某个角色下的用户，分页查询
	 * @description: <分页查询>
	 * @param:
	 * @throws:
	 */
    @ResponseBody
    @RequestMapping("/findPageUser")
    public XJsonInfo findPageUser(Long roleid,SysUser vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(sysRoleUserService.findPageByRole(roleid,vo));
    }

	/**
	 * 查询某个用户下的角色
	 * @param userid
	 * @param vo
	 * @return
	 * @throws ServiceException
	 * @throws DaoException
	 */
	@ResponseBody
	@RequestMapping("/findPageRole")
	public XJsonInfo findPageRole(Long userid,SysRole vo) throws ServiceException, DaoException {
		return  new XJsonInfo().setPageData(sysRoleUserService.findPageByUser(userid,vo));
	}

	
}
