package com.github.tenant.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class TenantInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {

        // / 获取所有租户 ID
        Enumeration<String> tenantIds = request.getHeaders("tenantId");

        if (tenantIds == null || !tenantIds.hasMoreElements()) {
            // 如果没有提供 tenantId，返回错误响应
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tenant ID is required.");
            return false;
        }

        List<Long> tenantIdList = new ArrayList<>();
        while (tenantIds.hasMoreElements()) {
            String tenantId = tenantIds.nextElement();
            tenantIdList.add(Long.valueOf(tenantId));
        }

        // 设置租户 ID
        request.setAttribute("tenantId", tenantIdList);

        return true;
    }
}
