package com.bingo.business.map.service;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
import com.sun.org.apache.xpath.internal.operations.String;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bingo.business.map.model.*;
import com.bingo.business.map.repository.*;

/**
 * @author huangtw
 * 2018-03-27 09:49:29
 * 对象功能: 坐标转换数据 service管理
 */
@Service
@Transactional
public class MapPointChangeService{
	
	@Resource
	private MapPointChangeRepository mappointchangeRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(MapPointChange vo) throws ServiceException, DaoException {
		mappointchangeRepository.saveOrUpdate(vo);
	}

	/**
	 * 保存对象,新增，不能更新
	 * @param vo
	 * @throws ServiceException
	 * @throws DaoException
	 */
	public void save(MapPointChange vo) throws ServiceException, DaoException {
		mappointchangeRepository.save(vo);
	}

	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public MapPointChange get(Serializable id) throws DaoException{
		return mappointchangeRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		mappointchangeRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<MapPointChange> findPage(MapPointChange vo){
		StringBuffer hql = new StringBuffer(" from MapPointChange where id is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		/**
		if(StringUtils.isNotEmpty(vo.getUserAccount())){
			hql.append(" and userAccount = ?");
			fldValues.add(vo.getUserAccount());
		}
		**/
		return mappointchangeRepository.findPage(hql.toString(), vo, fldValues);
	}

	/**
	 * 查询最相近的点
	 * 查询X,Y均小于0.05以内的点，然后进行计算最小的点
	 * @return
	 */
	public MapPointChange queryNearPoint(MapPointChange vo){
		StringBuffer hql = new StringBuffer(" from MapPointChange where id is not null ");
		List<Object> fldValues = new ArrayList<Object>();

		//blat
		if(vo.getBlat()>0){
			hql.append(" and blat-"+vo.getBlat()+" <0.05 ");
		}
		if(vo.getBlng()>0){
			hql.append(" and blng-"+vo.getBlng()+" <0.05 ");
		}

		//glat
		if(vo.getGlat()>0){
			hql.append(" and glat-"+vo.getGlat()+" <0.05 ");
		}
		//glng
		if(vo.getGlng()>0){
			hql.append(" and glng-"+vo.getGlng()+" <0.05 ");
		}

		List<MapPointChange> list =  (List<MapPointChange>)mappointchangeRepository.findPage(hql.toString(), vo, fldValues);
		if(list==null || list.size()==0){
			return null;
		}
		if(list.size()==1){
			return list.get(0);
		}
		//





		return list.get(0);
	}
	
}
