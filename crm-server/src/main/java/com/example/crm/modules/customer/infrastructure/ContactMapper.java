package com.example.crm.modules.customer.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crm.modules.customer.domain.model.Contact;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContactMapper extends BaseMapper<Contact> {
}
