package com.example.crm.modules.customer.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.customer.application.CustomerQuery;
import com.example.crm.modules.customer.domain.model.Customer;
import com.example.crm.modules.customer.domain.repository.CustomerRepository;
import com.example.crm.shared.api.DataScope;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerMapper mapper;

    public CustomerRepositoryImpl(CustomerMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Page<Customer> page(CustomerQuery query, DataScope scope) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (!scope.includeAll()) {
            Long uid = scope.userId();
            wrapper.and(w -> w.eq(Customer::getOwnerId, uid).or().isNull(Customer::getOwnerId));
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(Customer::getName, query.getKeyword())
                    .or().like(Customer::getPhone, query.getKeyword()));
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Customer::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getSource())) {
            wrapper.eq(Customer::getSource, query.getSource());
        }
        if (StringUtils.hasText(query.getIndustry())) {
            wrapper.eq(Customer::getIndustry, query.getIndustry());
        }
        if (StringUtils.hasText(query.getLevel())) {
            wrapper.eq(Customer::getLevel, query.getLevel());
        }
        if (query.getOwnerId() != null) {
            wrapper.eq(Customer::getOwnerId, query.getOwnerId());
        } else if (query.isOnlyMine() && !scope.includeAll()) {
            wrapper.eq(Customer::getOwnerId, scope.userId());
        }
        if (query.getCreateTimeStart() != null && !query.getCreateTimeStart().isBlank()) {
            wrapper.ge(Customer::getCreateTime, LocalDateTime.parse(query.getCreateTimeStart() + "T00:00:00"));
        }
        if (query.getCreateTimeEnd() != null && !query.getCreateTimeEnd().isBlank()) {
            wrapper.le(Customer::getCreateTime, LocalDateTime.parse(query.getCreateTimeEnd() + "T23:59:59"));
        }
        wrapper.orderByDesc(Customer::getId);
        return mapper.selectPage(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public Customer findById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public void save(Customer customer) {
        mapper.insert(customer);
    }

    @Override
    public void update(Customer customer) {
        mapper.updateById(customer);
    }

    @Override
    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public long countByTimeRange(DataScope scope, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        applyScope(wrapper, scope);
        if (start != null) {
            wrapper.ge(Customer::getCreateTime, start);
        }
        if (end != null) {
            wrapper.lt(Customer::getCreateTime, end);
        }
        return mapper.selectCount(wrapper);
    }

    @Override
    public long countAll(DataScope scope) {
        return countByTimeRange(scope, null, null);
    }

    @Override
    public List<Map<String, Object>> countGroupByField(DataScope scope, String field) {
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        if (!scope.includeAll()) {
            Long uid = scope.userId();
            wrapper.and(w -> w.eq("owner_id", uid).or().isNull("owner_id"));
        }
        return mapper.selectMaps(wrapper.select(field + " as name", "count(*) as cnt").groupBy(field));
    }

    private void applyScope(LambdaQueryWrapper<Customer> wrapper, DataScope scope) {
        if (!scope.includeAll()) {
            Long uid = scope.userId();
            wrapper.and(w -> w.eq(Customer::getOwnerId, uid).or().isNull(Customer::getOwnerId));
        }
    }
}
