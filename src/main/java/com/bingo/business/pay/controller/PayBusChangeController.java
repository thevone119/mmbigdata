package com.bingo.business.pay.controller;

import com.bingo.business.pay.model.PayBus;
import com.bingo.business.pay.model.PayBusChange;
import com.bingo.business.pay.service.PayBusChangeService;
import com.bingo.business.pay.service.PayBusService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.filter.ControllerFilter;
import com.bingo.common.model.SessionUser;
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
	 * @description: <查询>
	 * @param:
	 * @throws:
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
    @ResponseBody
    @RequestMapping("/query")
    public XJsonInfo query(Long id) throws ServiceException, DaoException {
		PayBusChange vo = payBusChangeService.get(id);
		//权限控制，只能查看自己的
		SessionUser loginuser = sessionCache.getLoginUser();
		if(loginuser.getUsertype()!=1 && !vo.getBusId().equals(loginuser.getUserid())){
			return null;
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
		SessionUser loginuser = sessionCache.getLoginUser();
		if(loginuser==null){
			if(vo.getBusId()==null){
				return null;
			}
		}
		if(vo.getBusId()==null){
			if(loginuser.getUsertype()!=1){
				vo.setBusId(loginuser.getUserid());
			}
		}


        return  new XJsonInfo().setPageData(payBusChangeService.findPage(vo));
    }

	
}
