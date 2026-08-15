package com.example.crm.modules.customer.application;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.customer.domain.model.Customer;
import com.example.crm.modules.customer.domain.repository.CustomerRepository;
import com.example.crm.shared.api.DataScope;
import com.example.crm.shared.api.PageResult;
import com.example.crm.shared.audit.application.AuditService;
import com.example.crm.shared.auth.UserContext;
import com.example.crm.shared.exception.BizException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerAppService {

    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    public CustomerAppService(CustomerRepository customerRepository, AuditService auditService) {
        this.customerRepository = customerRepository;
        this.auditService = auditService;
    }

    public PageResult<Customer> page(CustomerQuery query) {
        Page<Customer> page = customerRepository.page(query, DataScope.fromUserContext());
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    public Customer detail(Long id) {
        Customer customer = customerRepository.findById(id);
        if (customer == null) {
            throw new BizException(404, "客户不存在");
        }
        checkVisible(customer);
        return customer;
    }

    public void create(Customer customer) {
        if (customer.getStatus() == null) {
            customer.setStatus("potential");
        }
        if (customer.getOwnerId() == null && !UserContext.isAdminOrManager()) {
            customer.setOwnerId(UserContext.userId());
        }
        customerRepository.save(customer);
        auditService.log("customer", "create", customer.getId(), "新增客户: " + customer.getName());
    }

    public void update(Customer customer) {
        Customer exists = detail(customer.getId());
        checkEditable(exists);
        customer.setOwnerId(exists.getOwnerId());
        customerRepository.update(customer);
        auditService.log("customer", "update", customer.getId(), "修改客户: " + customer.getName());
    }

    public void delete(Long id) {
        Customer customer = detail(id);
        checkEditable(customer);
        customerRepository.removeById(id);
        auditService.log("customer", "delete", id, "删除客户: " + customer.getName());
    }

    @Transactional
    public void claim(Long id) {
        Customer customer = detail(id);
        if (customer.getOwnerId() != null) {
            throw new BizException("该客户已被领取");
        }
        customer.setOwnerId(UserContext.userId());
        customerRepository.update(customer);
        auditService.log("customer", "claim", id, "领取公海客户: " + customer.getName());
    }

    @Transactional
    public void assign(Long id, Long userId) {
        if (!UserContext.isAdminOrManager()) {
            throw new BizException(403, "无权限执行分配操作");
        }
        Customer customer = detail(id);
        customer.setOwnerId(userId);
        customerRepository.update(customer);
        auditService.log("customer", "assign", id,
                "分配客户: " + customer.getName() + " -> 用户 " + userId);
    }

    private void checkVisible(Customer customer) {
        if (UserContext.isAdminOrManager()) {
            return;
        }
        if (customer.getOwnerId() != null && !customer.getOwnerId().equals(UserContext.userId())) {
            throw new BizException(403, "无权访问该客户");
        }
    }

    private void checkEditable(Customer customer) {
        if (UserContext.isAdminOrManager()) {
            return;
        }
        if (!UserContext.userId().equals(customer.getOwnerId())) {
            throw new BizException(403, "仅客户负责人可操作");
        }
    }
}
