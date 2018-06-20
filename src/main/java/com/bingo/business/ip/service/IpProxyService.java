package com.bingo.business.ip.service;

import com.bingo.business.ip.bean.IpProxyModel;
import com.bingo.business.ip.repository.IpProxyRepository;
import com.bingo.business.taobao.model.TBUser;
import com.bingo.business.taobao.repository.TBUserRepository;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangtw
 * 2018-04-07 23:45:50
 * 对象功能: 淘宝用户表 service管理
 */
@Service
@Transactional
public class IpProxyService {
	
	@Resource
	private IpProxyRepository ipProxyRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(IpProxyModel vo) throws ServiceException, DaoException {
		ipProxyRepository.saveOrUpdate(vo);
	}


	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public IpProxyModel get(Serializable id) throws DaoException{
		return ipProxyRepository.getById(id);
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		ipProxyRepository.deleteById(id);
	}
	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<IpProxyModel> findPage(IpProxyModel vo){
		StringBuffer hql = new StringBuffer(" from IpProxyModel where ip is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		/**
		if(StringUtils.isNotEmpty(vo.getUserAccount())){
			hql.append(" and userAccount = ?");
			fldValues.add(vo.getUserAccount());
		}
		**/
		hql.append(" order by update_time desc");
		return ipProxyRepository.findPage(hql.toString(), vo, fldValues);
	}
	
}
