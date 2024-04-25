package com.github.config;

import com.github.domain.Account;
import com.github.listerner.LoggerListener;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.spring.boot.ConfigurationCustomizer;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import com.mybatisflex.spring.boot.SqlSessionFactoryBeanCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MyBatisFlexConfiguration implements MyBatisFlexCustomizer, ConfigurationCustomizer, SqlSessionFactoryBeanCustomizer {

    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        String name = globalConfig
                .getDbType()
                .getName();
        log.info("MyBatisFlexConfiguration.customize.globalConfig ==> {}", name);

        // 注册日志监听器
        globalConfig.registerInsertListener(new LoggerListener(), Account.class);
        globalConfig.registerUpdateListener(new LoggerListener(), Account.class);
    }

    @Override
    public void customize(FlexConfiguration configuration) {
        log.info("MyBatisFlexConfiguration.customize.configuration ==> {}", configuration);
    }

    @Override
    public void customize(SqlSessionFactoryBean factoryBean) {
        log.info("MyBatisFlexConfiguration.customize.factoryBean ==> {}", factoryBean);
    }
}
