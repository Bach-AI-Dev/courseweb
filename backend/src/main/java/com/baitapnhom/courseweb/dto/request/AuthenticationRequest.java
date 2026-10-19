package com.baitapnhom.courseweb.dto.request;

public class AuthenticationRequest {
    // @FieldDefaults(level = AccessLevel.PRIVATE)
    private String username;
    private String password;

    // @NoArgsConstructor
    public AuthenticationRequest() {
    }

    // @AllArgsConstructor
    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // @Data (Getters và Setters)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // @Builder
    public static AuthenticationRequestBuilder builder() {
        return new AuthenticationRequestBuilder();
    }

    public static class AuthenticationRequestBuilder {
        private String username;
        private String password;

        AuthenticationRequestBuilder() {
        }

        public AuthenticationRequestBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AuthenticationRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public AuthenticationRequest build() {
            return new AuthenticationRequest(this.username, this.password);
        }
    }
}
