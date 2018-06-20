package com.bingo.business.sys.service;

import com.bingo.business.sys.model.T_SYS_SMS_Config;
import com.bingo.business.sys.repository.SmsConfigRepository;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.StringClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/1.
 */
@Service
public class SmsConfigService {

    @Resource
    private PubClass pubClass;

    @Resource
    private SmsConfigRepository smsConfigRepository;

    public Page<T_SYS_SMS_Config> queryPage(T_SYS_SMS_Config pm) throws DaoException {
        Pageable pageParams = pubClass.createPagenation();
        Page<T_SYS_SMS_Config> page = new Page<T_SYS_SMS_Config>();
        StringBuffer countHql = new StringBuffer();
        StringBuffer qryHql = new StringBuffer();
        StringBuffer hql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();

        countHql.append("select count(1)");
        hql.append(" FROM T_SYS_SMS_Config WHERE 1=1");

        if (StringUtils.isNotBlank(pm.getCode())) {
            hql.append(" AND code = ?");
            params.add(pm.getCode());
        }

        if (pm.getState() != -1 ) {
            hql.append(" AND state = ?");
            params.add(pm.getState());
        }

        qryHql.append(hql.toString()).append(" order by code");

        long count = smsConfigRepository.count(countHql.append(hql.toString()).toString(), params.toArray());
        if (count > 0) {
            List<T_SYS_SMS_Config> result = smsConfigRepository.queryPageListByHql(qryHql.toString(),
                    params.toArray(), pageParams);
            page.setResult(result);
        }
        page.setTotalCount(count);
        return page;
    }

    @Transactional
    public T_SYS_SMS_Config saveOrUpdate(T_SYS_SMS_Config info ) throws ServiceException {
        try {
            return smsConfigRepository.saveOrUpdate(info);
        } catch (DaoException e) {
            throw new ServiceException("新增或修改失败", e);
        }
    }

    @Transactional
    public void remove(String ids) throws DaoException {
        List<Long> arrIds= StringClass.toLongList(ids);
        Map<String, Object> map= new HashMap<>();
        String hql = "delete from T_SYS_SMS_Config t where t.id in (:ids)";
        map.put("ids",arrIds);
        smsConfigRepository.executeBySql(hql, map);
    }

    public T_SYS_SMS_Config get(Long id) throws ServiceException {
        try {
            return smsConfigRepository.getById(id);
        } catch (DaoException e) {
            throw new ServiceException("获取失败", e);
        }
    }

    public T_SYS_SMS_Config queryByCode(String code) throws DaoException {
        String hql = "from T_SYS_SMS_Config where code=?";
        return smsConfigRepository.find(hql,new String[]{code});
    }
}
