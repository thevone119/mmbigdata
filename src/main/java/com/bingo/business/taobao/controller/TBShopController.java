package com.bingo.business.taobao.controller;

import com.bingo.SpringUtil;
import com.bingo.business.taobao.crawler.TBShopCreateTimeCrawer;
import com.bingo.business.taobao.crawler.TBShopMainCrawer;
import com.bingo.business.taobao.crawler.TBShopSearchCrawer;
import com.bingo.business.taobao.crawler.TBUserRateCrawer;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.Page;
import com.bingo.common.thread.MyThreadPool;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import com.bingo.business.taobao.model.*;
import com.bingo.business.taobao.service.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * @author huangtw
 * 2018-04-03 22:46:01
 * 对象功能: 淘宝商家 Controller管理
 */
@RestController
@RequestMapping("/api/taobao/tbshop")
public class TBShopController  {

	public static String[] skey = {"女装","男装","内衣","鞋靴","箱包","配件","童装玩具","孕产","用品","家电","数码","手机","美妆","洗护","保健品","珠宝","眼镜","手表","运动","户外","乐器","游戏","动漫","影视","美食","生鲜","零食","鲜花","宠物","农资","房产","装修","建材","家具","家饰","家纺","汽车","二手车","用品","办公","DIY","五金电子","百货","餐厨","家庭保健","学习","卡券","本地服务"};

	public static String[] skey01 = {"1","2","3","4","5","6","7","8","9","0"};

	public static String[] skey02 = {"a","b","c","d","e","f","g","h","i","j","k","l","n","m","o","p","q","r","s","t","u","v","w","x","y","z"};

	@Resource
    private PubClass pubClass;
    
	@Resource
	private TBShopService tbshopService;

	@Resource
	private AsyncTaskService asyncTaskService;




	public TBShopController(){
		
	}
	
	/**
	 * @description: <修改、保存>
	 * @param:
	 * @throws:
	 */
	@ResponseBody
    @RequestMapping("/save")
    public XJsonInfo save(TBShop vo) throws ServiceException, DaoException {
        tbshopService.saveOrUpdate(vo);
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
			tbshopService.delete(new Long(id));
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
        TBShop vo = tbshopService.get(id);
        if(vo==null){
            vo = new TBShop();
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
    public XJsonInfo findPage(TBShop vo) throws Exception {
        return  new XJsonInfo().setPageData(tbshopService.findPage(vo));
    }

	@ResponseBody
	@RequestMapping("/TBShopSearch")
	public XJsonInfo TBShopSearch(TBShop vo) throws Exception {
		for(int i=0;i<skey.length;i++){
			tbshopService.TBShopSearch(skey[i]);
			//ShopCrawerThread(s);
		}
		for(int i=0;i<skey01.length;i++){
			tbshopService.TBShopSearch(skey01[i]);
			//ShopCrawerThread(s);
		}
		for(int i=0;i<skey02.length;i++){
			tbshopService.TBShopSearch(skey02[i]);
			//ShopCrawerThread(s);
		}
		return  new XJsonInfo();
	}

	@ResponseBody
	@RequestMapping("/TBShopSearchByProd")
	public XJsonInfo TBShopSearchByProd(TBShop vo) throws Exception {
		for(int i=0;i<skey.length;i++){
			tbshopService.TBShopSearchByProd(skey[i]);
			//ShopCrawerThread(s);
		}
		for(int i=0;i<skey01.length;i++){
			tbshopService.TBShopSearchByProd(skey01[i]);
			//ShopCrawerThread(s);
		}
		for(int i=0;i<skey02.length;i++){
			tbshopService.TBShopSearchByProd(skey02[i]);
			//ShopCrawerThread(s);
		}
		return  new XJsonInfo();
	}

	@ResponseBody
	@RequestMapping("/UpdateShop")
	public XJsonInfo UpdateShop(TBShop vo) throws Exception {
		vo.setPageNo(1);
		vo.setPageSize(100);
		Page<TBShop> page =  tbshopService.findPage(vo);
		long totalCount = page.getTotalCount();
		vo.setTotalCount(totalCount);
		List<TBShop> list = page.getResult();
		for(TBShop shop:list){
			try{
				new TBShopMainCrawer().crawerByShopid(shop.getShopid());
			}catch (Exception ex){
				ex.printStackTrace();
			}

		}
		//循环取后面的
    	for(int i=2;i<totalCount/vo.getPageSize();i++){
			vo.setPageNo(i);
			page =  tbshopService.findPage(vo);
			list = page.getResult();
			for(TBShop shop:list){
				try{
					new TBShopMainCrawer().crawerByShopid(shop.getShopid());
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
		}
		return  new XJsonInfo();
	}


	@ResponseBody
	@RequestMapping("/UpdateShopByUserRate")
	public XJsonInfo UpdateShopByUserRate(TBShop vo) throws Exception {
		//开启1个等待线程去执行
		MyThreadPool pool = new MyThreadPool(1,10);
		vo.setPageNo(1);
		vo.setPageSize(100);

		while(true){
			Page<TBShop> page =  tbshopService.queryBySellerCreditNull(vo);
			List<TBShop> list = page.getResult();
			if(list==null || list.size()<10){
				break;
			}
			for(TBShop shop:list){
				pool.execute(()->{
					try{
						new TBUserRateCrawer().userRateUrlAction(shop);
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

	@ResponseBody
	@RequestMapping("/getCrawer")
	public void getCrawer(String body) throws Exception {
    	System.out.println(body);
	}



	@ResponseBody
	@RequestMapping("/UpdateShopCreateTime")
	public XJsonInfo UpdateShopCreateTime(TBShop vo) throws Exception {
		//开启3个等待线程去执行
		MyThreadPool pool = new MyThreadPool(1,100);
		vo.setPageNo(1);
		vo.setPageSize(100);
		vo.setTotalCount(10000);
		while(true){
			Page<TBShop> page =  tbshopService.queryByCreateTimeNull(vo);

			List<TBShop> list = page.getResult();
			if(list==null || list.size()<10){
				break;
			}
			for(TBShop shop:list){
				pool.execute(()->{
					try{
						new TBShopCreateTimeCrawer().crawerByShop(shop);
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

	/**
	 * 根据已有的日期计算没有的日期
	 *
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/UpdateShopCreateTimeByOldData")
	public XJsonInfo UpdateShopCreateTimeByOldData(TBShop vo) throws Exception {
		//开启3个等待线程去执行
		MyThreadPool pool = new MyThreadPool(1,100);
		vo.setPageNo(1);
		vo.setPageSize(100);
		vo.setTotalCount(10000);
		while(true){
			Page<TBShop> page =  tbshopService.queryByCreateTimeNull(vo);

			List<TBShop> list = page.getResult();
			if(list==null || list.size()<10){
				break;
			}
			for(TBShop shop:list){
				pool.execute(()->{
					try{
						new TBShopCreateTimeCrawer().crawerByShop(shop);
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

	private  void initShopCreateTime() throws IOException, ServiceException {
		BufferedReader reader  = new BufferedReader(new FileReader("e:/mycreatetime.txt"));
		String tempString = null;

		while ((tempString = reader.readLine()) != null) {
			// 显示行号
			System.out.println( tempString);
			String s[] = tempString.split(":");
			if(s.length!=2){
				continue;
			}
			TBShop shop = tbshopService.get(new Long(s[0]));
			shop.setShopCreatetime(s[1]);
			tbshopService.saveOrUpdate(shop);
		}
		reader.close();
		System.out.println("end");

	}








	
}
