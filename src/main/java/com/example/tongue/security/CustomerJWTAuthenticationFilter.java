package com.example.tongue.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomerJWTAuthenticationFilter extends OncePerRequestFilter {

    private String customerServiceKey;
    private String serviceName;
    private String header;
    private String prefix;
    private RememberMeServices rememberMeServices;
    private CustomerAuthenticationManager authenticationManager;

    public CustomerJWTAuthenticationFilter(@Autowired CustomerAuthenticationManager authenticationManager,
                                           @Value("${customer.management.service.key}") String customerServiceKey,
                                           @Value("${shopping.service.name}") String serviceName){
        this.authenticationManager=authenticationManager;
        this.customerServiceKey=customerServiceKey;
        this.header="Authorization";
        this.prefix="Bearer ";
        this.serviceName=serviceName;
        this.rememberMeServices = new NullRememberMeServices();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        doFilter(httpServletRequest,httpServletResponse,filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.info("Customer Management Service Authorization");
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a!=null){
            log.info("Aborting since the user is already authenticated");
            chain.doFilter(request,response);
            return;
        }
        log.info("Searching jwt in request parameter or authorization header...");
        Boolean jwtOnRequestParameter = Boolean.FALSE;
        String jwtToken = request.getParameter("jwtToken");
        if (jwtToken!=null){
            log.info("JWT found on Request Parameters");
            jwtOnRequestParameter = Boolean.TRUE;
        }
        if (!jwtOnRequestParameter && !SecurityUtils.isJwtOnRequestHeader(request,header,prefix)){
            log.info("No Jwt found either in Request Header and Parameters");
            chain.doFilter(request,response);
            return;
        }

        Claims claims;

        try {
            if (jwtOnRequestParameter)
                claims = SecurityUtils.validateJwtStringAndGetClaims(jwtToken,customerServiceKey,header,prefix);
            else
                claims = SecurityUtils.validateJwtAndGetClaims(request,customerServiceKey,header,prefix);
            if (claims==null)
                throw new MalformedJwtException("Malformed Jwt");
            if (!SecurityUtils.containsRequiredValues(claims))
                throw new MalformedJwtException("Incomplete Jwt");
            if (!SecurityUtils.validIssuerAndAudience(claims,"customer-management-service",serviceName))
                throw new MalformedJwtException("Invalid issuer and audience for this jwt");
            Authentication authentication = SecurityUtils.getAuthenticationTokenFromClaims(claims);
            if (authentication==null)
                throw new MalformedJwtException("Error converting claims to Authentication");
            log.info("Authenticating...");
            authentication = authenticationManager.authenticate(authentication);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            this.rememberMeServices.loginSuccess(request,response,authentication);
            log.info("Successful authentication");
        }catch (MalformedJwtException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
        catch (ExpiredJwtException | UnsupportedJwtException e){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN,e.getMessage());
        }
        catch (AuthenticationException e){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.sendError(HttpServletResponse.SC_NOT_FOUND,e.getMessage());
        }
        catch (IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage());
        }
        log.info("Invoking next security filter");
        chain.doFilter(request,response);
    }
}
