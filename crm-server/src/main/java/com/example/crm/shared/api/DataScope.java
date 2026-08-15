package com.example.crm.shared.api;

import com.example.crm.shared.auth.UserContext;

/**
 * 数据权限范围：includeAll=true 表示可访问全部数据（管理员/主管），
 * 否则仅限 userId 本人拥有或公海（owner 为空）的数据。
 */
public record DataScope(Long userId, boolean includeAll) {

    public static DataScope fromUserContext() {
        return new DataScope(UserContext.userId(), UserContext.isAdminOrManager());
    }
}
