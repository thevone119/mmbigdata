package com.bingo.business.pay.service;

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

import com.bingo.business.pay.model.*;
import com.bingo.business.pay.repository.*;

/**
 * @author huangtw
 * 2019-04-18 20:13:29
 * 对象功能: 收款子账号 service管理
 */
@Service
@Transactional
public class PaySubAccountService{
	
	@Resource
	private PaySubAccountRepository paysubaccountRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(PaySubAccount vo) throws ServiceException, DaoException {
		paysubaccountRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public PaySubAccount get(Serializable id) throws DaoException{
		return paysubaccountRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		paysubaccountRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<PaySubAccount> findPage(PaySubAccount vo){
		StringBuffer hql = new StringBuffer(" from PaySubAccount where sid is not null ");
		List<Object> fldValues = new ArrayList<Object>();


		if( vo.getBusId()>0){
			hql.append(" and busId = ?");
			fldValues.add(vo.getBusId());
		}

		if(vo.getState()>=0){
			hql.append(" and state = ?");
			fldValues.add(vo.getState());
		}

		if(StringUtils.isNotEmpty(vo.getSubaccount())){
			hql.append(" and subaccount = ?");
			fldValues.add(vo.getSubaccount());
		}


		return paysubaccountRepository.findPage(hql.toString(), vo, fldValues);
	}

	/**
	 * 查询所有有效的子账号
	 * 根据子账号查找
	 * @param userId
	 * @param payType
	 * @return
	 */
	public List<PaySubAccount> listValidSubAccount(Long userId,Integer payType){
		StringBuffer hql = new StringBuffer(" from PaySubAccount  where busId=? and payType=? and state=1 ");
		return paysubaccountRepository.query(hql.toString(),new Object[]{userId,payType});
	}


	
}
