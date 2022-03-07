package com.hqh.graduationthesisserver.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hqh.graduationthesisserver.domain.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.hqh.graduationthesisserver.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * create jwt
     * get user information to create jwt
     */
    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create()
                  // publisher name
                  .withIssuer(GET_ARRAYS)
                  .withAudience(GET_ARRAYS_ADMINISTRATION)
                  // creation time
                  .withIssuedAt(new Date())
                  .withSubject(userPrincipal.getUsername())
                  // permission
                  .withArrayClaim(AUTHORITIES, claims)
                  .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                  .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    /**
     * get permission from userPrincipal to give function generateJwtToken()
     *
     * -> return a list of permissions
     */
    private String[] getClaimsFromUser(UserPrincipal user) {
        return user.getAuthorities()
                   .stream()
                   .map(GrantedAuthority::getAuthority)
                   .toArray(String[]::new);
    }

    /**
     * get rights from tokens
     */
    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);

        return stream(claims)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * get request from token
     * get token and decrypt token
     * */
    private String[] getClaimsFromToken(String token) {

    }

}
