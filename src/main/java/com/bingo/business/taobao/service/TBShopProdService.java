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
 * 2018-04-03 22:46:38
 * 对象功能: 淘宝商品 service管理
 */
@Service
@Transactional
public class TBShopProdService{
	
	@Resource
	private TBShopProdRepository tbshopprodRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(TBShopProd vo) throws ServiceException, DaoException {
		tbshopprodRepository.saveOrUpdate(vo);
	}

	/**
	 * 只更新不为空的值
	 * @param vo
	 * @throws ServiceException
	 * @throws DaoException
	 */
	public void UpdateNoNull(TBShopProd vo) throws ServiceException, DaoException {
		TBShopProd prod = tbshopprodRepository.getById(vo.getProductId());
		if(prod==null){
			tbshopprodRepository.saveOrUpdate(vo);
			return;
		}
		try{
			if(prod.copyPropertiesIgnoreNull(vo)){
				tbshopprodRepository.saveOrUpdate(prod);
			}
		}catch (Exception e){

		}
	}

	/**
	 * 查询产品
	 * @param attribute
	 * @param orderby
	 * @return
	 */
	public List<TBShopProd> queryByNull(String attribute,int pageno,int pagesize ,String orderby){
		StringBuffer hql = new StringBuffer(" from TBShopProd where " + attribute + " is  null ");
		if(orderby!=null){
			hql.append("order by " + orderby );
		}
		TBShopProd vo = new TBShopProd();
		vo.setTotalCount(10000);
		//一次读取100条
		vo.setPageSize(pagesize);
		vo.setPageNo(pageno);
		List<Object> fldValues = new ArrayList<Object>();
		Page<TBShopProd> page = tbshopprodRepository.findPage(hql.toString(), vo, fldValues);
		return page.getResult();
	}

	public List<TBShopProd> queryByHql(String queryStr,int max){
		TBShopProd vo = new TBShopProd();
		vo.setTotalCount(10000);
		//一次读取100条
		vo.setPageSize(max);
		vo.setPageNo(1);
		List<Object> fldValues = new ArrayList<Object>();
		Page<TBShopProd> page = tbshopprodRepository.findPage(queryStr, vo, fldValues);
		return page.getResult();
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public TBShopProd get(Serializable id) throws DaoException{
		return tbshopprodRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		tbshopprodRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<TBShopProd> findPage(TBShopProd vo){
		StringBuffer hql = new StringBuffer(" from TBShopProd where productId is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		/**
		if(StringUtils.isNotEmpty(vo.getUserAccount())){
			hql.append(" and userAccount = ?");
			fldValues.add(vo.getUserAccount());
		}
		**/
		return tbshopprodRepository.findPage(hql.toString(), vo, fldValues);
	}
	
}
