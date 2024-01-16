package com.backbase.moviesdigger.utils;

import com.backbase.moviesdigger.exceptions.UnauthorizedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenMethodsUtil {

    //TODO I skip token verification by public key to make it simply, but in real big apps we have to adjust it using openid-connect/certs openid
    public String getUserTokenClaimValue(String token, String tokenClaim) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode;
            try {
                rootNode = objectMapper.readTree(payload);
            } catch (JsonProcessingException e) {
                log.warn("Could not read json body, reason is {}", e.getMessage());
                throw new UnauthorizedException("Failed authorization for user, try again or speak with admin");
            }
            return rootNode.path(tokenClaim).asText();
        } catch (Exception e) {
            log.warn("Exception during extracting token claim from access token, reason is {}", e.getMessage());
            return null;
        }
    }
}
