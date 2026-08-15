package com.example.crm.modules.user.domain.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.user.domain.model.SysUser;

public interface UserRepository {

    Page<SysUser> page(long current, long size, String keyword, Integer status, String roleCode);

    SysUser findById(Long id);

    SysUser findByUsername(String username);

    long countByUsername(String username);

    void save(SysUser user);

    void update(SysUser user);

    void removeById(Long id);
}
