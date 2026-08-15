package com.example.crm.modules.customer.application;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.customer.domain.model.Contact;
import com.example.crm.modules.customer.domain.repository.ContactRepository;
import com.example.crm.modules.customer.domain.service.CustomerVisibilityService;
import com.example.crm.shared.api.DataScope;
import com.example.crm.shared.api.PageResult;
import com.example.crm.shared.auth.UserContext;
import com.example.crm.shared.exception.BizException;
import org.springframework.stereotype.Service;

@Service
public class ContactAppService {

    private final ContactRepository contactRepository;
    private final CustomerVisibilityService customerVisibilityService;

    public ContactAppService(ContactRepository contactRepository,
                             CustomerVisibilityService customerVisibilityService) {
        this.contactRepository = contactRepository;
        this.customerVisibilityService = customerVisibilityService;
    }

    public PageResult<Contact> page(ContactQuery query) {
        if (query.getCustomerId() != null) {
            checkCustomerVisible(query.getCustomerId());
        }
        Page<Contact> page = contactRepository.page(query, DataScope.fromUserContext());
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    public Contact detail(Long id) {
        Contact contact = contactRepository.findById(id);
        if (contact == null) {
            throw new BizException(404, "联系人不存在");
        }
        checkCustomerVisible(contact.getCustomerId());
        return contact;
    }

    public void create(Contact contact) {
        checkCustomerVisible(contact.getCustomerId());
        contactRepository.save(contact);
    }

    public void update(Contact contact) {
        detail(contact.getId());
        contactRepository.update(contact);
    }

    public void delete(Long id) {
        detail(id);
        contactRepository.removeById(id);
    }

    private void checkCustomerVisible(Long customerId) {
        if (UserContext.isAdminOrManager()) {
            return;
        }
        if (!customerVisibilityService.isVisible(customerId, UserContext.userId())) {
            throw new BizException(403, "无权访问该客户下的联系人");
        }
    }
}
