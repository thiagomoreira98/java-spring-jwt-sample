package com.sample.security.interceptor;

import com.sample.security.annotation.UserRoles;
import com.sample.security.config.SecurityProperties;
import com.sample.security.context.UserContext;
import com.sample.security.exception.UnauthorizedException;
import com.sample.security.model.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private UserContext userContext;

    @Autowired
    private SecurityProperties securityProperties;

    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String accessToken = request.getHeader(HEADER_STRING);
        this.userContext.setToken(accessToken);

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        UserRoles annotation = ((HandlerMethod) handler).getMethod().getDeclaredAnnotation(UserRoles.class);

        if(annotation == null) {
            return true;
        }

        if(accessToken == null || accessToken.isEmpty()) {
            throw new UnauthorizedException("Authorization not provided!");
        }

        this.parseJWT(userContext.getToken());

        List<String> roles = Arrays.asList(annotation.value());

        if (!userContext.getUser().hasAnyAccess(roles)) {
            throw new UnauthorizedException("User don't have access!");
        }

        return true;
    }

    public void parseJWT(String token) {
        var user = Jwts.parser()
            .setSigningKey(this.securityProperties.getJwtSigningKey())
            .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
            .getBody();

        this.parseUser(user);
    }

    public void parseUser(Claims user) {
        try {
            userContext.setUser(UserMapper.mapFromJWTClaims(user));
        } catch (Exception ex) {
            throw new UnauthorizedException(ex);
        }
    }

}
