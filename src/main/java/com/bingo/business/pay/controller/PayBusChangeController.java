package com.bingo.business.pay.controller;

import com.bingo.business.pay.model.PayBus;
import com.bingo.business.pay.model.PayBusChange;
import com.bingo.business.pay.service.PayBusChangeService;
import com.bingo.business.pay.service.PayBusService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author huangtw
 * 2018-07-09 09:33:38
 * 对象功能:  Controller管理
 */
@RestController
@RequestMapping("/api/pay/paybuschange")
public class PayBusChangeController {

	@Resource
	private SessionCacheService sessionCache;

	@Resource
	private PayBusChangeService payBusChangeService;

	public PayBusChangeController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(PayBusChange vo) throws ServiceException, DaoException {
		payBusChangeService.saveOrUpdate(vo);
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
			payBusChangeService.delete(new Long(id));
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
		PayBusChange vo = payBusChangeService.get(id);
        if(vo==null){
            vo = new PayBusChange();
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
    public XJsonInfo findPage(PayBusChange vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(payBusChangeService.findPage(vo));
    }

	
}
