package com.bingo.business.map.model;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2018-03-27.
 */
public class Line {

    /**
     * 端点1
     */
    public MapPoint POINTA;


    /**
     * 端点2
     */
    public MapPoint POINTB;


    public Line(MapPoint pointA, MapPoint pointB) {


        this.POINTA = pointA;
        this.POINTB = pointB;
    }


    /**
     * 判断当前线段是否包含给定的点</br>
     * 即给定的点是否在当前边上
     * @param point
     * @return
     */
    public boolean isContainsPoint(MapPoint point) {


        boolean result = false;
        //判断给定点point与端点1构成线段的斜率是否和当前线段的斜率相同
        //给定点point与端点1构成线段的斜率k1
        Double k1 = null;
        if (point.x==this.POINTA.x) {
            k1 = Double.NEGATIVE_INFINITY;
        } else {
            k1 = div(sub(point.y, this.POINTA.y), sub(point.x, this.POINTA.x));
        }
        //当前线段的斜率k2
        Double k2 = null;
        if (this.POINTB.x==this.POINTA.x) {
            k2 = Double.NEGATIVE_INFINITY;
        } else {
            k2 = div(sub(this.POINTB.y, this.POINTA.y), sub(this.POINTB.x, this.POINTA.y));
        }
        if (k1 != null && k2 != null) {
            if (k1.equals(k2)) {
                //若斜率相同，继续判断给定点point的x是否在pointA.x和pointB.x之间,若在 则说明该点在当前边上
                if (sub(point.x, this.POINTA.x) * sub(point.x, this.POINTB.x) < 0) {
                    result = true;
                }
            }
        }
        return result;
    }


    //叉积
    double mult(MapPoint a, MapPoint b, MapPoint c) {
        return (a.x-c.x)*(b.y-c.y)-(b.x-c.x)*(a.y-c.y);
    }


    /**
     * 给定线段是否与当前线段相交</br>
     * 相交返回true, 不相交返回false
     * @param line
     * @return
     */
    public boolean
    isIntersect(Line line) {


        MapPoint aa = this.POINTA;
        MapPoint bb = this.POINTB;
        MapPoint cc = line.POINTA;
        MapPoint dd = line.POINTB;
        if (Math.max(aa.x, bb.x) < Math.min(cc.x, dd.x)) {
            return false;
        }
        if (Math.max(aa.y, bb.y) < Math.min(cc.y, dd.y)) {
            return false;
        }
        if (Math.max(cc.x, dd.x) < Math.min(aa.x, bb.x)) {
            return false;
        }
        if (Math.max(cc.y, dd.y) < Math.min(aa.y, bb.y)) {
            return false;
        }
        if (mult(cc, bb, aa) * mult(bb, dd, aa) < 0 ) {
            return false;
        }
        if ( mult(aa, dd, cc) * mult(dd, bb, cc) < 0 ) {
            return false;
        }
        return true;
    }


    /**
     * 提供精确的加法运算。
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }


    /**
     * 提供精确的减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }


    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }


    private static final int DEF_DIV_SCALE = 10;


    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1,double v2){
        return div(v1,v2,DEF_DIV_SCALE);
    }


    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1,double v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
