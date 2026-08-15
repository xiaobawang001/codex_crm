package com.example.crm.modules.followup.domain.model;

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
@TableName("follow_up")
public class FollowUp {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "关联客户不能为空")
    private Long customerId;

    private Long contactId;

    private String type;

    @NotBlank(message = "跟进内容不能为空")
    private String content;

    private LocalDateTime nextFollowTime;

    private Long createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
