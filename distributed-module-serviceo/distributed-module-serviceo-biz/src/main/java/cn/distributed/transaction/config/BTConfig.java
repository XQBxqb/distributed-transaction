package cn.distributed.transaction.config;


import cn.distributed.transaction.interceptor.BTPreInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BTConfig {

    @Bean
    public Boolean setAutoFillValueInterceptor(SqlSessionFactory sqlSessionFactory, BTPreInterceptor btPreInterceptor) {
        org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
        configuration.addInterceptor(btPreInterceptor);
        return Boolean.TRUE;
    }
}
