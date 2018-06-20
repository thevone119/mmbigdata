package com.bingo.business.taobao.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-04-03 22:47:22
 * 对象功能: 淘宝商家月数据 Model对象
 */
@Entity
@Table(name="t_tb_shop_data")
public class TBShopData extends PageModel{

	@Id
    @GeneratedValue
	@Column(name = "keyid")
	protected Long  keyid;//keyid
	
	
	@Column(name = "shopId")
	protected Long  shopid;//shopId
	
	
	@Column(name = "sales_count")
	protected Long  salesCount;//sales_count
	
	
	@Column(name = "store_count")
	protected Long  storeCount;//store_count
	
	
	@Column(name = "sales_money")
	protected Long  salesMoney;//sales_money
	
	
	@Column(name = "max_prod_sales")
	protected Long  maxProdSales;//max_prod_sales
	
	
	@Column(name = "max_pord_store")
	protected Long  maxPordStore;//max_pord_store
	
	
	@Column(name = "count_Time")
	protected Long  countTime;//count_Time
	
	
	@Column(name = "seller_credit")
	protected Long  sellerCredit;//seller_credit
	
	
	@Column(name = "shop_score")
	protected String  shopScore;//shop_score
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public TBShopData(){
	
	}
	
	public void setKeyid(Long keyid){
		this.keyid = keyid;
	}
	/**
	 * 返回 keyid
	 * @return
	 */
	public Long getKeyid(){
		return this.keyid;
	}
	
	public void setShopid(Long shopid){
		this.shopid = shopid;
	}
	/**
	 * 返回 shopId
	 * @return
	 */
	public Long getShopid(){
		return this.shopid;
	}
	
	public void setSalesCount(Long salesCount){
		this.salesCount = salesCount;
	}
	/**
	 * 返回 sales_count
	 * @return
	 */
	public Long getSalesCount(){
		return this.salesCount;
	}
	
	public void setStoreCount(Long storeCount){
		this.storeCount = storeCount;
	}
	/**
	 * 返回 store_count
	 * @return
	 */
	public Long getStoreCount(){
		return this.storeCount;
	}
	
	public void setSalesMoney(Long salesMoney){
		this.salesMoney = salesMoney;
	}
	/**
	 * 返回 sales_money
	 * @return
	 */
	public Long getSalesMoney(){
		return this.salesMoney;
	}
	
	public void setMaxProdSales(Long maxProdSales){
		this.maxProdSales = maxProdSales;
	}
	/**
	 * 返回 max_prod_sales
	 * @return
	 */
	public Long getMaxProdSales(){
		return this.maxProdSales;
	}
	
	public void setMaxPordStore(Long maxPordStore){
		this.maxPordStore = maxPordStore;
	}
	/**
	 * 返回 max_pord_store
	 * @return
	 */
	public Long getMaxPordStore(){
		return this.maxPordStore;
	}
	
	public void setCountTime(Long countTime){
		this.countTime = countTime;
	}
	/**
	 * 返回 count_Time
	 * @return
	 */
	public Long getCountTime(){
		return this.countTime;
	}
	
	public void setSellerCredit(Long sellerCredit){
		this.sellerCredit = sellerCredit;
	}
	/**
	 * 返回 seller_credit
	 * @return
	 */
	public Long getSellerCredit(){
		return this.sellerCredit;
	}
	
	public void setShopScore(String shopScore){
		this.shopScore = shopScore;
	}
	/**
	 * 返回 shop_score
	 * @return
	 */
	public String getShopScore(){
		return this.shopScore;
	}
	
	
}
