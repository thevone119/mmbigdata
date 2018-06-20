package com.bingo.business.taobao.controller;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import com.bingo.business.taobao.model.*;
import com.bingo.business.taobao.service.*;

/**
 * @author huangtw
 * 2018-04-03 22:47:22
 * 对象功能: 淘宝商家月数据 Controller管理
 */
@RestController
@RequestMapping("/api/taobao/tbshopdata")
public class TBShopDataController  {
	
	@Resource
    private PubClass pubClass;
    
	@Resource
	private TBShopDataService tbshopdataService;

	public TBShopDataController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(TBShopData vo) throws ServiceException, DaoException {
        tbshopdataService.saveOrUpdate(vo);
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
			tbshopdataService.delete(new Long(id));
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
        TBShopData vo = tbshopdataService.get(id);
        if(vo==null){
            vo = new TBShopData();
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
    public XJsonInfo findPage(TBShopData vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(tbshopdataService.findPage(vo));
    }

	
}
