package com.example.crm.modules.user.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crm.modules.user.domain.model.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}
