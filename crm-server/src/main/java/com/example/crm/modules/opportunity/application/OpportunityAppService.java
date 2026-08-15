package com.example.crm.modules.opportunity.application;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.customer.domain.service.CustomerVisibilityService;
import com.example.crm.modules.opportunity.domain.model.Opportunity;
import com.example.crm.modules.opportunity.domain.model.OpportunityStage;
import com.example.crm.modules.opportunity.domain.repository.OpportunityRepository;
import com.example.crm.shared.api.DataScope;
import com.example.crm.shared.api.PageResult;
import com.example.crm.shared.audit.application.AuditService;
import com.example.crm.shared.auth.UserContext;
import com.example.crm.shared.exception.BizException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OpportunityAppService {

    private final OpportunityRepository opportunityRepository;
    private final CustomerVisibilityService customerVisibilityService;
    private final AuditService auditService;

    public OpportunityAppService(OpportunityRepository opportunityRepository,
                                 CustomerVisibilityService customerVisibilityService,
                                 AuditService auditService) {
        this.opportunityRepository = opportunityRepository;
        this.customerVisibilityService = customerVisibilityService;
        this.auditService = auditService;
    }

    public PageResult<Opportunity> page(OpportunityQuery query) {
        if (query.getCustomerId() != null) {
            checkCustomerVisible(query.getCustomerId());
        }
        Page<Opportunity> page = opportunityRepository.page(query, DataScope.fromUserContext());
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    public Opportunity detail(Long id) {
        Opportunity opportunity = opportunityRepository.findById(id);
        if (opportunity == null) {
            throw new BizException(404, "商机不存在");
        }
        checkVisible(opportunity);
        return opportunity;
    }

    public void create(Opportunity opportunity) {
        checkCustomerVisible(opportunity.getCustomerId());
        if (opportunity.getStage() == null || opportunity.getStage().isBlank()) {
            opportunity.setStage(OpportunityStage.CONTACT.code());
        }
        OpportunityStage.fromCode(opportunity.getStage());
        if (opportunity.getOwnerId() == null && !UserContext.isAdminOrManager()) {
            opportunity.setOwnerId(UserContext.userId());
        }
        opportunityRepository.save(opportunity);
        auditService.log("opportunity", "create", opportunity.getId(), "新增商机: " + opportunity.getName());
    }

    public void update(Opportunity opportunity) {
        Opportunity exists = detail(opportunity.getId());
        checkEditable(exists);
        if (OpportunityStage.fromCode(exists.getStage()).isTerminal()) {
            throw new BizException("商机已结束，不可修改");
        }
        OpportunityStage.fromCode(opportunity.getStage());
        opportunity.setOwnerId(exists.getOwnerId());
        opportunity.setWinAmount(exists.getWinAmount());
        opportunity.setLoseReason(exists.getLoseReason());
        opportunityRepository.update(opportunity);
    }

    public void changeStage(Long id, String stage) {
        Opportunity opportunity = detail(id);
        checkEditable(opportunity);
        OpportunityStage current = OpportunityStage.fromCode(opportunity.getStage());
        OpportunityStage target = OpportunityStage.fromCode(stage);
        if (current.isTerminal()) {
            throw new BizException("商机已结束，不可变更阶段");
        }
        if (target.isTerminal()) {
            throw new BizException("请使用赢单/输单接口完成终态操作");
        }
        opportunity.setStage(target.code());
        opportunityRepository.update(opportunity);
        auditService.log("opportunity", "stage", id, "商机阶段变更: " + stage);
    }

    public void win(Long id, BigDecimal winAmount) {
        Opportunity opportunity = detail(id);
        checkEditable(opportunity);
        if (OpportunityStage.fromCode(opportunity.getStage()).isTerminal()) {
            throw new BizException("商机已结束");
        }
        opportunity.setStage(OpportunityStage.WIN.code());
        opportunity.setWinAmount(winAmount);
        opportunityRepository.update(opportunity);
        auditService.log("opportunity", "win", id, "商机赢单: " + opportunity.getName());
    }

    public void lose(Long id, String reason) {
        Opportunity opportunity = detail(id);
        checkEditable(opportunity);
        if (OpportunityStage.fromCode(opportunity.getStage()).isTerminal()) {
            throw new BizException("商机已结束");
        }
        opportunity.setStage(OpportunityStage.LOSE.code());
        opportunity.setLoseReason(reason);
        opportunityRepository.update(opportunity);
        auditService.log("opportunity", "lose", id, "商机输单: " + opportunity.getName());
    }

    public void delete(Long id) {
        Opportunity opportunity = detail(id);
        checkEditable(opportunity);
        opportunityRepository.removeById(id);
        auditService.log("opportunity", "delete", id, "删除商机: " + opportunity.getName());
    }

    private void checkVisible(Opportunity opportunity) {
        if (UserContext.isAdminOrManager()) {
            return;
        }
        Long uid = UserContext.userId();
        boolean visible = uid.equals(opportunity.getOwnerId())
                || opportunity.getOwnerId() == null
                || customerVisibilityService.isVisible(opportunity.getCustomerId(), uid);
        if (!visible) {
            throw new BizException(403, "无权访问该商机");
        }
    }

    private void checkEditable(Opportunity opportunity) {
        if (UserContext.isAdminOrManager()) {
            return;
        }
        if (!UserContext.userId().equals(opportunity.getOwnerId())) {
            throw new BizException(403, "仅商机负责人可操作");
        }
    }

    private void checkCustomerVisible(Long customerId) {
        if (UserContext.isAdminOrManager()) {
            return;
        }
        if (!customerVisibilityService.isVisible(customerId, UserContext.userId())) {
            throw new BizException(403, "无权操作该客户下的商机");
        }
    }
}
