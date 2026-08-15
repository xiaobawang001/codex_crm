package com.example.crm.shared.audit.infrastructure;

import com.example.crm.shared.audit.domain.OperationLog;
import com.example.crm.shared.audit.domain.OperationLogRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OperationLogRepositoryImpl implements OperationLogRepository {

    private final OperationLogMapper mapper;

    public OperationLogRepositoryImpl(OperationLogMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(OperationLog log) {
        mapper.insert(log);
    }
}
