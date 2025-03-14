package com.leverx.ratingsystem.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Stores the email confirmation code in Redis with a 24-hour expiration.
     */
    public void storeConfirmationCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, 24, TimeUnit.HOURS);
    }

    /**
     * Stores the pending user data in Redis temporarily until confirmation.
     */
    public void storeUserPendingConfirmation(String email, String code, String userJson) {
        redisTemplate.opsForValue().set("pending:" + email, userJson, 24, TimeUnit.HOURS);
        storeConfirmationCode(email, code);
    }

    /**
     * Validates the email confirmation code and retrieves user data if valid.
     */
    public String validateAndRetrievePendingUser(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);
        if (storedCode != null && storedCode.equals(code)) {
            return redisTemplate.opsForValue().get("pending:" + email);
        }
        return null;
    }

    /**
     * Retrieves pending user data (without validation).
     */
    public String getPendingUser(String email) {
        return redisTemplate.opsForValue().get("pending:" + email);
    }

    /**
     * Checks if an email confirmation is still pending (user has not confirmed yet).
     */
    public boolean isEmailUnconfirmed(String email) {
        return redisTemplate.hasKey(email);
    }

    /**
     * Removes the confirmation code and pending user data after successful confirmation.
     */
    public void removeConfirmationCode(String email) {
        redisTemplate.delete(email);
        redisTemplate.delete("pending:" + email);
    }
}