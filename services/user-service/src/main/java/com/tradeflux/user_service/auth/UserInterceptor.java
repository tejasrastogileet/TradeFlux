package com.tradeflux.user_service.auth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        String userId =
                request.getHeader("X-User-Id");
        String email =
                request.getHeader("X-User-Email");
        String role =
                request.getHeader("X-User-Role");

        if (
                userId != null
                        &&
                        email != null
                        &&
                        role != null
        ) {

            AuthenticatedUser user =
                    AuthenticatedUser.builder()
                            .userId(
                                    Long.valueOf(userId)
                            )
                            .email(email)
                            .role(role)
                            .build();
            UserContextHolder.setCurrentUser(user);
        }
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        UserContextHolder.clear();
    }
}
