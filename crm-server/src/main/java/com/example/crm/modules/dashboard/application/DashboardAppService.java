package com.example.crm.modules.dashboard.application;

import com.example.crm.modules.customer.domain.repository.CustomerRepository;
import com.example.crm.modules.followup.domain.repository.FollowUpRepository;
import com.example.crm.modules.opportunity.domain.repository.OpportunityRepository;
import com.example.crm.shared.api.DataScope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据看板：基于各聚合仓储的只读统计（CQRS 读模型）。
 */
@Service
public class DashboardAppService {

    private final CustomerRepository customerRepository;
    private final OpportunityRepository opportunityRepository;
    private final FollowUpRepository followUpRepository;

    public DashboardAppService(CustomerRepository customerRepository,
                               OpportunityRepository opportunityRepository,
                               FollowUpRepository followUpRepository) {
        this.customerRepository = customerRepository;
        this.opportunityRepository = opportunityRepository;
        this.followUpRepository = followUpRepository;
    }

    public Map<String, Object> summary() {
        DataScope scope = DataScope.fromUserContext();
        Map<String, Object> result = new HashMap<>();
        result.put("customerTotal", customerRepository.countAll(scope));
        result.put("customerMonthNew",
                customerRepository.countByTimeRange(scope, YearMonth.now().atDay(1).atStartOfDay(), null));
        result.put("opportunityTotal", opportunityRepository.countAll(scope));
        result.put("opportunityAmount", opportunityRepository.sumAmount(scope));
        result.put("followUpToday", followUpRepository.countByTimeRange(scope,
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay()));
        result.put("followUpTodo", followUpRepository.countTodo(scope));
        return result;
    }

    public List<Map<String, Object>> customerStats() {
        return mergeGroupCounts(customerRepository.countGroupByField(DataScope.fromUserContext(), "status"));
    }

    public List<Map<String, Object>> sourceStats() {
        return mergeGroupCounts(customerRepository.countGroupByField(DataScope.fromUserContext(), "source"));
    }

    public List<Map<String, Object>> opportunityStats() {
        return opportunityRepository.countGroupByStage(DataScope.fromUserContext());
    }

    public List<Map<String, Object>> trend() {
        DataScope scope = DataScope.fromUserContext();
        List<Map<String, Object>> result = new ArrayList<>();
        YearMonth current = YearMonth.now();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = current.minusMonths(i);
            LocalDateTime start = month.atDay(1).atStartOfDay();
            LocalDateTime end = month.plusMonths(1).atDay(1).atStartOfDay();
            Map<String, Object> item = new HashMap<>();
            item.put("month", month.toString());
            item.put("customerCount", customerRepository.countByTimeRange(scope, start, end));
            item.put("opportunityAmount", opportunityRepository.sumAmountByTimeRange(scope, start, end));
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> mergeGroupCounts(List<Map<String, Object>> rows) {
        Map<String, Long> merged = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Object name = row.get("name");
            Object cnt = row.get("cnt");
            merged.merge(name == null ? "(未填写)" : name.toString(),
                    cnt == null ? 0L : Long.parseLong(cnt.toString()), Long::sum);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        merged.forEach((name, cnt) -> result.add(Map.of("name", name, "cnt", cnt)));
        return result;
    }
}
