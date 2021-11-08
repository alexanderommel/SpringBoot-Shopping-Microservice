package com.example.tongue.security.filters;

import com.example.tongue.core.authentication.UserRepository;
import com.example.tongue.security.JwtAuthenticationManager;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleJwtAuthenticationFilter extends OncePerRequestFilter {

    //Fields
    private GoogleIdTokenVerifier tokenVerifier;
    private JwtAuthenticationManager authenticationManager;
    private RememberMeServices rememberMeServices = new NullRememberMeServices();
    private static final Logger logger =
            LoggerFactory.getLogger(GoogleJwtAuthenticationFilter.class);
    //Only authorized accounts are enabled to login using this id
    public static String clientId = "319232536194-754v9d4i0hpfugk92qe8o0g3am7qj5q3.apps.googleusercontent.com";

    public GoogleJwtAuthenticationFilter(UserRepository userRepository){
        logger.info("Setting GooglePublicKeysManager");
        GooglePublicKeysManager publicKeysManager = new GooglePublicKeysManager(new NetHttpTransport(),new GsonFactory());
        logger.info("Setting GoogleIdTokenVerifier");
        tokenVerifier = new GoogleIdTokenVerifier.Builder(publicKeysManager)
                .setAudience(Collections.singletonList(clientId))
                .build();
        logger.info("Setting GoogleJWTAuthenticationManager");
        authenticationManager = new JwtAuthenticationManager(userRepository);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        logger.info("Getting parameter 'idToken' from request");
        String idTokenString = httpServletRequest.getParameter("idToken");
        if (idTokenString==null){
            logger.info("idToken empty");
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        try {
            logger.info("IdToken: "+idTokenString);
            GoogleIdToken googleIdToken = tokenVerifier.verify(idTokenString);
            if (googleIdToken==null){
                logger.info("IdToken invalid (Probably is not a Google Signed Token)");
                filterChain.doFilter(httpServletRequest,httpServletResponse);
                return;
            }else {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();
                logger.info("Getting payload");
                String userId = payload.getSubject();
                Authentication authentication = new UsernamePasswordAuthenticationToken(userId,"google");
                logger.info("Authenticating user...");
                authentication = authenticationManager.authenticate(authentication);
                logger.info("Saving on security context");
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                logger.info("RememberMeServices login success");
                this.rememberMeServices.loginSuccess(httpServletRequest,httpServletResponse,authentication);
            }
        }catch (AuthenticationException | GeneralSecurityException e) {
            e.printStackTrace();
            logger.info("Login failed");
            SecurityContextHolder.clearContext();
            this.rememberMeServices.loginFail(httpServletRequest,httpServletResponse);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }
        logger.info("DoFilter next");
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
