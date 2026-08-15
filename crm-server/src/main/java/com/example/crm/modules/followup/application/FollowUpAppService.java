package com.example.crm.modules.followup.application;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.customer.domain.service.CustomerVisibilityService;
import com.example.crm.modules.followup.domain.model.FollowUp;
import com.example.crm.modules.followup.domain.repository.FollowUpRepository;
import com.example.crm.shared.api.DataScope;
import com.example.crm.shared.api.PageResult;
import com.example.crm.shared.auth.UserContext;
import com.example.crm.shared.exception.BizException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowUpAppService {

    private final FollowUpRepository followUpRepository;
    private final CustomerVisibilityService customerVisibilityService;

    public FollowUpAppService(FollowUpRepository followUpRepository,
                              CustomerVisibilityService customerVisibilityService) {
        this.followUpRepository = followUpRepository;
        this.customerVisibilityService = customerVisibilityService;
    }

    public PageResult<FollowUp> page(FollowUpQuery query) {
        if (query.getCustomerId() != null) {
            checkCustomerVisible(query.getCustomerId());
        }
        Page<FollowUp> page = followUpRepository.page(query, DataScope.fromUserContext());
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    public List<FollowUp> todo() {
        return followUpRepository.findTodo(DataScope.fromUserContext());
    }

    public void create(FollowUp followUp) {
        checkCustomerVisible(followUp.getCustomerId());
        followUp.setCreateBy(UserContext.userId());
        followUpRepository.save(followUp);
    }

    public void update(FollowUp followUp) {
        FollowUp exists = followUpRepository.findById(followUp.getId());
        if (exists == null) {
            throw new BizException(404, "跟进记录不存在");
        }
        checkOwner(exists);
        checkCustomerVisible(followUp.getCustomerId());
        followUp.setCreateBy(exists.getCreateBy());
        followUp.setCreateTime(exists.getCreateTime());
        followUpRepository.update(followUp);
    }

    public void delete(Long id) {
        FollowUp exists = followUpRepository.findById(id);
        if (exists == null) {
            return;
        }
        checkOwner(exists);
        followUpRepository.removeById(id);
    }

    private void checkOwner(FollowUp followUp) {
        if (UserContext.isAdminOrManager()) {
            return;
        }
        if (!UserContext.userId().equals(followUp.getCreateBy())) {
            throw new BizException(403, "仅跟进人可操作");
        }
    }

    private void checkCustomerVisible(Long customerId) {
        if (UserContext.isAdminOrManager()) {
            return;
        }
        if (!customerVisibilityService.isVisible(customerId, UserContext.userId())) {
            throw new BizException(403, "无权操作该客户的跟进记录");
        }
    }
}
