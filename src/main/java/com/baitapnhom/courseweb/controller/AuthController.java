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
import com.baitapnhom.courseweb.entity.User;
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

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody @Valid RegisterRequest request) {
        User result = userService.register(request);
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(result);
        return apiResponse;
    }

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        // 1. Lấy kết quả từ Service (Lúc này result đã là một AuthenticationResponse hoàn chỉnh chứa Token)
        AuthenticationResponse result = authenticationService.authenticate(request);

        // 2. Trả về ApiResponse chứa Object 
        ApiResponse<AuthenticationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(result);

        return apiResponse;
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid IntrospectRequest request) throws ParseException, JOSEException {
        // 1. Lấy kết quả từ Service (Lúc này result đã là một AuthenticationResponse
        // hoàn chỉnh chứa Token)
        IntrospectResponse result = authenticationService.introspect(request);

        // 2. Trả về ApiResponse chứa Object
        ApiResponse<IntrospectResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(result);

        return apiResponse;
    }
}
