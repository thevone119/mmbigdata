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
 * 2018-04-03 22:46:38
 * 对象功能: 淘宝商品 Controller管理
 */
@RestController
@RequestMapping("/api/taobao/tbshopprod")
public class TBShopProdController  {
	
	@Resource
    private PubClass pubClass;
    
	@Resource
	private TBShopProdService tbshopprodService;

	public TBShopProdController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(TBShopProd vo) throws ServiceException, DaoException {
        tbshopprodService.saveOrUpdate(vo);
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
			tbshopprodService.delete(new Long(id));
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
        TBShopProd vo = tbshopprodService.get(id);
        if(vo==null){
            vo = new TBShopProd();
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
    public XJsonInfo findPage(TBShopProd vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(tbshopprodService.findPage(vo));
    }

	
}
