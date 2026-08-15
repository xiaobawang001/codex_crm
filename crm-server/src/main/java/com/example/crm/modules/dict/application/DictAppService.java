package com.example.crm.modules.dict.application;

import com.example.crm.modules.dict.domain.model.SysDictItem;
import com.example.crm.modules.dict.domain.repository.DictItemRepository;
import com.example.crm.shared.exception.BizException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictAppService {

    private final DictItemRepository dictItemRepository;

    public DictAppService(DictItemRepository dictItemRepository) {
        this.dictItemRepository = dictItemRepository;
    }

    public List<SysDictItem> listByType(String dictType) {
        return dictItemRepository.findByType(dictType);
    }

    public List<SysDictItem> listAll(String dictType, String keyword, Integer status) {
        return dictItemRepository.findAll(dictType, keyword, status);
    }

    public void save(SysDictItem item) {
        if (item.getSort() == null) {
            item.setSort(0);
        }
        if (item.getStatus() == null) {
            item.setStatus(1);
        }
        dictItemRepository.save(item);
    }

    public void delete(Long id) {
        if (dictItemRepository.findById(id) == null) {
            throw new BizException(404, "字典项不存在");
        }
        dictItemRepository.removeById(id);
    }
}
