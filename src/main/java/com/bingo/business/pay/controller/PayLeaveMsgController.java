package com.bingo.business.pay.controller;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.filter.ControllerFilter;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.RedisCacheService;
import com.bingo.common.service.SessionCacheService;
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
	private PayLeaveMsgService payleavemsgService;


	@Resource
	private SessionCacheService sessionCache;

	@Resource
	private RedisCacheService redis;

	public PayLeaveMsgController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ControllerFilter(LoginType = 1,UserType = 0)
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(PayLeaveMsg vo) throws ServiceException, DaoException {
		XJsonInfo ret = new XJsonInfo();
		ret.setSuccess(false);
		SessionUser loginuser = sessionCache.getLoginUser();
		if(loginuser==null){
			return new XJsonInfo(false,"对不起，您还未登陆，请登录后再留言");
		}
		vo.setMid(null);
		vo.setUserid(loginuser.getUserid());
		//限制留言
		//1个小时内，只允许错误5次密码
		Integer count = (Integer)redis.get("LEAVE_MSG_COUNT:"+vo.getUserid());
		if(count==null){
			count=0;
		}
		if(count>10){
			ret.setMsg("你留言次数太频繁，请稍候再试...");
			return ret;
		}
		count=count+1;
		redis.set("LEAVE_MSG_COUNT:"+vo.getUserid(),count,60);
        payleavemsgService.saveOrUpdate(vo);
        return new XJsonInfo();
    }

	/**
	 * @description: <删除>
	 * @param:
	 * @throws:
	 */
    //@ResponseBody
    //@RequestMapping("/delete")
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
