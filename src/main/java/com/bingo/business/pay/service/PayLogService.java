package com.bingo.business.pay.service;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
import com.bingo.common.utility.XJsonInfo;
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
 * 2018-07-09 09:34:30
 * 对象功能:  service管理
 */
@Service
@Transactional
public class PayLogService{
	
	@Resource
	private PayLogRepository paylogRepository;




	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(PayLog vo) throws ServiceException, DaoException {
		paylogRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public PayLog get(Serializable id) throws DaoException{
		return paylogRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		paylogRepository.deleteById(id);
	}

	/**
	 * 根据rid查询支付订单
	 * @param rid
	 * @return
	 * @throws DaoException
	 */
	public PayLog queryByRid(String rid) throws DaoException{
		StringBuffer qhtl = new StringBuffer(" from PayLog where rid =? ");
		return paylogRepository.find(qhtl.toString(),new String[]{rid});
	}



	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<PayLog> findPage(PayLog vo){
		StringBuffer hql = new StringBuffer(" from PayLog where logId is not null ");
		List<Object> fldValues = new ArrayList<Object>();

		if(StringUtils.isNotEmpty(vo.getOrderid())){
			hql.append(" and orderid = ?");
			fldValues.add(vo.getOrderid());
		}

		if(vo.getBusId()!=null){
			hql.append(" and busId = ?");
			fldValues.add(vo.getBusId());
		}

		if(vo.getProdPrice()!=null&&vo.getProdPrice()>0){
			hql.append(" and prodPrice = ?");
			fldValues.add(vo.getProdPrice());
		}

		if(vo.getCreatetime()!=null&&vo.getCreatetime().length()==6){
			hql.append(" and createtime like ?");
			fldValues.add("%"+vo.getCreatetime()+"%");
		}

		if(vo.getPayType()!=null&&vo.getPayType()>0){
			hql.append(" and payType = ?");
			fldValues.add(vo.getPayType());
		}




		return paylogRepository.findPage(hql.toString(), vo, fldValues);
	}
	
}
