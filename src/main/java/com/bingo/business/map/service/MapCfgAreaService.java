package com.bingo.business.map.service;

import com.bingo.business.map.model.MapCfgArea;
import com.bingo.business.map.model.MapCfgLayer;
import com.bingo.business.map.repository.MapCfgAreaRepository;
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
public class MapCfgAreaService {

	@Resource
	private MapCfgAreaRepository mapCfgAreaRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException
	 * @throws:
	 */
	public void saveOrUpdate(MapCfgArea vo) throws ServiceException, DaoException {
		mapCfgAreaRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public MapCfgArea get(Serializable id) throws DaoException {
		return mapCfgAreaRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException {
		mapCfgAreaRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<MapCfgArea> findPage(MapCfgArea vo){
		StringBuffer hql = new StringBuffer(" from MapCfgArea where id is not null ");
		List<Object> fldValues = new ArrayList<Object>();

		if(vo.getLayerId()!=null && vo.getLayerId()>0){
			hql.append(" and layerId = ?");
			fldValues.add(vo.getLayerId());
		}

		if(StringUtils.isNotEmpty(vo.getName())){
			hql.append(" and name like ?");
			fldValues.add("%"+vo.getName()+"%");
		}
		return mapCfgAreaRepository.findPage(hql.toString(), vo, fldValues);
	}

	public MapCfgArea queryByName(String name){
		StringBuffer hql = new StringBuffer(" from MapCfgArea where name =? ");
		return mapCfgAreaRepository.find(hql.toString(),new String[]{name});
	}

	/**
	 * 查询列表
	 * @param vo
	 * @return
	 */
	public List<MapCfgArea> queryList(MapCfgArea vo){
		StringBuffer hql = new StringBuffer(" from MapCfgArea where id is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		if(StringUtils.isNotEmpty(vo.getName())){
			hql.append(" and name like ?");
			fldValues.add("%"+vo.getName()+"%");
		}
		return mapCfgAreaRepository.query(hql.toString(), fldValues.toArray());
	}

	
}
