package com.bingo.business.taobao.service;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bingo.business.taobao.model.*;
import com.bingo.business.taobao.repository.*;

/**
 * @author huangtw
 * 2018-04-03 22:44:45
 * 对象功能: 淘宝类目 service管理
 */
@Service
@Transactional
public class TBCategoryService{
	
	@Resource
	private TBCategoryRepository tbcategoryRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(TBCategory vo) throws ServiceException, DaoException {
		tbcategoryRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public TBCategory get(Serializable id) throws DaoException{
		return tbcategoryRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		tbcategoryRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<TBCategory> findPage(TBCategory vo){
		StringBuffer hql = new StringBuffer(" from TBCategory where cid is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		/**
		if(StringUtils.isNotEmpty(vo.getUserAccount())){
			hql.append(" and userAccount = ?");
			fldValues.add(vo.getUserAccount());
		}
		**/
		return tbcategoryRepository.findPage(hql.toString(), vo, fldValues);
	}
	
}
