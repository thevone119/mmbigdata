package com.bingo.business.pay.controller;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import com.bingo.business.pay.model.*;
import com.bingo.business.pay.service.*;

/**
 * @author huangtw
 * 2018-09-06 22:49:44
 * 对象功能:  Controller管理
 */
@RestController
@RequestMapping("/api/pay/payleavemsg")
public class PayLeaveMsgController  {
	
	@Resource
    private PubClass pubClass;
    
	@Resource
	private PayLeaveMsgService payleavemsgService;

	public PayLeaveMsgController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(PayLeaveMsg vo) throws ServiceException, DaoException {
        payleavemsgService.saveOrUpdate(vo);
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
			payleavemsgService.delete(new Long(id));
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
        PayLeaveMsg vo = payleavemsgService.get(id);
        if(vo==null){
            vo = new PayLeaveMsg();
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
    public XJsonInfo findPage(PayLeaveMsg vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(payleavemsgService.findPage(vo));
    }

	
}
