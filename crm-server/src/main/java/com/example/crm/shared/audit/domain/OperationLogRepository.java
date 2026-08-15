package com.example.crm.shared.audit.domain;

public interface OperationLogRepository {

    void save(OperationLog log);
}
