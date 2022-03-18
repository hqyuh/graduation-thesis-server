package com.hqh.graduationthesisserver.listener;

import com.hqh.graduationthesisserver.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class AuthenticationFailureListener {

    private final LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    /***
     * If you can't log in,
     * this event will fire and put the username in the cache
     *
     * @param event
     */
    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event)
            throws ExecutionException {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof String) {
            String email = (String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttemptsCache(email);
        }
    }

}
