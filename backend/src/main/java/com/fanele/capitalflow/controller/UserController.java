package com.fanele.capitalflow.controller;

import com.fanele.capitalflow.dto.ProfileDetailDto;
import com.fanele.capitalflow.dto.ProfileUpdateRequest;
import com.fanele.capitalflow.dto.UserProfileDto;
import com.fanele.capitalflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserProfileDto profile() {
        return userService.getProfile();
    }

    @GetMapping("/profile/detail")
    public ProfileDetailDto profileDetail() {
        return userService.getProfileDetail();
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileDetailDto> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }
}
