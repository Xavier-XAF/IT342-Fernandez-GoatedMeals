package edu.cit.fernandez.goatedmeals.auth;

public class LoginRequest {
    private String email;
    private String password;
    private String loginType;
    private String googleIdToken;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getLoginType(){
        return loginType;
    }
    public void setLoginType(String loginType){
        this.loginType = loginType;
    }
    public String getGoogleIdToken(){
        return googleIdToken;
    }
    public void setGoogleIdToken(String googleIdToken){
        this.googleIdToken = googleIdToken;
    }
}
