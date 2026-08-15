package com.example.crm.modules.user.infrastructure;

import com.example.crm.modules.user.domain.model.SysRole;
import com.example.crm.modules.user.domain.model.SysUser;
import com.example.crm.modules.user.domain.model.SysUserRole;
import com.example.crm.modules.user.domain.repository.RoleRepository;
import com.example.crm.modules.user.domain.repository.UserRepository;
import com.example.crm.modules.user.domain.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminDataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataInitializer(UserRepository userRepository, RoleRepository roleRepository,
                                UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensureRoles();
        ensureAdmin();
    }

    private void ensureRoles() {
        String[][] defaults = {{"ADMIN", "管理员"}, {"MANAGER", "销售主管"}, {"SALES", "销售"}, {"GUEST", "访客"}};
        for (String[] item : defaults) {
            if (roleRepository.findByCode(item[0]) == null) {
                SysRole role = new SysRole();
                role.setRoleCode(item[0]);
                role.setRoleName(item[1]);
                roleRepository.save(role);
            }
        }
    }

    private void ensureAdmin() {
        SysUser admin = userRepository.findByUsername("admin");
        if (admin == null) {
            admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRealName("管理员");
            admin.setStatus(1);
            userRepository.save(admin);
            log.info("已创建默认管理员账号 admin / admin123（首次登录后请修改密码）");
        }
        SysRole adminRole = roleRepository.findByCode("ADMIN");
        if (adminRole != null) {
            boolean bound = userRoleRepository.findByUserId(admin.getId()).stream()
                    .anyMatch(r -> r.getRoleId().equals(adminRole.getId()));
            if (!bound) {
                SysUserRole relation = new SysUserRole();
                relation.setUserId(admin.getId());
                relation.setRoleId(adminRole.getId());
                userRoleRepository.save(relation);
            }
        }
    }
}
