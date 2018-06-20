package com.bingo.business.map.service;


import com.bingo.business.map.model.MapCfgAreaPoint;
import com.bingo.business.map.repository.MapCfgAreaPointRepository;
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
public class MapCfgAreaPointService {

	@Resource
	private MapCfgAreaPointRepository mapCfgAreaPointRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException
	 * @throws:
	 */
	public void saveOrUpdate(MapCfgAreaPoint vo) throws ServiceException, DaoException {
		mapCfgAreaPointRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public MapCfgAreaPoint get(Serializable id) throws DaoException {
		return mapCfgAreaPointRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException {
		mapCfgAreaPointRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<MapCfgAreaPoint> findPage(MapCfgAreaPoint vo){
		StringBuffer hql = new StringBuffer(" from MapCfgAreaPoint where id is not null ");
		List<Object> fldValues = new ArrayList<Object>();


		if(StringUtils.isNotEmpty(vo.getName())){
			hql.append(" and name like ?");
			fldValues.add("%"+vo.getName()+"%");
		}
		return mapCfgAreaPointRepository.findPage(hql.toString(), vo, fldValues);
	}

	
}
