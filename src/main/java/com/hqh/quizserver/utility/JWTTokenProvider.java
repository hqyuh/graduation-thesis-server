package com.hqh.quizserver.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.hqh.quizserver.entity.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.hqh.quizserver.constant.SecurityConstant.*;
import static java.util.Arrays.stream;


@Component
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
                  .sign(HMAC512(secret.getBytes()));
    }

    /**
     * get permission from userPrincipal to give function generateJwtToken()
     *
     * -> return a permissions or list
     *  ROLE_
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
     */
    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();

        return verifier
                .verify(token)
                .getClaim(AUTHORITIES)
                .asArray(String.class);
    }

    /**
     * verify jwt
     * -> This function is used to check jwt is valid or not
    * */
    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;

        try {
            Algorithm algorithm = HMAC512(secret);
            verifier = JWT.require(algorithm)
                          .withIssuer(GET_ARRAYS)
                          .build();
        } catch (JWTVerificationException ex) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }

        return verifier;
    }

    /**
     * get user authentication, get user information
     * */
    public Authentication getAuthentication(String username,
                                            List<GrantedAuthority> authorities,
                                            HttpServletRequest request) {
        // // valid user will set information
        UsernamePasswordAuthenticationToken userPasswordToken = new
                UsernamePasswordAuthenticationToken(username, null, authorities);
        userPasswordToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return userPasswordToken;
    }

    /**
     * check token is valid
     *
     * -> Returns false if username is invalid and token expires, otherwise returns true
     * */
    public boolean isTokenValid(String username, String token) {
        JWTVerifier verifier = getJWTVerifier();

        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
    }

    /**
     * check if the token has expired
     * ->
     * */
    public boolean isTokenExpired(JWTVerifier verifier, String token) {
        // getExpiresAt() -> Returns the Expiration Time value
        Date expiration = verifier.verify(token).getExpiresAt();

        return expiration.before(new Date());
    }

    // get subject
    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();

        return verifier
                .verify(token)
                .getSubject();
    }

}
