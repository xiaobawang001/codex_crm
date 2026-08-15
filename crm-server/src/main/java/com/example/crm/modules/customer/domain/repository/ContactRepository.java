package com.example.crm.modules.customer.domain.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.customer.application.ContactQuery;
import com.example.crm.modules.customer.domain.model.Contact;
import com.example.crm.shared.api.DataScope;

public interface ContactRepository {

    Page<Contact> page(ContactQuery query, DataScope scope);

    Contact findById(Long id);

    void save(Contact contact);

    void update(Contact contact);

    void removeById(Long id);
}
