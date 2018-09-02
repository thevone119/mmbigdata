package com.bingo.business.pay.service;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bingo.business.pay.model.*;
import com.bingo.business.pay.repository.*;

/**
 * @author huangtw
 * 2018-09-02 19:36:28
 * 对象功能:  service管理
 */
@Service
@Transactional
public class PayProdLogService{
	
	@Resource
	private PayProdLogRepository payprodlogRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(PayProdLog vo) throws ServiceException, DaoException {
		payprodlogRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public PayProdLog get(Serializable id) throws DaoException{
		return payprodlogRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		payprodlogRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<PayProdLog> findPage(PayProdLog vo){
		StringBuffer hql = new StringBuffer(" from PayProdLog where orderid is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		/**
		if(StringUtils.isNotEmpty(vo.getUserAccount())){
			hql.append(" and userAccount = ?");
			fldValues.add(vo.getUserAccount());
		}
		**/
		return payprodlogRepository.findPage(hql.toString(), vo, fldValues);
	}
	
}
