package com.example.crm.modules.dict.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_dict_item")
public class SysDictItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String dictType;

    private String dictLabel;

    private String dictValue;

    private Integer sort;

    private Integer status;
}
