package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Administrator on 2018-08-24.
 */
@Entity
@Table(name="T_PAY_APP_NOTIFICATION")
public class PayAppNotification  extends PageModel {
    @Id
    @Column(name = "nkey")
    private String nkey;//通知的唯一主键

    @Column(name = "id")
    private Integer id;//app通知的ID,

    @Column(name = "postTime")
    private Long postTime;//app通知的时间

    @Column(name = "packageName")
    private String packageName;//app通知的包名

    @Column(name = "title")
    private String title;//app通知的标题

    @Column(name = "text")
    private String text;//app通知的内容

    @Column(name = "subText")
    private String subText;//app通知小行内容

    @Column(name = "uid")
    private String uid;//商户ID

    @Column(name = "createTime")
    private Long createTime;//服务器的创建时间，接收到的时间,getTime();

    @Column(name = "state")
    private Integer state;//状态，0：未对应相关的订单，1：已对应相关定的订单

    @Column(name = "logid")
    private String logid;//对应的订单ID


    public PayAppNotification(){
        createTime = new Date().getTime();
    }


    public String getNkey() {
        return nkey;
    }

    public void setNkey(String nkey) {
        this.nkey = nkey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getPostTime() {
        return postTime;
    }

    public void setPostTime(Long postTime) {
        this.postTime = postTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getLogid() {
        return logid;
    }

    public void setLogid(String logid) {
        this.logid = logid;
    }


}
