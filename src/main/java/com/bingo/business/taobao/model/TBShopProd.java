package com.bingo.business.taobao.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-04-03 22:46:38
 * 对象功能: 淘宝商品 Model对象
 */
@Entity
@Table(name="t_tb_shop_prod")
public class TBShopProd extends PageModel{

	@Id
	@Column(name = "product_id")
	protected Long  productId;//产品ID
	
	
	@Column(name = "shopid")
	protected Long  shopid;//店铺ID


	@Column(name = "uid")
	protected Long  uid;//用户ID，卖家ID
	
	
	@Column(name = "outer_id")
	protected String  outerId;//outer_id
	
	
	@Column(name = "cid")
	protected Long  cid;//cid
	
	
	@Column(name = "cat_name")
	protected String  catName;//cat_name
	
	
	@Column(name = "commodity_id")
	protected Long  commodityId;//commodity_id
	
	
	@Column(name = "created")
	protected Long  created;//created
	
	
	@Column(name = "name")
	protected String  name;//商品名称

	@Column(name = "loc")
	protected String  loc;//归属地市
	
	
	@Column(name = "price")
	protected Long  price;//price 市场价
	
	
	@Column(name = "prod_desc")
	protected String  prodDesc;//prod_desc
	
	
	@Column(name = "modified")
	protected Long  modified;//modified
	
	
	@Column(name = "status")
	protected Long  status;//status
	
	
	@Column(name = "level")
	protected Long  level;//level
	
	
	@Column(name = "sale_num")
	protected Long  saleNum;//产品的销售量
	
	
	@Column(name = "rate_num")
	protected Long  rateNum;//产品的评分次数
	
	
	@Column(name = "shop_price")
	protected Long  shopPrice;//淘宝价
	
	
	@Column(name = "standard_price")
	protected Long  standardPrice;//产品的标准价格
	
	
	@Column(name = "vertical_market")
	protected Long  verticalMarket;//垂直市场,如：3（3C），4（鞋城）

	@Column(name = "create_Time",updatable = false)
	protected String  createTime;//create_Time


	@Column(name = "update_Time")
	protected String  updateTime;//update_Time
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public TBShopProd(){
		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String now=dtf2.format(LocalDateTime.now());
		createTime = now;
		updateTime = now;
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
	
	public void setOuterId(String outerId){
		this.outerId = outerId;
	}
	/**
	 * 返回 outer_id
	 * @return
	 */
	public String getOuterId(){
		return this.outerId;
	}
	
	public void setCid(Long cid){
		this.cid = cid;
	}
	/**
	 * 返回 cid
	 * @return
	 */
	public Long getCid(){
		return this.cid;
	}
	
	public void setCatName(String catName){
		this.catName = catName;
	}
	/**
	 * 返回 cat_name
	 * @return
	 */
	public String getCatName(){
		return this.catName;
	}
	
	public void setCommodityId(Long commodityId){
		this.commodityId = commodityId;
	}
	/**
	 * 返回 commodity_id
	 * @return
	 */
	public Long getCommodityId(){
		return this.commodityId;
	}
	
	public void setCreated(Long created){
		this.created = created;
	}
	/**
	 * 返回 created
	 * @return
	 */
	public Long getCreated(){
		return this.created;
	}
	
	public void setName(String name){
		this.name = name;
	}
	/**
	 * 返回 name
	 * @return
	 */
	public String getName(){
		return this.name;
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
	
	public void setProdDesc(String prodDesc){
		this.prodDesc = prodDesc;
	}
	/**
	 * 返回 prod_desc
	 * @return
	 */
	public String getProdDesc(){
		return this.prodDesc;
	}
	
	public void setModified(Long modified){
		this.modified = modified;
	}
	/**
	 * 返回 modified
	 * @return
	 */
	public Long getModified(){
		return this.modified;
	}
	
	public void setStatus(Long status){
		this.status = status;
	}
	/**
	 * 返回 status
	 * @return
	 */
	public Long getStatus(){
		return this.status;
	}
	
	public void setLevel(Long level){
		this.level = level;
	}
	/**
	 * 返回 level
	 * @return
	 */
	public Long getLevel(){
		return this.level;
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
	
	public void setVerticalMarket(Long verticalMarket){
		this.verticalMarket = verticalMarket;
	}
	/**
	 * 返回 vertical_market
	 * @return
	 */
	public Long getVerticalMarket(){
		return this.verticalMarket;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	@Override
	public String toString() {
		return "TBShopProd{" +
				"productId=" + productId +
				", shopid=" + shopid +
				", uid=" + uid +
				", outerId='" + outerId + '\'' +
				", cid=" + cid +
				", catName='" + catName + '\'' +
				", commodityId=" + commodityId +
				", created=" + created +
				", name='" + name + '\'' +
				", loc='" + loc + '\'' +
				", price=" + price +
				", prodDesc='" + prodDesc + '\'' +
				", modified=" + modified +
				", status=" + status +
				", level=" + level +
				", saleNum=" + saleNum +
				", rateNum=" + rateNum +
				", shopPrice=" + shopPrice +
				", standardPrice=" + standardPrice +
				", verticalMarket=" + verticalMarket +
				", createTime='" + createTime + '\'' +
				", updateTime='" + updateTime + '\'' +
				'}';
	}
}
