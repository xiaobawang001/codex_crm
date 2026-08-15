package com.example.crm.modules.opportunity.interfaces.rest;

import com.example.crm.modules.opportunity.application.OpportunityAppService;
import com.example.crm.modules.opportunity.application.OpportunityQuery;
import com.example.crm.modules.opportunity.domain.model.Opportunity;
import com.example.crm.shared.api.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityAppService opportunityAppService;

    public OpportunityController(OpportunityAppService opportunityAppService) {
        this.opportunityAppService = opportunityAppService;
    }

    @GetMapping
    public Result<Object> page(@RequestParam(defaultValue = "1") long current,
                               @RequestParam(defaultValue = "10") long size,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Long customerId,
                               @RequestParam(required = false) String stage,
                               @RequestParam(required = false) Long ownerId,
                               @RequestParam(required = false) BigDecimal amountMin,
                               @RequestParam(required = false) BigDecimal amountMax,
                               @RequestParam(required = false) String expectedCloseStart,
                               @RequestParam(required = false) String expectedCloseEnd) {
        OpportunityQuery query = new OpportunityQuery();
        query.setCurrent(current);
        query.setSize(size);
        query.setKeyword(keyword);
        query.setCustomerId(customerId);
        query.setStage(stage);
        query.setOwnerId(ownerId);
        query.setAmountMin(amountMin);
        query.setAmountMax(amountMax);
        query.setExpectedCloseStart(expectedCloseStart);
        query.setExpectedCloseEnd(expectedCloseEnd);
        return Result.ok(opportunityAppService.page(query));
    }

    @GetMapping("/{id}")
    public Result<Opportunity> detail(@PathVariable Long id) {
        return Result.ok(opportunityAppService.detail(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody Opportunity opportunity) {
        opportunityAppService.create(opportunity);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody Opportunity opportunity) {
        opportunity.setId(id);
        opportunityAppService.update(opportunity);
        return Result.ok();
    }

    @PutMapping("/{id}/stage")
    public Result<Void> changeStage(@PathVariable Long id, @RequestBody Map<String, String> body) {
        opportunityAppService.changeStage(id, body.get("stage"));
        return Result.ok();
    }

    @PutMapping("/{id}/win")
    public Result<Void> win(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        BigDecimal amount = body == null || body.get("winAmount") == null
                ? null : new BigDecimal(body.get("winAmount").toString());
        opportunityAppService.win(id, amount);
        return Result.ok();
    }

    @PutMapping("/{id}/lose")
    public Result<Void> lose(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        String reason = body == null ? null : (String) body.get("loseReason");
        opportunityAppService.lose(id, reason);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        opportunityAppService.delete(id);
        return Result.ok();
    }
}
