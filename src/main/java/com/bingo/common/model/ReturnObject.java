package com.bingo.common.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2018-08-07.
 * 定义统一返回的对象，一般用于json数据格式返回
 */
public class ReturnObject implements Serializable {
    private Integer code=0;
    private String msg="ok";
    private Object data;

    public ReturnObject(){

    }

    public ReturnObject(Integer code){
        this.code = code;
    }

    public ReturnObject(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public ReturnObject(Integer code,String msg,Object data){
        this.code = code;
        this.msg = msg;
        this.data=data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
