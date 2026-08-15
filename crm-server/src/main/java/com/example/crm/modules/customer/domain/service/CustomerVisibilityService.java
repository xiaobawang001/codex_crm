package com.example.crm.modules.customer.domain.service;

import com.example.crm.modules.customer.domain.model.Customer;
import com.example.crm.modules.customer.domain.repository.CustomerRepository;
import org.springframework.stereotype.Service;

/**
 * 客户数据可见性领域服务：判断某客户对指定用户是否可见（本人拥有或公海）。
 * 供联系人、跟进、商机等相邻聚合做数据权限校验。
 */
@Service
public class CustomerVisibilityService {

    private final CustomerRepository customerRepository;

    public CustomerVisibilityService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public boolean isVisible(Long customerId, Long userId) {
        Customer customer = customerRepository.findById(customerId);
        return customer != null
                && (customer.getOwnerId() == null || customer.getOwnerId().equals(userId));
    }
}
