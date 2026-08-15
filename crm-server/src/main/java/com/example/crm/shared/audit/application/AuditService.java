package com.example.crm.shared.audit.application;

import com.example.crm.shared.audit.domain.OperationLog;
import com.example.crm.shared.audit.domain.OperationLogRepository;
import com.example.crm.shared.auth.UserContext;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final OperationLogRepository operationLogRepository;

    public AuditService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    public void log(String module, String action, Long targetId, String content) {
        OperationLog log = new OperationLog();
        log.setUserId(UserContext.userId());
        log.setModule(module);
        log.setAction(action);
        log.setTargetId(targetId);
        log.setContent(content);
        operationLogRepository.save(log);
    }
}
