package com.bingo.business.sys.service;

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

import com.bingo.business.sys.model.*;
import com.bingo.business.sys.repository.*;

/**
 * @author huangtw
 * 2018-06-25 00:31:07
 * 对象功能: ç³»ç»è§è² service管理
 */
@Service
@Transactional
public class SysRoleService{
	
	@Resource
	private SysRoleRepository sysroleRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(SysRole vo) throws ServiceException, DaoException {
		sysroleRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public SysRole get(Serializable id) throws DaoException{
		return sysroleRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		sysroleRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<SysRole> findPage(SysRole vo){
		StringBuffer hql = new StringBuffer(" from SysRole where roleid is not null ");
		List<Object> fldValues = new ArrayList<Object>();

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
