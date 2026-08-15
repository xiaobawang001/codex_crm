package com.example.crm.shared.audit.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crm.shared.audit.domain.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
