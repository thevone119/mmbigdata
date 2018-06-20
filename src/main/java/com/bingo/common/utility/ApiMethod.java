package com.bingo.common.utility;

/**
 * Created by kidd on 2016/7/11.
 */
public enum ApiMethod {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");
    private String mehthod;

    private ApiMethod(String p_mehthod) {
        this.mehthod = p_mehthod;
    }

    @Override
    public String toString() {
        return String.valueOf(this.mehthod);
    }
}
