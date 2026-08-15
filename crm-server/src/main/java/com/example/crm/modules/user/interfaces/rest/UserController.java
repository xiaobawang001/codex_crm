package com.example.crm.modules.user.interfaces.rest;

import com.example.crm.modules.user.application.UserAppService;
import com.example.crm.modules.user.interfaces.dto.UserRequest;
import com.example.crm.modules.user.domain.model.SysRole;
import com.example.crm.shared.api.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserAppService userAppService;

    public UserController(UserAppService userAppService) {
        this.userAppService = userAppService;
    }

    @GetMapping("/users")
    public Result<Object> page(@RequestParam(defaultValue = "1") long current,
                               @RequestParam(defaultValue = "10") long size,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Integer status,
                               @RequestParam(required = false) String roleCode) {
        return Result.ok(userAppService.page(current, size, keyword, status, roleCode));
    }

    @PostMapping("/users")
    public Result<Void> create(@Valid @RequestBody UserRequest request) {
        userAppService.create(request);
        return Result.ok();
    }

    @PutMapping("/users/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserRequest request) {
        request.setId(id);
        userAppService.update(request);
        return Result.ok();
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userAppService.updateStatus(id, status);
        return Result.ok();
    }

    @PutMapping("/users/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userAppService.resetPassword(id, body.get("password"));
        return Result.ok();
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userAppService.delete(id);
        return Result.ok();
    }

    @GetMapping("/roles")
    public Result<List<SysRole>> roles() {
        return Result.ok(userAppService.listRoles());
    }
}
