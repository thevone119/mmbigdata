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
 * 2018-04-03 22:48:02
 * 对象功能: 淘宝商品月数据 service管理
 */
@Service
@Transactional
public class TBShopProdDataService{
	
	@Resource
	private TBShopProdDataRepository tbshopproddataRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(TBShopProdData vo) throws ServiceException, DaoException {
		tbshopproddataRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public TBShopProdData get(Serializable id) throws DaoException{
		return tbshopproddataRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		tbshopproddataRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<TBShopProdData> findPage(TBShopProdData vo){
		StringBuffer hql = new StringBuffer(" from TBShopProdData where keyid is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		/**
		if(StringUtils.isNotEmpty(vo.getUserAccount())){
			hql.append(" and userAccount = ?");
			fldValues.add(vo.getUserAccount());
		}
		**/
		return tbshopproddataRepository.findPage(hql.toString(), vo, fldValues);
	}
	
}
