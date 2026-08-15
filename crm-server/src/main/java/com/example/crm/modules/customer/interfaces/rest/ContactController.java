package com.example.crm.modules.customer.interfaces.rest;

import com.example.crm.modules.customer.application.ContactAppService;
import com.example.crm.modules.customer.application.ContactQuery;
import com.example.crm.modules.customer.domain.model.Contact;
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
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactAppService contactAppService;

    public ContactController(ContactAppService contactAppService) {
        this.contactAppService = contactAppService;
    }

    @GetMapping
    public Result<Object> page(@RequestParam(defaultValue = "1") long current,
                               @RequestParam(defaultValue = "10") long size,
                               @RequestParam(required = false) Long customerId,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String position) {
        ContactQuery query = new ContactQuery();
        query.setCurrent(current);
        query.setSize(size);
        query.setCustomerId(customerId);
        query.setKeyword(keyword);
        query.setPosition(position);
        return Result.ok(contactAppService.page(query));
    }

    @GetMapping("/{id}")
    public Result<Contact> detail(@PathVariable Long id) {
        return Result.ok(contactAppService.detail(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody Contact contact) {
        contactAppService.create(contact);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody Contact contact) {
        contact.setId(id);
        contactAppService.update(contact);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        contactAppService.delete(id);
        return Result.ok();
    }
}
