package com.example.crm.modules.customer.domain.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.customer.application.CustomerQuery;
import com.example.crm.modules.customer.domain.model.Customer;
import com.example.crm.shared.api.DataScope;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CustomerRepository {

    Page<Customer> page(CustomerQuery query, DataScope scope);

    Customer findById(Long id);

    void save(Customer customer);

    void update(Customer customer);

    void removeById(Long id);

    long countByTimeRange(DataScope scope, LocalDateTime start, LocalDateTime end);

    long countAll(DataScope scope);

    List<Map<String, Object>> countGroupByField(DataScope scope, String field);
}
