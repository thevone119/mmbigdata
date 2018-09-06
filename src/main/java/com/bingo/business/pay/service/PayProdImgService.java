package com.bingo.business.pay.service;

import com.bingo.business.pay.model.PayProd;
import com.bingo.business.pay.model.PayProdImg;
import com.bingo.business.pay.repository.PayProdImgRepository;
import com.bingo.business.pay.repository.PayProdRepository;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author huangtw
 * 2018-07-09 09:34:03
 * 对象功能:  service管理
 */
@Service
@Transactional
public class PayProdImgService {
	
	@Resource
	private PayProdImgRepository payProdImgRepository;



	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(PayProdImg vo) throws ServiceException, DaoException {
		payProdImgRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public PayProdImg get(Serializable id) throws DaoException{
		return payProdImgRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		payProdImgRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<PayProdImg> findPage(PayProdImg vo){
		StringBuffer hql = new StringBuffer(" from PayProdImg where cid is not null ");
		List<Object> fldValues = new ArrayList<Object>();


		if(vo.getImgPrice()!=null && vo.getImgPrice()>0){
			hql.append(" and imgPrice <= ? and imgPrice >= ? ");
			fldValues.add(vo.getImgPrice());
			fldValues.add(vo.getImgPrice()-0.5f);
		}
		if(vo.getUserId()!=null){
			hql.append(" and userId = ?");
			fldValues.add(vo.getUserId());
		}

		if(vo.getPayType()!=null&& vo.getPayType()>0){
			hql.append(" and payType = ?");
			fldValues.add(vo.getPayType());
		}

		return payProdImgRepository.findPage(hql.toString(), vo, fldValues);
	}

	/**
	 * 根据用户，价格,支付类型查询可用支付二维码
	 * @param userId
	 * @param imgPrice
	 * @return
	 */
	public List<PayProdImg> listByPrice(Long userId,Float imgPrice,Integer payType){
		StringBuffer hql = new StringBuffer(" from PayProdImg where userId=? and  imgPrice <= ? and imgPrice >= ? and payType=? order by imgPrice desc");
		return payProdImgRepository.query(hql.toString(),new Object[]{userId,imgPrice,imgPrice-0.5f,payType});
	}

	/**
	 * 查询空闲，可用的定额收款码
	 * @param userId
	 * @param imgPrice
	 * @param payType
	 * @return
	 */
	public List<PayProdImg> listFreeByPrice(Long userId,Float imgPrice,Integer payType,Integer payTimeOut){
		StringBuffer hql = new StringBuffer(" from PayProdImg img where userId=? and  imgPrice <= ? and imgPrice >= ? and payType=? and imgPrice not in(select payImgPrice from  PayLog log where busId=? and updatetime>? and payType=? and  payState!=1 ) order by imgPrice desc");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar cal = Calendar.getInstance();
		//锁多一分钟
		cal.add(Calendar.MINUTE,-payTimeOut-1);
		String validtime = format.format(cal.getTime());
		return payProdImgRepository.query(hql.toString(),new Object[]{userId,imgPrice,imgPrice-0.5f,payType,userId,validtime,payType});
	}

	/**
	 *根据用户，价格,支付类型查询可用支付二维码
	 * @param userId
	 * @param imgPrice
	 * @param payType
	 * @return
	 */
	public PayProdImg findByPriceAndType(Long userId,Float imgPrice,Integer payType){
		StringBuffer hql = new StringBuffer(" from PayProdImg where userId=? and imgPrice = ? and payType=? ");
		return payProdImgRepository.find(hql.toString(),new Object[]{userId,imgPrice,payType});
	}

	/**
	 * 根据支付链接查询
	 * @param imgContent
	 * @return
	 */
	public PayProdImg findByImgContent(String imgContent){
		StringBuffer hql = new StringBuffer(" from PayProdImg where imgContent=?  ");
		return payProdImgRepository.find(hql.toString(),new Object[]{imgContent});
	}

	
}
