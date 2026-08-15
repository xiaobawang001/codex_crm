package com.example.crm.modules.user.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.crm.modules.user.domain.model.SysUserRole;
import com.example.crm.modules.user.domain.repository.UserRoleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private final UserRoleMapper mapper;

    public UserRoleRepositoryImpl(UserRoleMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<SysUserRole> findByUserId(Long userId) {
        return mapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
    }

    @Override
    public void save(SysUserRole relation) {
        mapper.insert(relation);
    }

    @Override
    public void removeByUserId(Long userId) {
        mapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
    }
}
