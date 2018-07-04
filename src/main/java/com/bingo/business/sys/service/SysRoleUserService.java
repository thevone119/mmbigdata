package com.bingo.business.sys.service;

import com.bingo.business.sys.model.SysRole;
import com.bingo.business.sys.model.SysRoleRes;
import com.bingo.business.sys.model.SysRoleUser;
import com.bingo.business.sys.model.SysUser;
import com.bingo.business.sys.repository.*;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangtw
 * 2018-06-25 00:30:49
 * 对象功能: 角色用户关联 service管理
 */
@Service
@Transactional
public class SysRoleUserService {
	
	@Resource
	private SysRoleUserRepository sysRoleUserRepository;

	@Resource
	private SysUserRepository sysuserRepository;

	@Resource
	private SysRoleRepository sysroleRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(SysRoleUser vo) throws ServiceException, DaoException {
		sysRoleUserRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public SysRoleUser get(Serializable id) throws DaoException{
		return sysRoleUserRepository.getById(id);
	}

	/**
	 * 根据角色ID，用户ID查询对象
	 * @description: <取对象>
	 * @return
	 * @throws DaoException
	 */
	public SysRoleUser query(Long roleid,Long userid) throws DaoException{
		String hql = "from SysRoleUser where roleid=? and userid=?";
		return sysRoleUserRepository.find(hql,new Long[]{roleid,userid});
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
			 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		sysRoleUserRepository.deleteById(id);
	}

	/**
	 * 根据角色ID和用户ID删除
	 * @param roleid
	 * @param userid
	 * @throws DaoException
	 */
	public void delete(Long roleid,Long userid) throws DaoException{
		String hql = new String(" delete from SysRoleUser where roleid=? and userid=? ");
		sysRoleUserRepository.executeByHql(hql,new Long[]{roleid,userid});
	}



	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<SysRoleUser> findPage(SysRoleRes vo){
		StringBuffer hql = new StringBuffer(" from SysRoleUser where sid is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		/**
		if(StringUtils.isNotEmpty(vo.getUserAccount())){
			hql.append(" and userAccount = ?");
			fldValues.add(vo.getUserAccount());
		}
		**/
		return sysRoleUserRepository.findPage(hql.toString(), vo, fldValues);
	}



	/**
	 * 查询某个角色下的用户，分页查询
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<SysUser> findPageByRole(Long roleid,SysUser vo){
		StringBuffer hql = new StringBuffer(" from SysUser where userid in(select userid from SysRoleUser where roleid=?) ");
		List<Object> fldValues = new ArrayList<Object>();
		fldValues.add(roleid);
		if(StringUtils.isNotEmpty(vo.getUsername())){
			hql.append(" and username like ?");
			fldValues.add("%"+vo.getUsername()+"%");
		}
		if(StringUtils.isNotEmpty(vo.getNikename())){
			hql.append(" and nikename like ?");
			fldValues.add("%"+vo.getNikename()+"%");
		}
		if(vo.getUsertype()!=null && vo.getUsertype()!=-1){
			hql.append(" and usertype = ?");
			fldValues.add(vo.getUsertype());
		}
		if(vo.getState()!=null && vo.getState()!=-1){
			hql.append(" and state = ?");
			fldValues.add(vo.getState());
		}
		//密码不返回
		Page<SysUser> page = sysuserRepository.findPage(hql.toString(), vo, fldValues);
		if(page!=null && page.getTotalCount()>0){
			for(SysUser user : page.getResult()){
				//user.setPwd(null);
			}
		}
		return page;
	}


	/**
	 * 查询某个用户下的角色，根据用户ID查询
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<SysRole> findPageByUser(Long userid,SysRole vo){
		StringBuffer hql = new StringBuffer(" from SysRole where roleid in(select roleid from SysRoleUser where userid=?) ");
		List<Object> fldValues = new ArrayList<Object>();
		fldValues.add(userid);
		if(StringUtils.isNotEmpty(vo.getRolename())){
			hql.append(" and rolename like ?");
			fldValues.add("%"+vo.getRolename()+"%");
		}
		if(StringUtils.isNotEmpty(vo.getRolecode())){
			hql.append(" and rolecode like ?");
			fldValues.add("%"+vo.getRolecode()+"%");
		}
		if(vo.getRoletype()!=null && vo.getRoletype()!=-1){
			hql.append(" and roletype = ?");
			fldValues.add(vo.getRoletype());
		}
		if(vo.getRolestate()!=null && vo.getRolestate()!=-1){
			hql.append(" and rolestate = ?");
			fldValues.add(vo.getRolestate());
		}

		return sysroleRepository.findPage(hql.toString(), vo, fldValues);
	}
	
}
