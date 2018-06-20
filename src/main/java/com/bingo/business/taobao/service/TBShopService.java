package com.bingo.business.taobao.service;

import com.bingo.business.taobao.crawler.TBShopSearchByProdCrawer;
import com.bingo.business.taobao.crawler.TBShopSearchCrawer;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bingo.business.taobao.model.*;
import com.bingo.business.taobao.repository.*;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;

/**
 * @author huangtw
 * 2018-04-03 22:46:01
 * 对象功能: 淘宝商家 service管理
 */
@Service
@Transactional
public class TBShopService{
	
	@Resource
	private TBShopRepository tbshopRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(TBShop vo) throws ServiceException, DaoException {
		tbshopRepository.saveOrUpdate(vo);
	}

	/**
	 * 只更新不为空的值
	 * @param vo
	 * @throws ServiceException
	 * @throws DaoException
	 */
	public void UpdateNoNull(TBShop vo) throws ServiceException, DaoException {
		TBShop shop = tbshopRepository.getById(vo.getShopid());
		if(shop==null){
			tbshopRepository.saveOrUpdate(vo);
			return;
		}
		try{
			if(shop.copyPropertiesIgnoreNull(vo)){
				tbshopRepository.saveOrUpdate(shop);
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
	public TBShop get(Serializable id) throws DaoException{
		return tbshopRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		tbshopRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<TBShop> findPage(TBShop vo){
		StringBuffer hql = new StringBuffer(" from TBShop where shopid is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		/**
		if(StringUtils.isNotEmpty(vo.getUserAccount())){
			hql.append(" and userAccount = ?");
			fldValues.add(vo.getUserAccount());
		}
		**/
		hql.append(" order by updateTime desc");
		return tbshopRepository.findPage(hql.toString(), vo, fldValues);
	}


	public Page<TBShop> queryByCreateTimeNull(TBShop vo){
		StringBuffer hql = new StringBuffer(" from TBShop where shopCreatetime is  null ");
		List<Object> fldValues = new ArrayList<Object>();
		return tbshopRepository.findPage(hql.toString(), vo, fldValues);
	}

	public Page<TBShop> queryBySellerCreditNull(TBShop vo){
		StringBuffer hql = new StringBuffer(" from TBShop where sellerCredit is  null ");
		List<Object> fldValues = new ArrayList<Object>();
		return tbshopRepository.findPage(hql.toString(), vo, fldValues);
	}

	public List<TBShop> queryByNull(String attribute,int max ){
		return queryByNull(attribute,max,null);
	}
	/**
	 * 查询某个字段为空的记录，一次返回100条
	 * @param attribute
	 * @return
	 */
	public List<TBShop> queryByNull(String attribute,int max ,String orderby){
		StringBuffer hql = new StringBuffer(" from TBShop where " + attribute + " is  null ");
		if(orderby!=null){
			hql.append("order by " + orderby );
		}
		TBShop vo = new TBShop();
		vo.setTotalCount(10000);
		//一次读取100条
		vo.setPageSize(max);
		vo.setPageNo(1);
		List<Object> fldValues = new ArrayList<Object>();
		Page<TBShop> page = tbshopRepository.findPage(hql.toString(), vo, fldValues);
		return page.getResult();
	}


	public TBShop queryByUserRateUrl(String userRateUrl){
		StringBuffer hql = new StringBuffer(" from TBShop where userRateUrl=?");
		return tbshopRepository.find(hql.toString(),new String[]{userRateUrl});
	}


	@Async("taskHttpCrawer01")
	public void TBShopSearch(String key) throws Exception {
		TBShopSearchCrawer tbs = new TBShopSearchCrawer(key);
		tbs.start();
	}

	@Async("taskHttpCrawer10")
	public void TBShopSearchByProd(String key) throws Exception {
		TBShopSearchByProdCrawer tbs = new TBShopSearchByProdCrawer(key);
		tbs.start();
	}


	
}
