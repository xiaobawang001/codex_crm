package com.example.crm.modules.user.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private Map<String, Object> user;
}
