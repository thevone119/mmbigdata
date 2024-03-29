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
 * 2018-07-09 09:34:03
 * 对象功能:  service管理
 */
@Service
@Transactional
public class PayProdService{
	
	@Resource
	private PayProdRepository payprodRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(PayProd vo) throws ServiceException, DaoException {
		payprodRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public PayProd get(Serializable id) throws DaoException{
		return payprodRepository.getById(id);
	}

	/**
	 * 根据rid查询快捷支付商品
	 * @param rid
	 * @return
	 * @throws DaoException
	 */
	public PayProd getByRid(String rid) throws DaoException{
		return  payprodRepository.find(" from PayProd where rid =? ",new String[]{rid});
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		payprodRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<PayProd> findPage(PayProd vo){
		StringBuffer hql = new StringBuffer(" from PayProd where prodId is not null ");
		List<Object> fldValues = new ArrayList<Object>();

		if(StringUtils.isNotEmpty(vo.getProdName())){
			hql.append(" and prodName like ?");
			fldValues.add("%"+vo.getProdName()+"%");
		}

		if(vo.getBusId()!=null){
			hql.append(" and busId = ?");
			fldValues.add(vo.getBusId());
		}


		if(vo.getState()>=0){
			hql.append(" and state = ?");
			fldValues.add(vo.getState());
		}
		if(vo.getProdPrice()!=null && vo.getProdPrice()>=0){
			hql.append(" and prodPrice = ?");
			fldValues.add(vo.getProdPrice());
		}




		return payprodRepository.findPage(hql.toString(), vo, fldValues);
	}



	
}
