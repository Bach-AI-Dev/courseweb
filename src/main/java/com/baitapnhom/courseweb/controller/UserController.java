package com.baitapnhom.courseweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import com.baitapnhom.courseweb.dto.response.ApiResponse;
// import com.baitapnhom.courseweb.dto.request.RegisterRequest;
import com.baitapnhom.courseweb.dto.request.UserUpdateRequest;
import com.baitapnhom.courseweb.entity.User;
import com.baitapnhom.courseweb.service.UserService;

// import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    // @PostMapping
    // ApiResponse<User> createUser(@RequestBody @Valid RegisterRequest request) {
    //     ApiResponse<User> apiResponse = new ApiResponse<>();

    //     apiResponse.setResult(userService.register(request));
    //     return apiResponse;
    // }

    @GetMapping
    List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    User getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return userService.updatUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deletUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "User has been deleted...................";
    }
}
