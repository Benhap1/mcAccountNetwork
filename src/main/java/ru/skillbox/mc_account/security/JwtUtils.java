package ru.skillbox.mc_account.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.Map;

@Component
public class JwtUtils {

    public String getEmailFromToken(String token) {
        try {
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format");
            }
            String payload = new String(Base64.getDecoder().decode(tokenParts[1]));
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
            String email = claims.get("email").toString();

            return email;
        } catch (Exception e) {
            return null;
        }
    }
}

