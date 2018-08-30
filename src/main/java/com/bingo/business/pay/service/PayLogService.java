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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

	public PayLog queryByRidUid(String rid,String uid) throws DaoException{
		StringBuffer qhtl = new StringBuffer(" from PayLog where rid =? and uid=? ");
		return paylogRepository.find(qhtl.toString(),new String[]{rid,uid});
	}

	/**
	 * 根据商户ID，和订单ID查询订单
	 * @param uid
	 * @param orderid
	 * @return
	 * @throws DaoException
	 */
	public PayLog queryByUidOrderid(String uid,String orderid) throws DaoException{
		StringBuffer qhtl = new StringBuffer(" from PayLog where uid =? and orderid=? ");
		return paylogRepository.find(qhtl.toString(),new String[]{uid,orderid});
	}

	/**
	 * 根据3个不同的单号查询订单
	 * @param logId
	 * @param orderid
	 * @param rid
	 * @return
	 * @throws DaoException
	 */
	public PayLog queryByAllid(Long logId,String orderid,String rid) throws DaoException{
		StringBuffer qhtl = new StringBuffer(" from PayLog where logId =? and orderid=? and rid=?");
		return paylogRepository.find(qhtl.toString(),new Object[]{logId,orderid,rid});
	}


	/**
	 * 查询正在使用的订单
	 * 根据商户，价格查询
	 * @return
	 */
	public List<PayLog> queryByUseingLog(String uid,Integer  payType,Integer payTimeOut,Float prodPrice){
		return queryByUseingLog(uid,payType,payTimeOut,prodPrice,null,null);
	}

	public List<PayLog> queryByUseingLog(String uid,Integer  payType,Integer payTimeOut,Float prodPrice,Float payImgPrice,Long currTime){
		StringBuffer qhtl = new StringBuffer(" from PayLog where uid =? and payType=? and updatetime>? and payState!=1 ");
		if(prodPrice!=null){
			qhtl.append(" and prodPrice="+prodPrice );
		}
		if(payImgPrice!=null){
			qhtl.append(" and payImgPrice="+payImgPrice );
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar cal = Calendar.getInstance();
		if(currTime!=null){
			cal.setTime(new Date(currTime));
			//APP确认收款时，锁多30秒
			cal.add(Calendar.SECOND,-(payTimeOut*60)-30);
		}else{
			//释放出来给别人使用时，锁多一分钟
			cal.add(Calendar.MINUTE,-payTimeOut-1);
		}

		String validtime = format.format(cal.getTime());
		return paylogRepository.query(qhtl.toString(),new Object[]{uid,payType,validtime});
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

		if(StringUtils.isNotEmpty(vo.getUid())){
			hql.append(" and uid = ?");
			fldValues.add(vo.getUid());
		}

		if(vo.getProdPrice()!=null&&vo.getProdPrice()>0){
			hql.append(" and prodPrice = ?");
			fldValues.add(vo.getProdPrice());
		}

		if(vo.getCreatetime()!=null&&vo.getCreatetime().length()>=4&&vo.getCreatetime().length()<=8){
			hql.append(" and createtime like ?");
			fldValues.add(""+vo.getCreatetime()+"%");
		}

		if(vo.getPayType()!=null&&vo.getPayType()>0){
			hql.append(" and payType = ?");
			fldValues.add(vo.getPayType());
		}

		if(vo.getPayState()!=null&&vo.getPayState()>=0){
			hql.append(" and payState = ?");
			fldValues.add(vo.getPayState());
		}




		return paylogRepository.findPage(hql.toString(), vo, fldValues);
	}
	
}
