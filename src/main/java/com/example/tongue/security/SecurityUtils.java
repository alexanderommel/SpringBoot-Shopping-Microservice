package com.example.tongue.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SecurityUtils {

    public static Boolean containsRequiredValues(Claims claims){
        Object issuer = claims.getIssuer();
        Object audience = claims.getAudience();
        Object id = claims.getId();
        Object authorities = claims.get("authorities");
        Object subject = claims.getSubject();
        return !(issuer==null || audience==null || id==null || authorities==null || subject==null);
    }

    public static Claims validateJwtStringAndGetClaims(String token, String key, String header, String prefix){
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return null;
    }

    public static Claims validateJwtAndGetClaims(HttpServletRequest request, String key, String header, String prefix){
        String token = request.getHeader(header).replace(prefix,"");
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return null;
    }

    public static Boolean isJwtOnRequestHeader(HttpServletRequest request, String header, String prefix){
        String authorization = request.getHeader(header);
        if (authorization==null)
            return false;
        log.info("Authorization header: "+authorization);
        if (!authorization.startsWith(prefix))
            return false;
        return true;
    }

    public static Authentication getAuthenticationTokenFromClaims(Claims claims){
        try {
            String username= claims.getSubject();
            List<String> authList = (List<String>) claims.get("authorities");
            List<GrantedAuthority> authorities =
                    authList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(username,null,authorities);
            return authentication;
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return null;
    }

    public static Boolean validIssuerAndAudience(Claims claims, String issuer, String audience){
        if (!(claims.getIssuer().equalsIgnoreCase(issuer))){
            return false;
        }
        if (!(claims.getAudience().equalsIgnoreCase(audience))){
            return false;
        }
        return true;
    }



}
