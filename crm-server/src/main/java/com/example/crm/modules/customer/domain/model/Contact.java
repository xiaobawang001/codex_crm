package com.example.crm.modules.customer.domain.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("contact")
public class Contact {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "所属客户不能为空")
    private Long customerId;

    @NotBlank(message = "联系人姓名不能为空")
    private String name;

    private String phone;

    private String email;

    private String position;

    private String wechat;

    private Integer isPrimary;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
