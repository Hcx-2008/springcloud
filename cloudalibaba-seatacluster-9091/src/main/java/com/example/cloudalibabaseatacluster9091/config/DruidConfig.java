package com.example.cloudalibabaseatacluster9091.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidConfig {
    //获取yml配置文件的配置// 使用该注解必须用set方法，不常用,读取配置文件中对应信息
    @ConfigurationProperties(prefix = "spring.datasource")
    //将druid数据源注入ioc容器
    @Bean
    public DataSource druid(){
        return new DruidDataSource();
    }

    //配置Druid的监控
    //1、配置管理后台的Servlet
    @Bean
    public ServletRegistrationBean DruidServlet(){
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String,String> initParams = new HashMap<>();
        //druid后台登录的用户名
        initParams.put("loginUsername","admin");
        //druid后台登录的密码
        initParams.put("loginPassword","admin");
        //默认就是允许所有访问
        initParams.put("allow","");
        bean.setInitParameters(initParams);
        return bean;
    }

    //2、配置监控的filter
    @Bean
    public FilterRegistrationBean druidStatFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());
        Map<String,String> initParams = new HashMap<>();
        initParams.put("exclusions","*.js,*.css,/druid/*");
        bean.setInitParameters(initParams);
        bean.setUrlPatterns(Arrays.asList("/*"));
        return  bean;
    }
}

