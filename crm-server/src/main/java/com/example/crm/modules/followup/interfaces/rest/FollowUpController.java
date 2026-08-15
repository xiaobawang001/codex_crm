package com.example.crm.modules.followup.interfaces.rest;

import com.example.crm.modules.followup.application.FollowUpAppService;
import com.example.crm.modules.followup.application.FollowUpQuery;
import com.example.crm.modules.followup.domain.model.FollowUp;
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

import java.util.List;

@RestController
@RequestMapping("/api/follow-ups")
public class FollowUpController {

    private final FollowUpAppService followUpAppService;

    public FollowUpController(FollowUpAppService followUpAppService) {
        this.followUpAppService = followUpAppService;
    }

    @GetMapping
    public Result<Object> page(@RequestParam(defaultValue = "1") long current,
                               @RequestParam(defaultValue = "10") long size,
                               @RequestParam(required = false) Long customerId,
                               @RequestParam(required = false) Long contactId,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate,
                               @RequestParam(required = false) String type,
                               @RequestParam(required = false) Long createBy) {
        FollowUpQuery query = new FollowUpQuery();
        query.setCurrent(current);
        query.setSize(size);
        query.setCustomerId(customerId);
        query.setContactId(contactId);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setType(type);
        query.setCreateBy(createBy);
        return Result.ok(followUpAppService.page(query));
    }

    @GetMapping("/todo")
    public Result<List<FollowUp>> todo() {
        return Result.ok(followUpAppService.todo());
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody FollowUp followUp) {
        followUpAppService.create(followUp);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FollowUp followUp) {
        followUp.setId(id);
        followUpAppService.update(followUp);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        followUpAppService.delete(id);
        return Result.ok();
    }
}
