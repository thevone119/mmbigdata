package com.bingo.business.sys.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/1.
 * 手机短信模板配置
 */
@Entity
@Table(name = "T_SYS_SMS_CONFIG", catalog = "", uniqueConstraints = {@UniqueConstraint(columnNames = {})})
public class T_SYS_SMS_Config {

    @Id
    @SequenceGenerator(name = "seqid", sequenceName = "SEQ_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqid")
    @Column(name = "ID")
    private Long id;

    @Column(name = "TEMPLATES")
    private String templates; // 短信模板

    @Column(name = "CODE")
    private String code; // 短信模板编码

    @Column(name = "name")
    private String name; // 短信模板名称

    @Column(name = "parameter")
    private String parameter; // 发送短信的参数



    @Column(name = "STATE")
    private int state; // 状态

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_TIME")
    private Date updateTime = new Date(); //更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplates() {
        return templates;
    }

    public void setTemplates(String templates) {
        this.templates = templates;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
