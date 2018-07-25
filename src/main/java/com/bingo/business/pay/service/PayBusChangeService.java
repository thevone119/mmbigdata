package com.bingo.business.pay.service;

import com.bingo.business.pay.model.PayBus;
import com.bingo.business.pay.model.PayBusChange;
import com.bingo.business.pay.repository.PayBusChangeRepository;
import com.bingo.business.pay.repository.PayBusRepository;
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
 * 2018-07-09 09:33:38
 * 对象功能:  service管理
 */
@Service
@Transactional
public class PayBusChangeService {
	
	@Resource
	private PayBusChangeRepository payBusChangeRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(PayBusChange vo) throws ServiceException, DaoException {
		payBusChangeRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public PayBusChange get(Serializable id) throws DaoException{
		return payBusChangeRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		payBusChangeRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<PayBusChange> findPage(PayBusChange vo){
		StringBuffer hql = new StringBuffer(" from PayBusChange where cid is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		/**
		if(StringUtils.isNotEmpty(vo.getUserAccount())){
			hql.append(" and userAccount = ?");
			fldValues.add(vo.getUserAccount());
		}
		**/
		if(vo.getBusId()!=null){
			hql.append(" and busId = ?");
			fldValues.add(vo.getBusId());
		}

		if(vo.getCtype()!=null&&vo.getCtype()>0){
			hql.append(" and ctype = ?");
			fldValues.add(vo.getCtype());
		}

		if(vo.getCreatetime()!=null&&vo.getCreatetime().length()==6){
			hql.append(" and createtime like ?");
			fldValues.add("%"+vo.getCreatetime()+"%");
		}
		return payBusChangeRepository.findPage(hql.toString(), vo, fldValues);
	}
	
}
