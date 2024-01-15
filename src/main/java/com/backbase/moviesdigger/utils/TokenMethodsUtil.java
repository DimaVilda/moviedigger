package com.backbase.moviesdigger.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.PublicKey;

@Component
@Slf4j
public class TokenMethodsUtil {

    public String getUserTokenClaimValue(String token, String tokenClaim) {
        try {
           // SecretKey secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
            Claims claims = Jwts.parser()
                    .verifyWith(PublicKey.)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get(tokenClaim, String.class);
        } catch (Exception e) {
            log.warn("Exception during extracting token claim from access token, reason is {}", e.getMessage());
            return null;
        }
    }
}
