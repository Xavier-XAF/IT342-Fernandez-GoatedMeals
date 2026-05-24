package edu.cit.fernandez.goatedmeals.auth;

public interface AuthenticationStrategy {
    boolean supports(String loginType);
    User authenticate(LoginRequest request);
}

