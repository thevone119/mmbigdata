package com.bingo.business.sys.controller;

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

import com.bingo.business.sys.model.*;
import com.bingo.business.sys.service.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author huangtw
 * 2018-06-25 00:31:29
 * 对象功能: 系统资源 Controller管理
 */
@RestController
@RequestMapping("/api/sys/sysres")
@ControllerFilter(LoginType = 1,UserType = 0)
public class SysResController  {
	
	@Resource
    private PubClass pubClass;
    
	@Resource
	private SysResService sysresService;

	@Resource
	private SysRoleService sysRoleService;

	@Resource
	private SessionCacheService sessionCache;

	public SysResController(){
		
	}


	/**
	 * 查询根菜单
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/listByPid")
	@ResponseBody
	public List<SysRes> listByPid(Long pid) throws Exception{
		if (pid==null){
			pid=0L;
		}
		List<SysRes> nodeList =sysresService.queryByPid(pid);
		for(SysRes res:nodeList){
			List<SysRes> _l =sysresService.queryByPid(res.getResid());
			if( _l!=null &&  _l.size()>0){
				res.setChildCount(_l.size());
			}
		}
		return nodeList;
	}

	/**
	 * 查询所有资源
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/listAll")
	@ResponseBody
	public List<SysRes> listAll() throws Exception{
		List<SysRes> nodeList =sysresService.queryAll();
		return nodeList;
	}

	/**
	 * 删除某些菜单下的子菜单
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/deleteByPid")
	@ResponseBody
	public XJsonInfo deleteByPid(Long pid) throws Exception{
		sysresService.deleteByPid(pid);
		return new XJsonInfo();
	}


	// 获得当前登录用户菜单权限
	@RequestMapping(value = "/getTreeForCurrentUser")
	public XJsonInfo getTreeForCurrentUser() throws DaoException {
		SessionUser user =(SessionUser)sessionCache.getLoginUser();
		if (user == null) {
			return new XJsonInfo(false, "无法获得登录用户信息");
		}
		//1.查询所有的菜单
		List<SysRes> menuList =sysresService.queryByRestype(0);
		//2.过滤无效的菜单
		for(int i=0;i<menuList.size();i++){
			SysRes m = menuList.get(i);
			if(m.getResstate()!=1){
				menuList.remove(i);
				i--;
			}
		}
		//3.查询当前用户的角色，资源权限列表
		if(user.getUsertype()!=1){
			List<SysRole> listrole = sysRoleService.queryByUser(user.getUserid());
			List<Long> roleids = new ArrayList<Long>();
			for(SysRole role : listrole){
				roleids.add(role.getRoleid());
			}
			List<SysRes> listres  = sysresService.queryByRole((Long[])roleids.toArray(new Long[0]));
			Map<Long,String> hasres = new HashMap();
			for(SysRes res:listres){
				hasres.put(res.getResid(),null);
			}
			//判断是否存在,不存在则删除
			for(int i=0;i<menuList.size();i++){
				SysRes m = menuList.get(i);
				if(!hasres.containsKey(m.getResid())){
					menuList.remove(i);
					i--;
				}
			}
		}

		return new XJsonInfo().setData(menuList);
	}


	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(SysRes vo) throws ServiceException, DaoException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		vo.setCreatetime(format.format(new Date()));
		vo.setUpdatetime(format.format(new Date()));

		SysRes res = sysresService.saveOrUpdate(vo);

		XJsonInfo ret = new XJsonInfo();
		//把ID返回
		ret.setData(res.getResid());
        return ret;
    }


	/**
	 * @description: <删除>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
	@RequestMapping("/delete2")
	public XJsonInfo delete2(Long id) throws ServiceException, DaoException {
		sysresService.delete(id);
		//同时删除此节点下的子节点
		sysresService.deleteByPid(id);
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
			sysresService.delete(new Long(id));
			//同时删除此节点下的子节点
			sysresService.deleteByPid(new Long(id));
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
        SysRes vo = sysresService.get(id);
        if(vo==null){
            vo = new SysRes();
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
    public XJsonInfo findPage(SysRes vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(sysresService.findPage(vo));
    }

	
}
