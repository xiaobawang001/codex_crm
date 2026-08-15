package com.example.crm.modules.followup.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crm.modules.followup.domain.model.FollowUp;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FollowUpMapper extends BaseMapper<FollowUp> {
}
