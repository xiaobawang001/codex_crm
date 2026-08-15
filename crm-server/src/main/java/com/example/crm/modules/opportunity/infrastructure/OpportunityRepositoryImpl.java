package com.example.crm.modules.opportunity.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.opportunity.application.OpportunityQuery;
import com.example.crm.modules.opportunity.domain.model.Opportunity;
import com.example.crm.modules.opportunity.domain.repository.OpportunityRepository;
import com.example.crm.shared.api.DataScope;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class OpportunityRepositoryImpl implements OpportunityRepository {

    private final OpportunityMapper mapper;

    public OpportunityRepositoryImpl(OpportunityMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Page<Opportunity> page(OpportunityQuery query, DataScope scope) {
        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();
        if (query.getCustomerId() != null) {
            wrapper.eq(Opportunity::getCustomerId, query.getCustomerId());
        } else if (!scope.includeAll()) {
            applyOpportunityScope(wrapper, scope.userId());
        }
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(Opportunity::getName, query.getKeyword());
        }
        if (query.getStage() != null && !query.getStage().isBlank()) {
            wrapper.eq(Opportunity::getStage, query.getStage());
        }
        if (query.getOwnerId() != null) {
            wrapper.eq(Opportunity::getOwnerId, query.getOwnerId());
        }
        if (query.getAmountMin() != null) {
            wrapper.ge(Opportunity::getAmount, query.getAmountMin());
        }
        if (query.getAmountMax() != null) {
            wrapper.le(Opportunity::getAmount, query.getAmountMax());
        }
        if (query.getExpectedCloseStart() != null && !query.getExpectedCloseStart().isBlank()) {
            wrapper.ge(Opportunity::getExpectedCloseDate, LocalDate.parse(query.getExpectedCloseStart()));
        }
        if (query.getExpectedCloseEnd() != null && !query.getExpectedCloseEnd().isBlank()) {
            wrapper.le(Opportunity::getExpectedCloseDate, LocalDate.parse(query.getExpectedCloseEnd()));
        }
        wrapper.orderByDesc(Opportunity::getId);
        return mapper.selectPage(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public Opportunity findById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public void save(Opportunity opportunity) {
        mapper.insert(opportunity);
    }

    @Override
    public void update(Opportunity opportunity) {
        mapper.updateById(opportunity);
    }

    @Override
    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public long countAll(DataScope scope) {
        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();
        if (!scope.includeAll()) {
            applyOpportunityScope(wrapper, scope.userId());
        }
        return mapper.selectCount(wrapper);
    }

    @Override
    public BigDecimal sumAmount(DataScope scope) {
        return sum(buildScopeWrapper(scope));
    }

    @Override
    public BigDecimal sumAmountByTimeRange(DataScope scope, LocalDateTime start, LocalDateTime end) {
        QueryWrapper<Opportunity> wrapper = buildScopeWrapper(scope);
        wrapper.ge("create_time", start).lt("create_time", end);
        return sum(wrapper);
    }

    @Override
    public List<Map<String, Object>> countGroupByStage(DataScope scope) {
        QueryWrapper<Opportunity> wrapper = new QueryWrapper<>();
        if (!scope.includeAll()) {
            Long uid = scope.userId();
            wrapper.and(w -> w.eq("owner_id", uid)
                    .or().isNull("owner_id")
                    .or().inSql("customer_id",
                            "SELECT id FROM customer WHERE deleted = 0 AND owner_id = " + uid));
        }
        return mapper.selectMaps(wrapper.select("stage", "count(*) as cnt", "COALESCE(sum(amount), 0) as amount")
                .groupBy("stage").orderByAsc("stage"));
    }

    private BigDecimal sum(QueryWrapper<Opportunity> wrapper) {
        List<Map<String, Object>> rows = mapper.selectMaps(
                wrapper.select("COALESCE(sum(amount), 0) as total"));
        Object total = rows.isEmpty() ? null : rows.get(0).get("total");
        return total == null ? BigDecimal.ZERO : new BigDecimal(total.toString());
    }

    private QueryWrapper<Opportunity> buildScopeWrapper(DataScope scope) {
        QueryWrapper<Opportunity> wrapper = new QueryWrapper<>();
        if (!scope.includeAll()) {
            Long uid = scope.userId();
            wrapper.and(w -> w.eq("owner_id", uid)
                    .or().isNull("owner_id")
                    .or().inSql("customer_id",
                            "SELECT id FROM customer WHERE deleted = 0 AND owner_id = " + uid));
        }
        return wrapper;
    }

    private void applyOpportunityScope(LambdaQueryWrapper<Opportunity> wrapper, Long uid) {
        wrapper.and(w -> w.eq(Opportunity::getOwnerId, uid)
                .or().isNull(Opportunity::getOwnerId)
                .or().inSql(Opportunity::getCustomerId,
                        "SELECT id FROM customer WHERE deleted = 0 AND owner_id = " + uid));
    }
}
