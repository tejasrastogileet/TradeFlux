package com.tradeflux.wallet_service.auth;



import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor
        implements RequestInterceptor {

    @Override
    public void apply(
            RequestTemplate template
    ) {
        AuthenticatedUser user =
                UserContextHolder.getCurrentUser();

        if (user != null) {

            template.header(
                    "X-User-Id",
                    String.valueOf(user.getUserId())
            );
            template.header(
                    "X-User-Email",
                    user.getEmail()
            );

            template.header(
                    "X-User-Role",
                    user.getRole()
            );
        }
    }
}