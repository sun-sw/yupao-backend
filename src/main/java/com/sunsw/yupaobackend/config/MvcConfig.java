package com.sunsw.yupaobackend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * 自定义过滤器,解决前后端分离跨域问题
     * @return
     */
    @Bean
    public FilterRegistrationBean myFilter(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        //传入自己创建的filter
        filterRegistrationBean.setFilter(new MyFilterConfig());
        //设置拦截路径
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        return filterRegistrationBean;
    }

}
