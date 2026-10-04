package com.baitapnhom.courseweb.dto.response;

public class AuthenticationResponse {

    private boolean authenticated;
    private String token;
    // private UserResponse user;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(boolean authenticated, String token, UserResponse user) {
        this.authenticated = authenticated;
        this.token = token;
        // this.user = user;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // public UserResponse getUser() {
    //     return user;
    // }

    // public void setUser(UserResponse user) {
    //     this.user = user;
    // }
}
