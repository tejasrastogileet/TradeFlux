package com.tradeflux.portfolio_service.auth;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Slf4j
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(

            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        RoleAllowed roleAllowed =
                method.getMethodAnnotation(
                        RoleAllowed.class
                );

        if (roleAllowed == null) {
            roleAllowed =
                    method.getBeanType()
                            .getAnnotation(
                                    RoleAllowed.class
                            );
        }

        if (roleAllowed == null) {
            return true;
        }
        AuthenticatedUser currentUser =
                UserContextHolder.getCurrentUser();

        if (currentUser == null) {
            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return false;
        }

        Set<String> allowedRoles =
                Set.of(roleAllowed.value());

        boolean hasAccess =
                allowedRoles.contains(
                        currentUser.getRole()
                );
        if (!hasAccess) {
            log.warn(
                    "Access denied for role: {}",
                    currentUser.getRole()
            );

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return false;
        }
        return true;
    }
}
