package com.example.crm.modules.opportunity.domain.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.opportunity.application.OpportunityQuery;
import com.example.crm.modules.opportunity.domain.model.Opportunity;
import com.example.crm.shared.api.DataScope;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OpportunityRepository {

    Page<Opportunity> page(OpportunityQuery query, DataScope scope);

    Opportunity findById(Long id);

    void save(Opportunity opportunity);

    void update(Opportunity opportunity);

    void removeById(Long id);

    long countAll(DataScope scope);

    BigDecimal sumAmount(DataScope scope);

    BigDecimal sumAmountByTimeRange(DataScope scope, LocalDateTime start, LocalDateTime end);

    List<Map<String, Object>> countGroupByStage(DataScope scope);
}
