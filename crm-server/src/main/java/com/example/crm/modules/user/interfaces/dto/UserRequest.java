package com.example.crm.modules.user.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UserRequest {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String realName;

    private String phone;

    private String email;

    private String password;

    private Integer status;

    private List<String> roleCodes;
}
