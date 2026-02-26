package com.example.aslapp_backend.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final long REFRESH_TTL_DAYS = 7;

    private String generateJti(){
        return UUID.randomUUID().toString();}
    private String getKey(Long userId, String jti) {
        return PREFIX + userId + ":" + jti;
    }

    public void storeRefreshToken(Long userId, String jti, String device, String ip) {
        if (userId == null || jti == null || jti.isEmpty()) {
            throw new IllegalArgumentException("userId and jti cannot be null or empty");
        }

        String key = getKey(userId,jti) ;
        Map<String, Object> sessionData = new HashMap<>();

        sessionData.put("issuedAt", Instant.now().toString());
        sessionData.put("device", device != null ? device : "UNKNOWN");
        sessionData.put("ip", ip != null ? ip : "UNKNOWN");

        redisTemplate.opsForHash().putAll(key, sessionData);
        redisTemplate.expire(key, Duration.ofDays(REFRESH_TTL_DAYS));
    }


    public void addToBlackList(Long userId, String jti) {
        storeRefreshToken(userId, jti, null, null);
    }


    public boolean isValid(Long userId, String jti) {
        if (userId == null || jti == null) {
            return false;
        }

        String key = getKey(userId, jti);
        String blacklistKey = BLACKLIST_PREFIX + jti;

        // Check if token exists and is not blacklisted
        return redisTemplate.hasKey(key) && !redisTemplate.hasKey(blacklistKey);
    }

    /**
     * Retrieves all stored data for a refresh token
     */
    public Map<Object, Object> getTokenData(Long userId, String jti) {
        if (userId == null || jti == null) {
            return null;
        }

        String key = getKey(userId, jti);
        return  redisTemplate.opsForHash().entries(key);
    }

    /**
     * Deletes a single refresh token
     */
    public void deleteRefreshToken(Long userId, String jti) {
        if (userId == null || jti == null) {
            return;
        }

        String key = getKey(userId, jti);
        redisTemplate.delete(key);
    }


    public void blacklistToken(String jti) {
        if (jti == null || jti.isEmpty()) {
            return;
        }

        String blacklistKey = BLACKLIST_PREFIX + jti;
        redisTemplate.opsForValue().set(blacklistKey, "revoked", Duration.ofDays(REFRESH_TTL_DAYS));
    }


    public void deleteAllRefreshTokens(Long userId) {
        if (userId == null) {
            return;
        }

        Set<String> keys = redisTemplate.keys(PREFIX + userId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }


    public String rotateRefreshToken(Long userId, String oldJti, String device, String ip) {
        if (userId == null || oldJti == null) {
            throw new IllegalArgumentException("userId and oldJti cannot be null");
        }

        // Blacklist the old token
        blacklistToken(oldJti);
        deleteRefreshToken(userId, oldJti);

        // Generate and store new token
        String newJti = generateJti();
        storeRefreshToken(userId, newJti, device, ip);

        return newJti;
    }


    public Map<String, Map<Object, Object>> getAllUserTokens(Long userId) {
        if (userId == null) {
            return new HashMap<>();
        }

        Set<String> keys = redisTemplate.keys(PREFIX + userId + ":*");
        Map<String, Map<Object, Object>> result = new HashMap<>();

        if (keys != null) {
            for (String key : keys) {
                String jti = key.replace(PREFIX + userId + ":", "");
                Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
                result.put(jti, data);
            }
        }

        return result;
    }


}
