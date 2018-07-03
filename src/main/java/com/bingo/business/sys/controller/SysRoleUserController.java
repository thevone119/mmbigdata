package com.bingo.business.sys.controller;

import com.bingo.business.sys.model.SysRoleRes;
import com.bingo.business.sys.service.SysRoleResService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author huangtw
 * 2018-06-25 00:30:49
 * 对象功能: 角色用户关联 Controller管理
 */
@RestController
@RequestMapping("/api/sys/sysroleres")
public class SysRoleUserController {

	@Resource
    private PubClass pubClass;

	@Resource
	private SysRoleResService sysroleresService;

	public SysRoleUserController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(SysRoleRes vo) throws ServiceException, DaoException {
        sysroleresService.saveOrUpdate(vo);
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
			sysroleresService.delete(new Long(id));
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
        SysRoleRes vo = sysroleresService.get(id);
        if(vo==null){
            vo = new SysRoleRes();
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
    public XJsonInfo findPage(SysRoleRes vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(sysroleresService.findPage(vo));
    }

	
}
