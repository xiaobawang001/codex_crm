package com.example.crm.modules.dashboard.interfaces.rest;

import com.example.crm.modules.dashboard.application.DashboardAppService;
import com.example.crm.shared.api.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardAppService dashboardAppService;

    public DashboardController(DashboardAppService dashboardAppService) {
        this.dashboardAppService = dashboardAppService;
    }

    @GetMapping("/summary")
    public Result<Map<String, Object>> summary() {
        return Result.ok(dashboardAppService.summary());
    }

    @GetMapping("/customer-stats")
    public Result<List<Map<String, Object>>> customerStats() {
        return Result.ok(dashboardAppService.customerStats());
    }

    @GetMapping("/source-stats")
    public Result<List<Map<String, Object>>> sourceStats() {
        return Result.ok(dashboardAppService.sourceStats());
    }

    @GetMapping("/opportunity-stats")
    public Result<List<Map<String, Object>>> opportunityStats() {
        return Result.ok(dashboardAppService.opportunityStats());
    }

    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> trend() {
        return Result.ok(dashboardAppService.trend());
    }
}
