package com.github.tenant.config;

import com.github.tenant.interceptor.TenantInterceptor;
import com.mybatisflex.core.tenant.TenantFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new TenantInterceptor())
                .excludePathPatterns("/account/add");
    }

    @Bean
    public TenantFactory tenantFactory() {
        return () -> {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                Object tenantId = attributes.getAttribute("tenantId", RequestAttributes.SCOPE_REQUEST);
                if (tenantId instanceof List<?> tenantIdList) {
                    return tenantIdList.toArray();
                }
            }

            return new Object[0];
        };

    }
}