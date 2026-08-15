package com.example.crm.modules.user.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.user.domain.model.SysUser;
import com.example.crm.modules.user.domain.model.SysRole;
import com.example.crm.modules.user.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper mapper;
    private final RoleMapper roleMapper;

    public UserRepositoryImpl(UserMapper mapper, RoleMapper roleMapper) {
        this.mapper = mapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public Page<SysUser> page(long current, long size, String keyword, Integer status, String roleCode) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword).or().like(SysUser::getRealName, keyword));
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        if (StringUtils.hasText(roleCode)) {
            SysRole role = roleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, roleCode));
            if (role != null) {
                wrapper.inSql(SysUser::getId,
                        "SELECT user_id FROM sys_user_role WHERE role_id = " + role.getId());
            } else {
                wrapper.eq(SysUser::getId, -1);
            }
        }
        wrapper.orderByDesc(SysUser::getId);
        return mapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public SysUser findById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public SysUser findByUsername(String username) {
        return mapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public long countByUsername(String username) {
        return mapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public void save(SysUser user) {
        mapper.insert(user);
    }

    @Override
    public void update(SysUser user) {
        mapper.updateById(user);
    }

    @Override
    public void removeById(Long id) {
        mapper.deleteById(id);
    }
}
