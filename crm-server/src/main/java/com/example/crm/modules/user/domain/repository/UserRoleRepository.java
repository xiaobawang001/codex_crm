package com.example.crm.modules.user.domain.repository;

import com.example.crm.modules.user.domain.model.SysUserRole;

import java.util.List;

public interface UserRoleRepository {

    List<SysUserRole> findByUserId(Long userId);

    void save(SysUserRole relation);

    void removeByUserId(Long userId);
}
