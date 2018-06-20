package com.bingo.business.sys.controller;

import com.bingo.business.sys.model.T_SYS_SMS_Config;
import com.bingo.business.sys.service.SmsConfigService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/1.
 */
@RestController
@RequestMapping("/api/sys/smsConfig")
@Transactional
public class SmsConfigController {

    @Resource
    private SmsConfigService smsConfigService;

    @RequestMapping("/getList")
    public XJsonInfo getList(T_SYS_SMS_Config pm) throws DaoException {
        return XJsonInfo.success().setPageData(smsConfigService.queryPage(pm));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public XJsonInfo save(T_SYS_SMS_Config info) throws ServiceException {
        info.setUpdateTime(new Date());
        smsConfigService.saveOrUpdate(info);
        return new XJsonInfo();
    }


    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public XJsonInfo remove(String ids) throws DaoException {
        smsConfigService.remove(ids);
        return new XJsonInfo();
    }

    @RequestMapping(value = "/getModel")
    public XJsonInfo getModel(Long id) throws ServiceException {
        return new XJsonInfo().setData(smsConfigService.get(id));
    }

}
