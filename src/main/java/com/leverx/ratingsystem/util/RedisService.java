package com.leverx.ratingsystem.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private static final long CONFIRMATION_CODE_EXPIRATION = 24;
    private static final long RESET_CODE_EXPIRATION = 10;

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
        redisTemplate.opsForValue().set("pending:" + email, userJson, CONFIRMATION_CODE_EXPIRATION, TimeUnit.HOURS);
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
        return Boolean.TRUE.equals(redisTemplate.hasKey(email));
    }

    /**
     * Removes the confirmation code and pending user data after successful confirmation.
     */
    public void removeConfirmationCode(String email) {
        redisTemplate.delete(email);
        redisTemplate.delete("pending:" + email);
    }

    /**
     * Stores a password reset code in Redis with a 10-minute expiration time.
     */
    public void storeResetCode(String email, String code) {
        redisTemplate.opsForValue().set("reset:" + email, code, RESET_CODE_EXPIRATION, TimeUnit.MINUTES);
    }

    /**
     * Validates the provided password reset code.
     */
    public boolean validateResetCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get("reset:" + email);
        return storedCode != null && storedCode.equals(code);
    }

    /**
     * Removes the password reset code from Redis after it has been used.
     */
    public void removeResetCode(String email) {
        redisTemplate.delete("reset:" + email);
    }
}