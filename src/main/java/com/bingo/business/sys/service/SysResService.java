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
 * 2018-06-25 00:31:29
 * 对象功能: 系统资源 service管理
 */
@Service
@Transactional
public class SysResService{
	
	@Resource
	private SysResRepository sysresRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public SysRes saveOrUpdate(SysRes vo) throws ServiceException, DaoException {
		return sysresRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public SysRes get(Serializable id) throws DaoException{
		return sysresRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		sysresRepository.deleteById(id);
	}

	/**
	 * 根据父节点获取列表
	 * @param pid
	 * @return
	 * @throws DaoException
	 */
	public List<SysRes> queryByPid(Long pid) throws DaoException{
		String hql  = "from SysRes where presid=?";
		return sysresRepository.query(hql,new Long[]{pid});
	}

	/**
	 * 删除父节点下的子节点
	 * @param pid
	 */
	public void deleteByPid(Long pid){
		String hql  = "delete from SysRes where presid=?";
		sysresRepository.executeByHql(hql,new Long[]{pid});
	}

	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<SysRes> findPage(SysRes vo){
		StringBuffer hql = new StringBuffer(" from SysRes where resid is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		/**
		if(StringUtils.isNotEmpty(vo.getUserAccount())){
			hql.append(" and userAccount = ?");
			fldValues.add(vo.getUserAccount());
		}
		**/
		return sysresRepository.findPage(hql.toString(), vo, fldValues);
	}
	
}
