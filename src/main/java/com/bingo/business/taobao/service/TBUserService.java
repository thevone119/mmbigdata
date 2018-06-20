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
 * 2018-04-07 23:45:50
 * 对象功能: 淘宝用户表 service管理
 */
@Service
@Transactional
public class TBUserService{
	
	@Resource
	private TBUserRepository tbuserRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(TBUser vo) throws ServiceException, DaoException {
		tbuserRepository.saveOrUpdate(vo);
	}

	/**
	 * 只更新不为空的值
	 * @param vo
	 * @throws ServiceException
	 * @throws DaoException
	 */
	public void UpdateNoNull(TBUser vo) throws ServiceException, DaoException {
		TBUser user = tbuserRepository.getById(vo.getUid());
		if(user==null){
			tbuserRepository.saveOrUpdate(vo);
			return;
		}
		try{
			if(user.copyPropertiesIgnoreNull(vo)){
				tbuserRepository.saveOrUpdate(user);
			}

		}catch (Exception e){

		}
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public TBUser get(Serializable id) throws DaoException{
		return tbuserRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		tbuserRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<TBUser> findPage(TBUser vo){
		StringBuffer hql = new StringBuffer(" from TBUser where uid is not null and uid not in(select uid from TBShop where uid is not null)");
		List<Object> fldValues = new ArrayList<Object>();
		/**
		if(StringUtils.isNotEmpty(vo.getUserAccount())){
			hql.append(" and userAccount = ?");
			fldValues.add(vo.getUserAccount());
		}
		**/
		return tbuserRepository.findPage(hql.toString(), vo, fldValues);
	}
	
}
