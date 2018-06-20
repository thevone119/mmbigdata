package com.bingo.common.utility;

/**
 * Created by kidd on 2016/7/11.
 */
public class ApiRoute {
    public String uri;
    public ApiMethod method;

    public ApiRoute() {
        this.method = ApiMethod.GET;
    }

    public ApiRoute(String p_uri) {
        this.uri = p_uri;
        this.method = ApiMethod.GET;
    }

    public ApiRoute(String p_uri,ApiMethod p_method) {
        this.uri = p_uri;
        this.method = p_method;
    }
}


