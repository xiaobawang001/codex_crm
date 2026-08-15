package com.example.crm.modules.dict.domain.repository;

import com.example.crm.modules.dict.domain.model.SysDictItem;

import java.util.List;

public interface DictItemRepository {

    List<SysDictItem> findByType(String dictType);

    List<SysDictItem> findAll(String dictType, String keyword, Integer status);

    SysDictItem findById(Long id);

    void save(SysDictItem item);

    void removeById(Long id);
}
