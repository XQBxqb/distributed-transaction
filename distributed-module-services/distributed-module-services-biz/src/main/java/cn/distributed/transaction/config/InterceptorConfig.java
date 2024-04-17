package cn.distributed.transaction.config;

import cn.distributed.transaction.interceptor.FeignApiInterceptor;
import cn.distributed.transaction.interceptor.RollbackInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
@Slf4j
@Component
public class InterceptorConfig extends WebMvcConfigurationSupport {

    @Autowired
    private FeignApiInterceptor feignApiInterceptor;
    @Autowired
    private RollbackInterceptor rollbackInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 这里添加的路径不包含项目的contextPath哦
        registry.addInterceptor(feignApiInterceptor)
                .addPathPatterns("/feign/**");
        registry.addInterceptor(rollbackInterceptor)
                        .addPathPatterns("/rollback");
        super.addInterceptors(registry);
    }

}