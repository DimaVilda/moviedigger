package com.backbase.moviesdigger.interceptors;

import com.backbase.moviesdigger.dtos.BearerTokenModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class BearerTokenInterceptor implements HandlerInterceptor {

    private final BearerTokenModel tokenWrapper;
    private static final String BEARER_PREFIX = "Bearer ";

    //TODO in best case scenation a token should not be in DTO but in spring security context but it takes more time
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String authorizationHeaderValue = request.getHeader("Authorization");
        if (authorizationHeaderValue != null && authorizationHeaderValue.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeaderValue.substring(BEARER_PREFIX.length()).trim();

            if (!token.equals(tokenWrapper.getToken())) {
                tokenWrapper.setToken(token);
            }
        }
        return true;
    }
}
