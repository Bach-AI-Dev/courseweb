package com.baitapnhom.courseweb.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baitapnhom.courseweb.dto.request.AuthenticationRequest;
import com.baitapnhom.courseweb.dto.request.IntrospectRequest;
import com.baitapnhom.courseweb.dto.request.RegisterRequest;
import com.baitapnhom.courseweb.dto.response.ApiResponse;
import com.baitapnhom.courseweb.dto.response.AuthenticationResponse;
import com.baitapnhom.courseweb.dto.response.IntrospectResponse;
import com.baitapnhom.courseweb.dto.response.UserResponse; // Đã thêm import này
import com.baitapnhom.courseweb.service.AuthenticationService;
import com.baitapnhom.courseweb.service.UserService;
import com.nimbusds.jose.JOSEException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    // API đăng kí User
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
        UserResponse result = userService.register(request);
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(result);
        return apiResponse;
    }

    // API tạo tài khoản Teacher
    @PostMapping("/admin/teacher")
    public ApiResponse<UserResponse> registerteacher(@RequestBody @Valid RegisterRequest request) {
        // Đổi User thành UserResponse
        UserResponse result = userService.registerteacher(request);
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(result);
        return apiResponse;
    }

    // API login User
    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {

        AuthenticationResponse result = authenticationService.authenticate(request);
        ApiResponse<AuthenticationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(result);

        return apiResponse;
    }

    // API kiểm tra token
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid IntrospectRequest request)
            throws ParseException, JOSEException {
        IntrospectResponse result = authenticationService.introspect(request);
        ApiResponse<IntrospectResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(result);

        return apiResponse;
    }
}
