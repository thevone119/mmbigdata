package com.bingo.common.configuration;

import com.bingo.common.filter.URLInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Administrator on 2018-06-26.
 */
@SpringBootConfiguration
public class MySpringMVCConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private URLInterceptor urlconfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(urlconfig).addPathPatterns("/**");
    }


}
