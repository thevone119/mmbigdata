package com.bingo.business.sys.service;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.bingo.business.sys.model.*;
import com.bingo.business.sys.repository.*;

/**
 * @author huangtw
 * 2018-06-24 23:55:28
 * 对象功能: 淘宝用户表 service管理
 */
@Service
@Transactional
public class SysUserService{
	
	@Resource
	private SysUserRepository sysuserRepository;

	/**
	 * @description: <保存对象>
	 * @param:
	 * @return:
	 * @throws DaoException 
	 * @throws:
	 */
	public void saveOrUpdate(SysUser vo) throws ServiceException, DaoException {
		sysuserRepository.saveOrUpdate(vo);
	}
	
	/**
	 * @description: <取对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public SysUser get(Serializable id) throws DaoException{
		return sysuserRepository.getById(id);
	}


	/**
	 * 根据用户名，密码查询用户
	 * @param useracc
	 * @param pwd
	 * @return
	 * @throws DaoException
	 */
	public SysUser queryByUserAndPwd(String useracc,String pwd) throws DaoException{
		String hql = "from SysUser where useracc=? and pwd=?";
		return sysuserRepository.find(hql,new String[]{useracc,pwd});
	}

	/**
	 * 根据账号，邮箱查询用户
	 * @param useracc
	 * @param email
	 * @return
	 * @throws DaoException
	 */
	public SysUser queryByUserAndEmail(String useracc,String email) throws DaoException{
		String hql = "from SysUser where useracc=? and email=?";
		return sysuserRepository.find(hql,new String[]{useracc,email});
	}

	/**
	 * 根据账号，邮箱查询用户
	 * @param useracc
	 * @return
	 * @throws DaoException
	 */
	public SysUser queryByUseracc(String useracc) throws DaoException{
		String hql = "from SysUser where useracc=? ";
		return sysuserRepository.find(hql,new String[]{useracc});
	}

	/**
	 * 通过用户的uuid获取用户
	 * @param uuid
	 * @return
	 * @throws DaoException
	 */
	public SysUser queryByUuid(String uuid) throws DaoException{
		String hql = "from SysUser where uuid=? ";
		return sysuserRepository.find(hql,new String[]{uuid});
	}
	
	/**
	 * @description: <删除对象>
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	public void delete(Serializable id) throws DaoException{
		sysuserRepository.deleteById(id);
	}




	
	/**
	 * @description: <取分页列表>
	 * @param:
	 * @return:
	 * @throws:
	 */
	public Page<SysUser> findPage(SysUser vo){
		StringBuffer hql = new StringBuffer(" from SysUser where userid is not null ");
		List<Object> fldValues = new ArrayList<Object>();
		if(StringUtils.isNotEmpty(vo.getUseracc())){
			hql.append(" and useracc like ?");
			fldValues.add("%"+vo.getUseracc()+"%");
		}
		if(StringUtils.isNotEmpty(vo.getNikename())){
			hql.append(" and nikename like ?");
			fldValues.add("%"+vo.getNikename()+"%");
		}
		if(vo.getUsertype()!=null && vo.getUsertype()!=-1){
			hql.append(" and usertype = ?");
			fldValues.add(vo.getUsertype());
		}
		if(vo.getState()!=null && vo.getState()!=-1){
			hql.append(" and state = ?");
			fldValues.add(vo.getState());
		}
		//密码不返回
		Page<SysUser> page = sysuserRepository.findPage(hql.toString(), vo, fldValues);
		if(page!=null && page.getTotalCount()>0){
			for(SysUser user : page.getResult()){
				//user.setPwd(null);
			}
		}
		return page;
	}

	/**
	 * 重设商户的signkey
	 * @param
	 * @return
	 */
	public String reSetSignKey(Long userid){
		StringBuffer qhtl = new StringBuffer(" update SysUser set signKey=? where userid=?");
		String signKey =  UUID.randomUUID().toString().replace("-", "").toLowerCase();
		sysuserRepository.executeByHql(qhtl.toString(),new Object[]{signKey,userid});
		return signKey;
	}

	/**
	 * 重设商户的signkey
	 * @param
	 * @return
	 */
	public void clearPayImg(Long userid){
		StringBuffer qhtl = new StringBuffer(" update SysUser set payImgContentWx=null,payImgContentZfb=null where userid=?");
		sysuserRepository.executeByHql(qhtl.toString(),new Object[]{userid});
		return;
	}



}
