package com.example.crm.modules.opportunity.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crm.modules.opportunity.domain.model.Opportunity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OpportunityMapper extends BaseMapper<Opportunity> {
}
