package com.baitapnhom.courseweb.dto.request;

public class IntrospectRequest {
    private String token;

    public IntrospectRequest() {
    }

    public IntrospectRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    // Khởi tạo Builder
    public static IntrospectRequestBuilder builder() {
        return new IntrospectRequestBuilder();
    }

    public static class IntrospectRequestBuilder {
        private String token;

        IntrospectRequestBuilder() {
        }

        public IntrospectRequestBuilder token(String token) {
            this.token = token;
            return this;
        }

        public IntrospectRequest build() {
            return new IntrospectRequest(this.token);
        }
    }
}
