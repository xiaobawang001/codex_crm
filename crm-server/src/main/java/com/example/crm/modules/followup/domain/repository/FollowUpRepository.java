package com.example.crm.modules.followup.domain.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.followup.application.FollowUpQuery;
import com.example.crm.modules.followup.domain.model.FollowUp;
import com.example.crm.shared.api.DataScope;

import java.time.LocalDateTime;
import java.util.List;

public interface FollowUpRepository {

    Page<FollowUp> page(FollowUpQuery query, DataScope scope);

    List<FollowUp> findTodo(DataScope scope);

    FollowUp findById(Long id);

    void save(FollowUp followUp);

    void update(FollowUp followUp);

    void removeById(Long id);

    long countByTimeRange(DataScope scope, LocalDateTime start, LocalDateTime end);

    long countTodo(DataScope scope);
}
