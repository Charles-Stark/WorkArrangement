package com.example.backend.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtil {

    // token到期时间10小时
    private static final long EXPIRE_TIME = 10 * 60 * 60 * 1000;
    // salt
    private static final String salt = "*12sca*3q74h??";

    public static String sign(String telephone) {
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("telephone", telephone)
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                    .sign(Algorithm.HMAC256(salt));
        } catch (IllegalArgumentException | JWTCreationException e) {
            e.printStackTrace();
        }

        return token;
    }

    public static Boolean verify(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(salt)).withIssuer("auth0").build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
        } catch (IllegalArgumentException | JWTVerificationException e) {
            return false;
        }

        return true;
    }

}
