package com.bingo.business.taobao.controller;

import com.bingo.business.taobao.crawler.TBShopMainCrawer;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
import com.bingo.common.thread.MyThreadPool;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import com.bingo.business.taobao.model.*;
import com.bingo.business.taobao.service.*;

import java.util.List;

/**
 * @author huangtw
 * 2018-04-07 23:45:50
 * 对象功能: 淘宝用户表 Controller管理
 */
@RestController
@RequestMapping("/api/taobao/tbuser")
public class TBUserController  {
	
	@Resource
    private PubClass pubClass;
    
	@Resource
	private TBUserService tbuserService;

	public TBUserController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(TBUser vo) throws ServiceException, DaoException {
        tbuserService.saveOrUpdate(vo);
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
			tbuserService.delete(new Long(id));
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
        TBUser vo = tbuserService.get(id);
        if(vo==null){
            vo = new TBUser();
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
    public XJsonInfo findPage(TBUser vo) throws ServiceException, DaoException {
        return  new XJsonInfo().setPageData(tbuserService.findPage(vo));
    }

	@ResponseBody
	@RequestMapping("/UpdateShopByUser")
	public XJsonInfo UpdateShopByUser(TBUser vo) throws Exception {
    	//开启3个等待线程去执行
		MyThreadPool pool = new MyThreadPool(10,20);
		vo.setPageNo(1);
		vo.setPageSize(100);
		vo.setTotalCount(1000000);
		while(true){
			Page<TBUser> page =  tbuserService.findPage(vo);

			List<TBUser> list = page.getResult();
			if(list==null || list.size()==0){
				break;
			}
			for(TBUser shop:list){
				pool.execute(()->{
					try{
						new TBShopMainCrawer().crawerByUid(shop.getUid());
					}catch(Exception ex){
						ex.printStackTrace();
					}
				});
			}
		}
		System.out.println("shutdown");
		pool.shutdown();
		return  new XJsonInfo();
	}



	
}
