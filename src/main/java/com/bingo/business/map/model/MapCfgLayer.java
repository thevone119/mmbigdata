package com.bingo.business.map.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/1.
 * 图层表
 */
@Entity
@Table(name = "T_MAP_CFG_LAYER", catalog = "", uniqueConstraints = {@UniqueConstraint(columnNames = {})})
public class MapCfgLayer extends PageModel {

    @Id
    @TableGenerator(name="GENERATOR_ID",table="T_SYS_DB_GENERATOR",allocationSize=10)
    @GeneratedValue(strategy=GenerationType.TABLE, generator="GENERATOR_ID")
    @Column(name = "ID")
    private Long id;

    @Column(name = "name")
    private String name; //图层名称

    @Column(name = "blng")
    private String blng; // 图层中心点坐标经度

    @Column(name = "blat")
    private String blat; // 图层中心点坐标纬度

    @Column(name = "glng")
    private String glng; // GPS中心点坐标经度

    @Column(name = "glat")
    private String glat; // GPS中心点坐标纬度

    @Column(name = "create_Time",updatable = false)
    private String createTime; // 创建时间

    @Column(name = "update_Time")
    private String updateTime; // 最后修改时间

    public MapCfgLayer(){
        createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlng() {
        return blng;
    }

    public void setBlng(String blng) {
        this.blng = blng;
    }

    public String getBlat() {
        return blat;
    }

    public void setBlat(String blat) {
        this.blat = blat;
    }

    public String getGlng() {
        return glng;
    }

    public void setGlng(String glng) {
        this.glng = glng;
    }

    public String getGlat() {
        return glat;
    }

    public void setGlat(String glat) {
        this.glat = glat;
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
}
