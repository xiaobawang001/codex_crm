package com.example.crm.modules.user.domain.repository;

import com.example.crm.modules.user.domain.model.SysRole;

import java.util.List;

public interface RoleRepository {

    List<SysRole> findAll();

    SysRole findByCode(String roleCode);

    SysRole findById(Long id);

    void save(SysRole role);
}
