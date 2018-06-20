package com.bingo.business.taobao.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-04-03 22:48:02
 * 对象功能: 淘宝商品月数据 Model对象
 */
@Entity
@Table(name="t_tb_shop_prod_data")
public class TBShopProdData extends PageModel{

	@Id
    @GeneratedValue
	@Column(name = "KEYID")
	protected Long  keyid;//KEYID
	
	
	@Column(name = "product_id")
	protected Long  productId;//product_id
	
	
	@Column(name = "shopid")
	protected Long  shopid;//shopid
	
	
	@Column(name = "price")
	protected Long  price;//price
	
	
	@Column(name = "sale_num")
	protected Long  saleNum;//sale_num
	
	
	@Column(name = "rate_num")
	protected Long  rateNum;//rate_num
	
	
	@Column(name = "shop_price")
	protected Long  shopPrice;//shop_price
	
	
	@Column(name = "standard_price")
	protected Long  standardPrice;//standard_price
	
	
	@Column(name = "count_Time")
	protected Long  countTime;//count_Time
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public TBShopProdData(){
	
	}
	
	public void setKeyid(Long keyid){
		this.keyid = keyid;
	}
	/**
	 * 返回 KEYID
	 * @return
	 */
	public Long getKeyid(){
		return this.keyid;
	}
	
	public void setProductId(Long productId){
		this.productId = productId;
	}
	/**
	 * 返回 product_id
	 * @return
	 */
	public Long getProductId(){
		return this.productId;
	}
	
	public void setShopid(Long shopid){
		this.shopid = shopid;
	}
	/**
	 * 返回 shopid
	 * @return
	 */
	public Long getShopid(){
		return this.shopid;
	}
	
	public void setPrice(Long price){
		this.price = price;
	}
	/**
	 * 返回 price
	 * @return
	 */
	public Long getPrice(){
		return this.price;
	}
	
	public void setSaleNum(Long saleNum){
		this.saleNum = saleNum;
	}
	/**
	 * 返回 sale_num
	 * @return
	 */
	public Long getSaleNum(){
		return this.saleNum;
	}
	
	public void setRateNum(Long rateNum){
		this.rateNum = rateNum;
	}
	/**
	 * 返回 rate_num
	 * @return
	 */
	public Long getRateNum(){
		return this.rateNum;
	}
	
	public void setShopPrice(Long shopPrice){
		this.shopPrice = shopPrice;
	}
	/**
	 * 返回 shop_price
	 * @return
	 */
	public Long getShopPrice(){
		return this.shopPrice;
	}
	
	public void setStandardPrice(Long standardPrice){
		this.standardPrice = standardPrice;
	}
	/**
	 * 返回 standard_price
	 * @return
	 */
	public Long getStandardPrice(){
		return this.standardPrice;
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
	
	
}
