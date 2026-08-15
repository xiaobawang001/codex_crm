package com.example.crm.modules.customer.interfaces.rest;

import com.example.crm.modules.customer.application.CustomerAppService;
import com.example.crm.modules.customer.application.CustomerQuery;
import com.example.crm.modules.customer.domain.model.Customer;
import com.example.crm.modules.customer.interfaces.dto.AssignRequest;
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

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerAppService customerAppService;

    public CustomerController(CustomerAppService customerAppService) {
        this.customerAppService = customerAppService;
    }

    @GetMapping
    public Result<Object> page(@RequestParam(defaultValue = "1") long current,
                               @RequestParam(defaultValue = "10") long size,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) String source,
                               @RequestParam(required = false) String industry,
                               @RequestParam(required = false) String level,
                               @RequestParam(required = false) Long ownerId,
                               @RequestParam(required = false) Boolean onlyMine,
                               @RequestParam(required = false) String createTimeStart,
                               @RequestParam(required = false) String createTimeEnd) {
        CustomerQuery query = new CustomerQuery();
        query.setCurrent(current);
        query.setSize(size);
        query.setKeyword(keyword);
        query.setStatus(status);
        query.setSource(source);
        query.setIndustry(industry);
        query.setLevel(level);
        query.setOwnerId(ownerId);
        query.setOnlyMine(Boolean.TRUE.equals(onlyMine));
        query.setCreateTimeStart(createTimeStart);
        query.setCreateTimeEnd(createTimeEnd);
        return Result.ok(customerAppService.page(query));
    }

    @GetMapping("/{id}")
    public Result<Customer> detail(@PathVariable Long id) {
        return Result.ok(customerAppService.detail(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody Customer customer) {
        customerAppService.create(customer);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        customer.setId(id);
        customerAppService.update(customer);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        customerAppService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/claim")
    public Result<Void> claim(@PathVariable Long id) {
        customerAppService.claim(id);
        return Result.ok();
    }

    @PutMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Long id, @Valid @RequestBody AssignRequest request) {
        customerAppService.assign(id, request.getUserId());
        return Result.ok();
    }
}
