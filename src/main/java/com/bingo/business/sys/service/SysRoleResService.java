package com.bingo.business.sys.service;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
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
 * 2018-06-25 00:30:49
 * 对象功能: ç³»ç»è§è²èµæºå³è service管理
 */
@Service
@Transactional
public class SysRoleResService{
	
	@Resource
	private SysRoleResRepository sysroleresRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(SysRoleRes vo) throws ServiceException, DaoException {
		sysroleresRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public SysRoleRes get(Serializable id) throws DaoException{
		return sysroleresRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		sysroleresRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<SysRoleRes> findPage(SysRoleRes vo){
		StringBuffer hql = new StringBuffer(" from SysRoleRes where sid is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		/**
		if(StringUtils.isNotEmpty(vo.getUserAccount())){
			hql.append(" and userAccount = ?");
			fldValues.add(vo.getUserAccount());
		}
		**/
		return sysroleresRepository.findPage(hql.toString(), vo, fldValues);
	}
	
}
