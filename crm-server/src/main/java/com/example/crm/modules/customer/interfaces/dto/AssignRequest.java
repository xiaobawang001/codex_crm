package com.example.crm.modules.customer.interfaces.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRequest {

    @NotNull(message = "目标用户不能为空")
    private Long userId;
}
