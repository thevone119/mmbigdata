package com.bingo.business.map.service;

import com.bingo.business.map.model.MapCfgLayer;
import com.bingo.business.map.repository.MapCfgLayerRepository;
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

/**
 * @author huangtw
 * 2017-08-24 19:07:18
 * 对象功能: 参数配置表 service管理
 */
@Service
@Transactional
public class MapCfgLayerService {

	@Resource
	private MapCfgLayerRepository mapCfgLayerRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException
	 * @throws:
	 */
	public void saveOrUpdate(MapCfgLayer vo) throws ServiceException, DaoException {
		mapCfgLayerRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public MapCfgLayer get(Serializable id) throws DaoException {
		return mapCfgLayerRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException {
		mapCfgLayerRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<MapCfgLayer> findPage(MapCfgLayer vo){
		StringBuffer hql = new StringBuffer(" from MapCfgLayer where id is not null ");
		List<Object> fldValues = new ArrayList<Object>();


		if(StringUtils.isNotEmpty(vo.getName())){
			hql.append(" and name like ?");
			fldValues.add("%"+vo.getName()+"%");
		}
		return mapCfgLayerRepository.findPage(hql.toString(), vo, fldValues);
	}


	public List<MapCfgLayer> queryList(MapCfgLayer vo){
		StringBuffer hql = new StringBuffer(" from MapCfgLayer where id is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		if(StringUtils.isNotEmpty(vo.getName())){
			hql.append(" and name like ?");
			fldValues.add("%"+vo.getName()+"%");
		}
		return mapCfgLayerRepository.query(hql.toString(), fldValues.toArray());
	}


	
}
