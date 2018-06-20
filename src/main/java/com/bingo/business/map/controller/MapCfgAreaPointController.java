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
 * 2018-03-13 15:00:43
 * 对象功能: 区域坐标点 Controller管理
 */
@RestController
@RequestMapping("/api/map/mapcfgareapoint")
public class MapCfgAreaPointController  {
	
	@Resource
    private PubClass pubClass;
    
	@Resource
	private MapCfgAreaPointService mapcfgareapointService;

	public MapCfgAreaPointController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(MapCfgAreaPoint vo) throws ServiceException, DaoException {
        mapcfgareapointService.saveOrUpdate(vo);
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
			mapcfgareapointService.delete(new Long(id));
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
		MapCfgAreaPoint vo = mapcfgareapointService.get(id);
        if(vo==null){
            vo = new MapCfgAreaPoint();
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
    public XJsonInfo findPage(MapCfgAreaPoint vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(mapcfgareapointService.findPage(vo));
    }

	
}
