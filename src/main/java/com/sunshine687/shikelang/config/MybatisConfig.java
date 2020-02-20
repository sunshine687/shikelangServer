package com.sunshine687.shikelang.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.sunshine687.shikelang", sqlSessionFactoryRef = "sessionFactory")
public class MybatisConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String classname;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


    /**
     * 创建数据源
     * @return 数据源
     */
    @Primary
    //@Primary
    //表示当多个候选对象具备自动连接单值依赖项的条件时，应该优先考虑bean。
    //如果候选对象中只有一个“主”bean，那么它将是自动连接的值。
    @Bean(name = "dataSource")
    public DataSource dataSource(){
        DruidDataSource dataSources = new DruidDataSource();
        //获取驱动
        dataSources.setDriverClassName(classname);
        //获取访问路径
        dataSources.setUrl(url);
        //获取用户名
        dataSources.setUsername(username);
        //获取密码
        dataSources.setPassword(password);
        return dataSources;
    }

    /**
     *
     * @param dataSource 数据源
     * @return 工厂
     */
    @Primary
    @Bean(name = "sessionFactory")
    public SqlSessionFactory sessionFactory(@Qualifier("dataSource") DataSource dataSource) {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            //resolver.getResources------将给定的位置模式解析为资源对象。
            //设置将在运行时合并到SqlSessionFactory配置中的MyBatis映射文件的位置。
            //sessionFactoryBean.setMapperLocations
            //这是在MyBatis配置文件中指定“< sqlmapper >”条目的替代方法。这个基于
            //Spring资源抽象的属性还允许在这里指定资源模式:例如:“classpath *:sqlmap / * -mapper.xml”。
            sessionFactoryBean.setMapperLocations(resolver.getResources("classpath:mappers/*Mapper.xml"));
            //返回一个实例对象的(可能共享或独立)由这个工厂管理。
            return sessionFactoryBean.getObject();
        } catch (Exception e) {
            System.out.println("mybatis初始化sqlSessionFactoryBean失败");
            e.printStackTrace();
        }
        return null;
    }
}
