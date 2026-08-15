package com.example.crm.modules.user.application;

import com.example.crm.modules.user.domain.model.SysRole;
import com.example.crm.modules.user.domain.model.SysUser;
import com.example.crm.modules.user.domain.model.SysUserRole;
import com.example.crm.modules.user.domain.repository.RoleRepository;
import com.example.crm.modules.user.domain.repository.UserRepository;
import com.example.crm.modules.user.domain.repository.UserRoleRepository;
import com.example.crm.modules.user.interfaces.dto.LoginRequest;
import com.example.crm.modules.user.interfaces.dto.LoginResponse;
import com.example.crm.modules.user.interfaces.dto.PasswordRequest;
import com.example.crm.security.JwtUtil;
import com.example.crm.shared.auth.UserContext;
import com.example.crm.shared.exception.BizException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AuthAppService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthAppService(UserRepository userRepository, RoleRepository roleRepository,
                          UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        SysUser user = userRepository.findByUsername(request.getUsername().trim());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException("账号已停用，请联系管理员");
        }
        Set<String> roles = findRoleCodes(user.getId());
        String token = jwtUtil.createToken(user.getId(), user.getUsername(), user.getRealName(), roles);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("roles", roles);
        return new LoginResponse(token, userInfo);
    }

    public void changePassword(PasswordRequest request) {
        SysUser user = userRepository.findById(UserContext.userId());
        if (user == null || !passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BizException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.update(user);
    }

    public Set<String> findRoleCodes(Long userId) {
        List<SysUserRole> relations = userRoleRepository.findByUserId(userId);
        Set<String> roles = new HashSet<>();
        for (SysUserRole relation : relations) {
            SysRole role = roleRepository.findById(relation.getRoleId());
            if (role != null) {
                roles.add(role.getRoleCode());
            }
        }
        return roles;
    }
}
