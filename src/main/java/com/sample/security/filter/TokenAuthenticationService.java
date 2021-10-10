package com.sample.security.filter;

import com.sample.security.config.SecurityProperties;
import com.sample.security.context.UserContext;
import com.sample.security.exception.UnauthorizedException;
import com.sample.security.model.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@Component
public class TokenAuthenticationService {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserContext userContext;

    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    public void addAuthentication(HttpServletResponse response, String username) {

        String jwt = Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS512, this.securityProperties.getJwtSigningKey())
                .compact();

        response.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            var user = Jwts.parser()
                .setSigningKey(this.securityProperties.getJwtSigningKey())
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody();

            if (user != null) {
                this.validUserAccess(user, token);
                return new UsernamePasswordAuthenticationToken(user.get("id"), null, Collections.emptyList());
            }
        }

        return null;
    }

    public void validUserAccess(Claims user, String token) {
        try {
            userContext.setToken(token);
            userContext.setUser(UserMapper.mapFromJWTClaims(user));
        } catch (Exception ex) {
            throw new UnauthorizedException(ex);
        }
    }

}
