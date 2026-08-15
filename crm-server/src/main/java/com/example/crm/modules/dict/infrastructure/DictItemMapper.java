package com.example.crm.modules.dict.infrastructure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crm.modules.dict.domain.model.SysDictItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DictItemMapper extends BaseMapper<SysDictItem> {
}
