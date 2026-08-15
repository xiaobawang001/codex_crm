package com.example.crm.modules.followup.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.followup.application.FollowUpQuery;
import com.example.crm.modules.followup.domain.model.FollowUp;
import com.example.crm.modules.followup.domain.repository.FollowUpRepository;
import com.example.crm.shared.api.DataScope;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public class FollowUpRepositoryImpl implements FollowUpRepository {

    private final FollowUpMapper mapper;

    public FollowUpRepositoryImpl(FollowUpMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Page<FollowUp> page(FollowUpQuery query, DataScope scope) {
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        if (query.getCustomerId() != null) {
            wrapper.eq(FollowUp::getCustomerId, query.getCustomerId());
        } else if (!scope.includeAll()) {
            wrapper.inSql(FollowUp::getCustomerId, visibleCustomerIdsSql(scope.userId()));
        }
        if (query.getContactId() != null) {
            wrapper.eq(FollowUp::getContactId, query.getContactId());
        }
        if (query.getStartDate() != null && !query.getStartDate().isBlank()) {
            wrapper.ge(FollowUp::getCreateTime, LocalDateTime.parse(query.getStartDate() + "T00:00:00"));
        }
        if (query.getEndDate() != null && !query.getEndDate().isBlank()) {
            wrapper.le(FollowUp::getCreateTime, LocalDateTime.parse(query.getEndDate() + "T23:59:59"));
        }
        if (query.getType() != null && !query.getType().isBlank()) {
            wrapper.eq(FollowUp::getType, query.getType());
        }
        if (query.getCreateBy() != null) {
            wrapper.eq(FollowUp::getCreateBy, query.getCreateBy());
        }
        wrapper.orderByDesc(FollowUp::getCreateTime);
        return mapper.selectPage(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public List<FollowUp> findTodo(DataScope scope) {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        if (!scope.includeAll()) {
            wrapper.inSql(FollowUp::getCustomerId, visibleCustomerIdsSql(scope.userId()));
        }
        wrapper.isNotNull(FollowUp::getNextFollowTime)
                .gt(FollowUp::getNextFollowTime, now)
                .le(FollowUp::getNextFollowTime, now.toLocalDate().atTime(LocalTime.MAX))
                .orderByAsc(FollowUp::getNextFollowTime)
                .last("LIMIT 50");
        return mapper.selectList(wrapper);
    }

    @Override
    public FollowUp findById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public void save(FollowUp followUp) {
        mapper.insert(followUp);
    }

    @Override
    public void update(FollowUp followUp) {
        mapper.updateById(followUp);
    }

    @Override
    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public long countByTimeRange(DataScope scope, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        if (!scope.includeAll()) {
            wrapper.inSql(FollowUp::getCustomerId, visibleCustomerIdsSql(scope.userId()));
        }
        wrapper.ge(FollowUp::getCreateTime, start).lt(FollowUp::getCreateTime, end);
        return mapper.selectCount(wrapper);
    }

    @Override
    public long countTodo(DataScope scope) {
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        if (!scope.includeAll()) {
            wrapper.inSql(FollowUp::getCustomerId, visibleCustomerIdsSql(scope.userId()));
        }
        wrapper.isNotNull(FollowUp::getNextFollowTime).le(FollowUp::getNextFollowTime, LocalDateTime.now());
        return mapper.selectCount(wrapper);
    }

    private String visibleCustomerIdsSql(Long uid) {
        return "SELECT id FROM customer WHERE deleted = 0 AND (owner_id = " + uid + " OR owner_id IS NULL)";
    }
}
