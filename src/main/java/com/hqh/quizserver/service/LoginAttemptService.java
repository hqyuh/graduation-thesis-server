package com.hqh.quizserver.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {

    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    private static final int ATTEMPT_INCREMENT = 1;
    private static final int TIME = 15;

    // user    attempts
    private final LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder
                .newBuilder()
                // it will expire/delete after TIME minutes of caching
                .expireAfterWrite(TIME, MINUTES)
                // number of items in cache
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    /***
     * remove user from cache
     *
     * @param email
     *
     */
    public void evictUserFromLoginAttemptCache(String email) {
        loginAttemptCache.invalidate(email);
    }

    /***
     * add user to cache
     *
     * @param email
     */
    public void addUserToLoginAttemptsCache(String email) {
        int attempts = 0;

        try {
            attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(email);
            // add to cache
            loginAttemptCache.put(email, attempts);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /***
     * lock if wrong login on MAXIMUM_NUMBER_OF_ATTEMPTS
     *
     * @param email
     * @return true/false
     */
    public boolean hasExceededMaxAttempts(String email) {
        try {
            return loginAttemptCache.get(email) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

}
