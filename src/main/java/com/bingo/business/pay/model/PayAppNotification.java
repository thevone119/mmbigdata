package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;
import com.bingo.common.utility.SecurityClass;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private String id;//app通知的ID,

    @Column(name = "posttime")
    private Long postTime;//app通知的时间

    @Column(name = "posttimeservice")
    private Long postTimeService;//app通知的时间，计算得到的服务器时间

    @Column(name = "packagename")
    private String packageName;//app通知的包名

    @Column(name = "title")
    private String title;//app通知的标题

    @Column(name = "text")
    private String text;//app通知的内容

    @Column(name = "subtext")
    private String subText;//app通知小行内容

    @Column(name = "uid")
    private String uid;//商户ID

    @Column(name = "createtime")
    private Long createTime;//服务器的创建时间，接收到的时间,getTime();

    @Column(name = "state")
    private Integer state;//状态，0：未提交，1：已提交服务器 2：已关联相关定的

    @Column(name = "logid")
    private String logid;//对应的订单ID

    @Column(name = "sign")
    private String sign;//签名数据，签名规则，各个字段合并后进行MD5签名

    @Column(name = "process_log")
    private String processLog;//处理过程记录


    public PayAppNotification(){
        createTime = new Date().getTime();
        state=1;
    }


    public String getNkey() {
        return nkey;
    }

    public void setNkey(String nkey) {
        this.nkey = nkey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Long getPostTimeService() {
        return postTimeService;
    }

    public void setPostTimeService(Long postTimeService) {
        this.postTimeService = postTimeService;
    }

    public String getProcessLog() {
        return processLog;
    }

    public void setProcessLog(String processLog) {
        this.processLog = processLog;
    }

    /**
     * 对对象进行签名,随便签一下就行了,自己用的
     * @return
     */
    public String MarkSign() throws IllegalAccessException {
        //组成相关的
        StringBuffer stringA =new  StringBuffer();
        stringA.append("sign_key=heimipay.com");
        stringA.append("&"+nkey);
        stringA.append("&"+id);
        stringA.append("&"+postTime);
        stringA.append("&"+packageName);
        stringA.append("&"+title);
        stringA.append("&"+text);
        stringA.append("&"+subText);
        stringA.append("&"+uid);

        //签名
        String _sign =  SecurityClass.encryptMD5(stringA.toString()).toUpperCase();
        return _sign;
    }

    @Override
    public String toString() {
        return "PayAppNotification{" +
                "nkey='" + nkey + '\'' +
                ", id=" + id +
                ", postTime=" + postTime +
                ", postTimeService=" + postTimeService +
                ", packageName='" + packageName + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", subText='" + subText + '\'' +
                ", uid='" + uid + '\'' +
                ", createTime=" + createTime +
                ", state=" + state +
                ", logid='" + logid + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    public static void main(String args[]) throws IllegalAccessException {
        PayAppNotification n = new PayAppNotification();
        n.setId("test");
        n.setNkey("n_com.tencent.mm_4097_1536313901084");
        n.setPackageName("com.tencent.mm");
        n.setPostTime(1536313901084l);
        n.setPostTimeService(1536313901191l);
        n.setTitle("微信支付");
        n.setText("[37条]微信支付: 微信支付收款0.05元");
        n.setUid("662b292a41104fb28a0aa9507f22121d");
        System.out.println(n.MarkSign());

    }
}
