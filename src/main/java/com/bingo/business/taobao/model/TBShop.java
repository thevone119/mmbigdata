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
 * 2018-04-03 22:46:01
 * 对象功能: 淘宝商家 Model对象
 */
@Entity
@Table(name="t_tb_shop")
public class TBShop extends PageModel{

	@Id
	@Column(name = "shopid")
	protected Long  shopid;//商家ID主键
	
	
	@Column(name = "title")
	protected String  title;//店铺标题
	
	
	@Column(name = "sdesc")
	protected String  sdesc;//店铺描述
	
	
	@Column(name = "bulletin")
	protected String  bulletin;//店铺公告
	
	
	@Column(name = "mainpage")
	protected String  mainpage;//商家主页,相对路径，如：//shop71784759.taobao.com

	@Column(name = "user_rate_url")
	protected String  userRateUrl;//卖家主页,相对路径，如：//rate.taobao.com/user-rate-UOmgWMCQ0OmIb.htm

	
	
	@Column(name = "nick")
	protected String  nick;//卖家昵称

	@Column(name = "uid")
	protected Long  uid;//卖家ID
	
	
	@Column(name = "main_cid")
	protected Long  mainCid;//主营类目ID
	
	
	@Column(name = "main_cname")
	protected String  mainCname;//主营类目名称
	
	
	@Column(name = "seller_credit")
	protected Long  sellerCredit;//商家信用

	@Column(name = "sales_count")
	protected Integer  salesCount;//销量

	@Column(name = "prod_count")
	protected Integer  prodCount;//产品数


	
	@Column(name = "shop_createtime")
	protected String  shopCreatetime;//开店时间
	
	
	@Column(name = "shop_area")
	protected String  shopArea;//所在地

	@Column(name = "prod_loc")
	protected String  prodLoc;//所在地(根据产品计算出来的所在地，热销产品的所在地汇总出来的。TOP5中最多的产品卖在哪个地区就算哪个地区的)


	@Column(name = "goodrate_percent")
	protected Float  goodratePercent;//好评率(好评率99.05)


	@Column(name = "shop_score")
	protected Float  shopScore;//店铺动态评分（近半年）,汇总
	
	@Column(name = "item_score")
	protected Float  itemScore;//商品描述评分（近半年）
	
	
	@Column(name = "service_score")
	protected Float  serviceScore;//服务态度评分（近半年）
	
	
	@Column(name = "delivery_score")
	protected Float  deliveryScore;//发货速度评分（近半年）


