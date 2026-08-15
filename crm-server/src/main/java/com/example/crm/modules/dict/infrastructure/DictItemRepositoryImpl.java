package com.example.crm.modules.dict.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.crm.modules.dict.domain.model.SysDictItem;
import com.example.crm.modules.dict.domain.repository.DictItemRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class DictItemRepositoryImpl implements DictItemRepository {

    private final DictItemMapper mapper;

    public DictItemRepositoryImpl(DictItemMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<SysDictItem> findByType(String dictType) {
        return mapper.selectList(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictType, dictType)
                .eq(SysDictItem::getStatus, 1)
                .orderByAsc(SysDictItem::getSort));
    }

    @Override
    public List<SysDictItem> findAll(String dictType, String keyword, Integer status) {
        return mapper.selectList(new LambdaQueryWrapper<SysDictItem>()
                .eq(StringUtils.hasText(dictType), SysDictItem::getDictType, dictType)
                .eq(status != null, SysDictItem::getStatus, status)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(SysDictItem::getDictLabel, keyword)
                        .or()
                        .like(SysDictItem::getDictValue, keyword))
                .orderByAsc(SysDictItem::getDictType)
                .orderByAsc(SysDictItem::getSort));
    }

    @Override
    public SysDictItem findById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public void save(SysDictItem item) {
        if (item.getId() == null) {
            mapper.insert(item);
        } else {
            mapper.updateById(item);
        }
    }

    @Override
    public void removeById(Long id) {
        mapper.deleteById(id);
    }
}
