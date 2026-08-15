package com.example.crm.modules.opportunity.domain.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("opportunity")
public class Opportunity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "商机名称不能为空")
    private String name;

    @NotNull(message = "关联客户不能为空")
    private Long customerId;

    private BigDecimal amount;

    private String stage;

    private LocalDate expectedCloseDate;

    private BigDecimal winAmount;

    private String loseReason;

    private Long ownerId;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
