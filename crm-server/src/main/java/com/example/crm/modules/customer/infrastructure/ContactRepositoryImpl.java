package com.example.crm.modules.customer.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.customer.application.ContactQuery;
import com.example.crm.modules.customer.domain.model.Contact;
import com.example.crm.modules.customer.domain.repository.ContactRepository;
import com.example.crm.shared.api.DataScope;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class ContactRepositoryImpl implements ContactRepository {

    private final ContactMapper mapper;

    public ContactRepositoryImpl(ContactMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Page<Contact> page(ContactQuery query, DataScope scope) {
        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<>();
        if (query.getCustomerId() != null) {
            wrapper.eq(Contact::getCustomerId, query.getCustomerId());
        } else if (!scope.includeAll()) {
            wrapper.inSql(Contact::getCustomerId, visibleCustomerIdsSql(scope.userId()));
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(Contact::getName, query.getKeyword())
                    .or().like(Contact::getPhone, query.getKeyword()));
        }
        if (StringUtils.hasText(query.getPosition())) {
            wrapper.like(Contact::getPosition, query.getPosition());
        }
        wrapper.orderByDesc(Contact::getId);
        return mapper.selectPage(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public Contact findById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public void save(Contact contact) {
        mapper.insert(contact);
    }

    @Override
    public void update(Contact contact) {
        mapper.updateById(contact);
    }

    @Override
    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    static String visibleCustomerIdsSql(Long uid) {
        return "SELECT id FROM customer WHERE deleted = 0 AND (owner_id = " + uid + " OR owner_id IS NULL)";
    }
}
