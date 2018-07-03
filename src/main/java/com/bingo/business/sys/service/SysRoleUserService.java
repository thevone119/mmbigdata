package com.bingo.business.sys.service;

import com.bingo.business.sys.model.SysRoleRes;
import com.bingo.business.sys.model.SysRoleUser;
import com.bingo.business.sys.repository.SysRoleResRepository;
import com.bingo.business.sys.repository.SysRoleUserRepository;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
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
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		sysRoleUserRepository.deleteById(id);
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
	
}
