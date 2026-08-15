package com.example.crm.modules.user.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.crm.modules.user.domain.model.SysRole;
import com.example.crm.modules.user.domain.repository.RoleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper mapper;

    public RoleRepositoryImpl(RoleMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<SysRole> findAll() {
        return mapper.selectList(new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getId));
    }

    @Override
    public SysRole findByCode(String roleCode) {
        return mapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, roleCode));
    }

    @Override
    public SysRole findById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public void save(SysRole role) {
        mapper.insert(role);
    }
}
