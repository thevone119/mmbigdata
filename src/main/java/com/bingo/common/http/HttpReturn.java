package com.bingo.common.http;

/**
 * Created by Administrator on 2018-09-06.
 * 定义http的返回
 */
public class HttpReturn {
    public int code;
    public String body;

    public HttpReturn(){

    }

    public HttpReturn(int code,String body){
        this.code=code;
        this.body=body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
