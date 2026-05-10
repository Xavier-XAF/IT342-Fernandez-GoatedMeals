package edu.cit.fernandez.goatedmeals.security;

import edu.cit.fernandez.goatedmeals.dtos.LoginRequest;
import edu.cit.fernandez.goatedmeals.models.User;

public interface AuthenticationStrategy {
    boolean supports(String loginType);
    User authenticate(LoginRequest request);
}

