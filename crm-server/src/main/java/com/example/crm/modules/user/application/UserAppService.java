package com.example.crm.modules.user.application;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.modules.user.domain.model.SysRole;
import com.example.crm.modules.user.domain.model.SysUser;
import com.example.crm.modules.user.domain.model.SysUserRole;
import com.example.crm.modules.user.domain.repository.RoleRepository;
import com.example.crm.modules.user.domain.repository.UserRepository;
import com.example.crm.modules.user.domain.repository.UserRoleRepository;
import com.example.crm.modules.user.interfaces.dto.UserRequest;
import com.example.crm.shared.api.PageResult;
import com.example.crm.shared.audit.application.AuditService;
import com.example.crm.shared.exception.BizException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserAppService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthAppService authAppService;
    private final AuditService auditService;

    public UserAppService(UserRepository userRepository, RoleRepository roleRepository,
                          UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder,
                          AuthAppService authAppService, AuditService auditService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authAppService = authAppService;
        this.auditService = auditService;
    }

    public PageResult<Map<String, Object>> page(long current, long size, String keyword,
                                                Integer status, String roleCode) {
        Page<SysUser> page = userRepository.page(current, size, keyword, status, roleCode);
        List<Map<String, Object>> records = new ArrayList<>();
        for (SysUser user : page.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", user.getId());
            item.put("username", user.getUsername());
            item.put("realName", user.getRealName());
            item.put("phone", user.getPhone());
            item.put("email", user.getEmail());
            item.put("status", user.getStatus());
            item.put("createTime", user.getCreateTime());
            item.put("roleCodes", authAppService.findRoleCodes(user.getId()));
            records.add(item);
        }
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    public List<SysRole> listRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public void create(UserRequest request) {
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BizException("密码不能为空");
        }
        if (userRepository.countByUsername(request.getUsername().trim()) > 0) {
            throw new BizException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        userRepository.save(user);
        saveRoles(user.getId(), request.getRoleCodes());
        auditService.log("user", "create", user.getId(), "新增用户: " + user.getUsername());
    }

    @Transactional
    public void update(UserRequest request) {
        SysUser user = userRepository.findById(request.getId());
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        userRepository.update(user);
        if (request.getRoleCodes() != null) {
            userRoleRepository.removeByUserId(user.getId());
            saveRoles(user.getId(), request.getRoleCodes());
        }
        auditService.log("user", "update", user.getId(), "修改用户: " + user.getUsername());
    }

    public void updateStatus(Long id, Integer status) {
        SysUser user = userRepository.findById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        user.setStatus(status);
        userRepository.update(user);
        auditService.log("user", "status", id, "用户状态变更: " + status);
    }

    public void resetPassword(Long id, String newPassword) {
        SysUser user = userRepository.findById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.update(user);
        auditService.log("user", "reset-password", id, "重置用户密码");
    }

    public void delete(Long id) {
        SysUser user = userRepository.findById(id);
        if (user == null) {
            return;
        }
        if ("admin".equals(user.getUsername())) {
            throw new BizException("内置管理员不可删除");
        }
        userRepository.removeById(id);
        auditService.log("user", "delete", id, "删除用户: " + user.getUsername());
    }

    private void saveRoles(Long userId, List<String> roleCodes) {
        if (roleCodes == null) {
            return;
        }
        for (String code : roleCodes) {
            SysRole role = roleRepository.findByCode(code);
            if (role != null) {
                SysUserRole relation = new SysUserRole();
                relation.setUserId(userId);
                relation.setRoleId(role.getId());
                userRoleRepository.save(relation);
            }
        }
    }
}
