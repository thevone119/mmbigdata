package com.bingo.business.map.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/1.
 * 区域表（网格表）,多边形区域表
 */
@Entity
@Table(name = "T_MAP_CFG_AREA", catalog = "", uniqueConstraints = {@UniqueConstraint(columnNames = {})})
public class MapCfgArea extends PageModel {

    @Id
    @TableGenerator(name="GENERATOR_ID",table="T_SYS_DB_GENERATOR",allocationSize=10)
    @GeneratedValue(strategy=GenerationType.TABLE, generator="GENERATOR_ID")
    @Column(name = "ID")
    private Long id;


    @Column(name = "name")
    private String name; //区域名称

    @Column(name = "blng")
    private String blng; // 区域中心点坐标经度

    @Column(name = "blat")
    private String blat; // 区域中心点坐标纬度

    @Column(name = "glng")
    private String glng; // GPS中心点坐标经度

    @Column(name = "glat")
    private String glat; // GPS中心点坐标纬度

    @Column(name = "create_Time",updatable = false)
    private String createTime; // 创建时间

    @Column(name = "update_Time")
    private String updateTime; // 最后修改时间

    @Column(name = "layer_id")
    private Long layerId; // 图层ID

    @Column(name = "area_type")
    private String areaType = "polygon"; // 区域类型,polygon:多边形  point 点 round: 圆形  sector:扇形

    @Column(name = "area_radius")
    private String areaRadius; // 区域半径

    @Column(name = "area_aspect")
    private String areaAspect; // 区域方向

    @Column(name = "area_border_gps")
    private String areaBorderGps; // 区域的边界坐标（GPS）坐标，每个坐标用|线分隔

    @Column(name = "area_border_baidu")
    private String areaBorderBaidu; // 区域的边界坐标（百度）坐标，每个坐标用|线分隔


    public MapCfgArea(){
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

    public Long getLayerId() {
        return layerId;
    }

    public void setLayerId(Long layerId) {
        this.layerId = layerId;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getAreaRadius() {
        return areaRadius;
    }

    public void setAreaRadius(String areaRadius) {
        this.areaRadius = areaRadius;
    }

    public String getAreaAspect() {
        return areaAspect;
    }

    public void setAreaAspect(String areaAspect) {
        this.areaAspect = areaAspect;
    }

    public String getAreaBorderGps() {
        return areaBorderGps;
    }

    public void setAreaBorderGps(String areaBorderGps) {
        this.areaBorderGps = areaBorderGps;
    }

    public String getAreaBorderBaidu() {
        return areaBorderBaidu;
    }

    public void setAreaBorderBaidu(String areaBorderBaidu) {
        this.areaBorderBaidu = areaBorderBaidu;
    }
}
