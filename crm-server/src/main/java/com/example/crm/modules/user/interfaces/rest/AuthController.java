package com.example.crm.modules.user.interfaces.rest;

import com.example.crm.modules.user.application.AuthAppService;
import com.example.crm.modules.user.interfaces.dto.LoginRequest;
import com.example.crm.modules.user.interfaces.dto.LoginResponse;
import com.example.crm.modules.user.interfaces.dto.PasswordRequest;
import com.example.crm.shared.api.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthAppService authAppService;

    public AuthController(AuthAppService authAppService) {
        this.authAppService = authAppService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authAppService.login(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.ok();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordRequest request) {
        authAppService.changePassword(request);
        return Result.ok();
    }
}
