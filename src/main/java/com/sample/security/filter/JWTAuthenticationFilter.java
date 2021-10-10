package com.sample.security.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private TokenAuthenticationService tokenAuthenticationService;

    private static final String HEADER_NAME = "Authorization";

    @Autowired
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, TokenAuthenticationService tokenAuthenticationService) {
        super(authenticationManager);
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader(HEADER_NAME);

        if (header == null) {
            chain.doFilter(request, response);
            return;
        }

        var authentication = this.authenticate(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken authenticate(HttpServletRequest request) {
        return this.tokenAuthenticationService.getAuthentication(request);
    }

}
