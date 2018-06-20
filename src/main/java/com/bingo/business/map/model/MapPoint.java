package com.bingo.business.map.model;

/**
 * Created by Administrator on 2018-03-16.
 */
public class MapPoint {
    public double lng;//x 地图中的经度
    public double lat;//y 地图中的维度

    public double x;//x 地图中的经度
    public double y;//y 地图中的维度

    public MapPoint(double lng,double lat){
        this.lng =lng;
        this.lat =lat;
        this.x=lng;
        this.y = lat;
    }

    public MapPoint(String lng,String lat){
        this.lng =new Double(lng);
        this.lat =new Double(lat);
        this.x=this.lng;
        this.y = this.lat ;
    }

    @Override
    public String toString() {
        return lng+","+lat;
    }

    @Override
    public boolean equals(Object obj) {

        // 如果为同一对象的不同引用,则相同
        if (this == obj) {
            return true;
        }
        // 如果传入的对象为空,则返回false
        if (obj == null) {
            return false;
        }
        if (obj instanceof MapPoint) {
            MapPoint point = (MapPoint) obj;
            if (point.x ==this.x && point.y ==this.y) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