	@Column(name = "comment_count")
	private Integer commentCount;//评论人数（近半年）
	
	
	@Column(name = "shop_type")
	protected String  shopType;//商家类型（TB淘宝、TM天猫）
	
	
	@Column(name = "create_Time",updatable = false)
	protected String  createTime;//创建时间
	
	
	@Column(name = "update_Time")
	protected String  updateTime;//最后修改时间
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public TBShop(){
		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String now=dtf2.format(LocalDateTime.now());
		createTime = now;
		updateTime = now;
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
	
	public void setTitle(String title){
		this.title = title;
	}
	/**
	 * 返回 title
	 * @return
	 */
	public String getTitle(){
		return this.title;
	}
	
	public void setSdesc(String sdesc){
		this.sdesc = sdesc;
	}
	/**
	 * 返回 sdesc
	 * @return
	 */
	public String getSdesc(){
		return this.sdesc;
	}
	
	public void setBulletin(String bulletin){
		this.bulletin = bulletin;
	}
	/**
	 * 返回 bulletin
	 * @return
	 */
	public String getBulletin(){
		return this.bulletin;
	}
	
	public void setMainpage(String mainpage){
		this.mainpage = mainpage;
	}
	/**
	 * 返回 mainpage
	 * @return
	 */
	public String getMainpage(){
		return this.mainpage;
	}
	
	public void setNick(String nick){
		this.nick = nick;
	}
	/**
	 * 返回 nick
	 * @return
	 */
	public String getNick(){
		return this.nick;
	}
	
	public void setMainCid(Long mainCid){
		this.mainCid = mainCid;
	}
	/**
	 * 返回 main_cid
	 * @return
	 */
	public Long getMainCid(){
		return this.mainCid;
	}
	
	public void setMainCname(String mainCname){
		this.mainCname = mainCname;
	}
	/**
	 * 返回 main_cname
	 * @return
	 */
	public String getMainCname(){
		return this.mainCname;
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
	
	public void setShopCreatetime(String shopCreatetime){
		this.shopCreatetime = shopCreatetime;
	}
	/**
	 * 返回 shop_createtime
	 * @return
	 */
	public String getShopCreatetime(){
		return this.shopCreatetime;
	}
	
	public void setShopArea(String shopArea){
		this.shopArea = shopArea;
	}
	/**
	 * 返回 shop_area
	 * @return
	 */
	public String getShopArea(){
		return this.shopArea;
	}

	public Float getShopScore() {
		return shopScore;
	}

	public void setShopScore(Float shopScore) {
		this.shopScore = shopScore;
	}

	public Float getGoodratePercent() {
		return goodratePercent;
	}

	public void setGoodratePercent(Float goodratePercent) {
		this.goodratePercent = goodratePercent;
	}

	public Float getItemScore() {
		return itemScore;
	}

	public void setItemScore(Float itemScore) {
		this.itemScore = itemScore;
	}

	public Float getServiceScore() {
		return serviceScore;
	}

	public void setServiceScore(Float serviceScore) {
		this.serviceScore = serviceScore;
	}

	public Float getDeliveryScore() {
		return deliveryScore;
	}

	public void setDeliveryScore(Float deliveryScore) {
		this.deliveryScore = deliveryScore;
	}

	public void setShopType(String shopType){
		this.shopType = shopType;
	}
	/**
	 * 返回 shop_type
	 * @return
	 */
	public String getShopType(){
		return this.shopType;
	}
	
	public void setCreateTime(String createTime){
		this.createTime = createTime;
	}
	/**
	 * 返回 create_Time
	 * @return
	 */
	public String getCreateTime(){
		return this.createTime;
	}
	
	public void setUpdateTime(String updateTime){
		this.updateTime = updateTime;
	}
	/**
	 * 返回 update_Time
	 * @return
	 */
	public String getUpdateTime(){
		return this.updateTime;
	}

	public Integer getSalesCount() {
		return salesCount;
	}

	public void setSalesCount(Integer salesCount) {
		this.salesCount = salesCount;
	}

	public Integer getProdCount() {
		return prodCount;
	}

	public void setProdCount(Integer prodCount) {
		this.prodCount = prodCount;
	}

	public String getUserRateUrl() {
		return userRateUrl;
	}

	public void setUserRateUrl(String userRateUrl) {
		this.userRateUrl = userRateUrl;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	public String getProdLoc() {
		return prodLoc;
	}

	public void setProdLoc(String prodLoc) {
		this.prodLoc = prodLoc;
	}

	@Override
	public String toString() {
		return "TBShop{" +
				"shopid=" + shopid +
				", title='" + title + '\'' +
				", sdesc='" + sdesc + '\'' +
				", bulletin='" + bulletin + '\'' +
				", mainpage='" + mainpage + '\'' +
				", userRateUrl='" + userRateUrl + '\'' +
				", nick='" + nick + '\'' +
				", uid=" + uid +
				", mainCid=" + mainCid +
				", mainCname='" + mainCname + '\'' +
				", sellerCredit=" + sellerCredit +
				", salesCount=" + salesCount +
				", prodCount=" + prodCount +
				", shopCreatetime='" + shopCreatetime + '\'' +
				", shopArea='" + shopArea + '\'' +
				", goodratePercent=" + goodratePercent +
				", shopScore=" + shopScore +
				", itemScore=" + itemScore +
				", serviceScore=" + serviceScore +
				", deliveryScore=" + deliveryScore +
				", commentCount=" + commentCount +
				", shopType='" + shopType + '\'' +
				", createTime='" + createTime + '\'' +
				", updateTime='" + updateTime + '\'' +
				'}';
	}
}
