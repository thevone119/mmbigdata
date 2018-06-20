package com.bingo.business.map.controller;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.XJsonInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.bingo.business.map.model.*;
import com.bingo.business.map.service.*;

/**
 * @author huangtw
 * 2018-03-13 14:36:36
 * 对象功能: 图层表 Controller管理
 */
@RestController
@RequestMapping("/api/map/mapcfglayer")
public class MapCfgLayerController  {
	
	@Resource
    private PubClass pubClass;
    
	@Resource
	private MapCfgLayerService mapcfglayerService;

	public MapCfgLayerController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(MapCfgLayer vo) throws ServiceException, DaoException {
        mapcfglayerService.saveOrUpdate(vo);
        return new XJsonInfo();
    }

	/**
	 * @description: <删除>
	 * @param:
	 * @throws:
	 */
    @ResponseBody
    @RequestMapping("/delete")
    public XJsonInfo delete(String[] selRows) throws ServiceException, DaoException {
		for(String id:selRows){
			mapcfglayerService.delete(new Long(id));
		}
        return new XJsonInfo();
    }

	/**
	 * @description: <查询>
	 * @param:
	 * @throws:
	 */
    @ResponseBody
    @RequestMapping("/query")
    public XJsonInfo query(Long id) throws ServiceException, DaoException {
        MapCfgLayer vo = mapcfglayerService.get(id);
        if(vo==null){
            vo = new MapCfgLayer();
        }
        return new XJsonInfo().setData(vo);
    }

	/**
	 * @description: <分页查询>
	 * @param:
	 * @throws:
	 */
    @ResponseBody
    @RequestMapping("/findPage")
    public XJsonInfo findPage(MapCfgLayer vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(mapcfglayerService.findPage(vo));
    }

	
}
