-- ============================================================
-- 云客 CRM 系统 数据库初始化脚本
-- 数据库: PostgreSQL 16+（当前环境 17.5）
-- 执行方式: psql -h <host> -p <port> -U <user> -d <database> -f init.sql
-- 幂等性: 可重复执行（CREATE TABLE IF NOT EXISTS / ON CONFLICT DO NOTHING）
-- 说明: 所有业务表采用逻辑删除（deleted 字段），不物理删除
-- ============================================================

-- ------------------------------------------------------------
-- 1. 用户表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    real_name   VARCHAR(50),
    phone       VARCHAR(20),
    email       VARCHAR(100),
    avatar      VARCHAR(255),
    status      SMALLINT     NOT NULL DEFAULT 1,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE sys_user IS '系统用户';
COMMENT ON COLUMN sys_user.status IS '1 启用 / 0 停用';
COMMENT ON COLUMN sys_user.deleted IS '逻辑删除 0/1';

-- ------------------------------------------------------------
-- 2. 角色表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_role (
    id        BIGSERIAL PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(50) NOT NULL,
    remark    VARCHAR(255)
);
COMMENT ON TABLE sys_role IS '角色';

-- ------------------------------------------------------------
-- 3. 用户-角色关联表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user_role (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
);
CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON sys_user_role (user_id);
COMMENT ON TABLE sys_user_role IS '用户角色关联';

-- ------------------------------------------------------------
-- 4. 客户表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customer (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    industry    VARCHAR(50),
    source      VARCHAR(50),
    level       VARCHAR(20),
    status      VARCHAR(20) DEFAULT 'potential',
    phone       VARCHAR(20),
    email       VARCHAR(100),
    address     VARCHAR(255),
    website     VARCHAR(255),
    owner_id    BIGINT,
    remark      VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT  NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_customer_owner_id ON customer (owner_id);
CREATE INDEX IF NOT EXISTS idx_customer_name ON customer (name);
CREATE INDEX IF NOT EXISTS idx_customer_status ON customer (status);
COMMENT ON TABLE customer IS '客户';
COMMENT ON COLUMN customer.owner_id IS '所有者用户ID，NULL 表示公海';

-- ------------------------------------------------------------
-- 5. 联系人表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS contact (
    id          BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    name        VARCHAR(50) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    position    VARCHAR(50),
    wechat      VARCHAR(50),
    is_primary  SMALLINT NOT NULL DEFAULT 0,
    remark      VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT  NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_contact_customer_id ON contact (customer_id);
CREATE INDEX IF NOT EXISTS idx_contact_phone ON contact (phone);
COMMENT ON TABLE contact IS '联系人';
COMMENT ON COLUMN contact.is_primary IS '是否主要联系人 0/1';

-- ------------------------------------------------------------
-- 6. 跟进记录表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS follow_up (
    id               BIGSERIAL PRIMARY KEY,
    customer_id      BIGINT NOT NULL,
    contact_id       BIGINT,
    type             VARCHAR(20),
    content          TEXT NOT NULL,
    next_follow_time TIMESTAMP,
    create_by        BIGINT,
    create_time      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted          SMALLINT  NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_follow_up_customer_id ON follow_up (customer_id);
CREATE INDEX IF NOT EXISTS idx_follow_up_create_by ON follow_up (create_by);
CREATE INDEX IF NOT EXISTS idx_follow_up_next_time ON follow_up (next_follow_time);
COMMENT ON TABLE follow_up IS '跟进记录';

-- ------------------------------------------------------------
-- 7. 商机表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS opportunity (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    customer_id         BIGINT NOT NULL,
    amount              DECIMAL(12,2),
    stage               VARCHAR(20) NOT NULL DEFAULT 'contact',
    expected_close_date DATE,
    win_amount          DECIMAL(12,2),
    lose_reason         VARCHAR(255),
    owner_id            BIGINT,
    remark              VARCHAR(500),
    create_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             SMALLINT  NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_opportunity_customer_id ON opportunity (customer_id);
CREATE INDEX IF NOT EXISTS idx_opportunity_owner_id ON opportunity (owner_id);
CREATE INDEX IF NOT EXISTS idx_opportunity_stage ON opportunity (stage);
COMMENT ON TABLE opportunity IS '商机';

-- ------------------------------------------------------------
-- 8. 数据字典表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_dict_item (
    id         BIGSERIAL PRIMARY KEY,
    dict_type  VARCHAR(50) NOT NULL,
    dict_label VARCHAR(50) NOT NULL,
    dict_value VARCHAR(50) NOT NULL,
    sort       INT NOT NULL DEFAULT 0,
    status     SMALLINT NOT NULL DEFAULT 1,
    CONSTRAINT uk_dict_type_value UNIQUE (dict_type, dict_value)
);
CREATE INDEX IF NOT EXISTS idx_dict_type ON sys_dict_item (dict_type);
COMMENT ON TABLE sys_dict_item IS '数据字典';

-- ------------------------------------------------------------
-- 9. 操作日志表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS operation_log (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT,
    module      VARCHAR(50),
    action      VARCHAR(50),
    target_id   BIGINT,
    content     VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_operation_log_user_id ON operation_log (user_id);
COMMENT ON TABLE operation_log IS '操作日志';

-- ============================================================
-- 种子数据
-- ============================================================

-- 角色
INSERT INTO sys_role (role_code, role_name, remark) VALUES
    ('ADMIN',   '管理员',   '系统最高权限'),
    ('MANAGER', '销售主管', '查看全部数据、分配客户'),
    ('SALES',   '销售',     '管理本人名下客户与商机'),
    ('GUEST',   '访客',     '只读账号')
ON CONFLICT (role_code) DO NOTHING;

-- 管理员账号（密码 admin123，BCrypt；启动时若不存在会由后端兜底创建）
INSERT INTO sys_user (username, password, real_name) VALUES
    ('admin', '$2b$10$r1dpJCTTWa6T4FqLWJcC3OqoWROl/IZlmLoP9wZO43b1EO1nKnqp2', '管理员')
ON CONFLICT (username) DO NOTHING;

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u, sys_role r
WHERE u.username = 'admin' AND r.role_code = 'ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- 数据字典
INSERT INTO sys_dict_item (dict_type, dict_label, dict_value, sort) VALUES
    ('industry',    '互联网',     'internet',     1),
    ('industry',    '制造业',     'manufacturing',2),
    ('industry',    '金融',       'finance',      3),
    ('industry',    '医疗健康',   'healthcare',   4),
    ('industry',    '教育',       'education',    5),
    ('industry',    '其他',       'other',        99),
    ('source',      '官网',       'website',      1),
    ('source',      '转介绍',     'referral',     2),
    ('source',      '展会',       'exhibition',   3),
    ('source',      '广告投放',   'ads',          4),
    ('source',      '其他',       'other',        99),
    ('level',       'A 级',       'A',            1),
    ('level',       'B 级',       'B',            2),
    ('level',       'C 级',       'C',            3),
    ('customer_status', '潜在客户', 'potential',  1),
    ('customer_status', '已合作',   'cooperated', 2),
    ('customer_status', '流失',     'lost',       3),
    ('customer_status', '暂停合作', 'paused',     4),
    ('follow_type', '电话',       'phone',        1),
    ('follow_type', '微信',       'wechat',       2),
    ('follow_type', '拜访',       'visit',        3),
    ('follow_type', '邮件',       'email',        4),
    ('opp_stage',   '初步接触',   'contact',      1),
    ('opp_stage',   '需求确认',   'requirement',  2),
    ('opp_stage',   '方案报价',   'proposal',     3),
    ('opp_stage',   '商务谈判',   'negotiation',  4),
    ('opp_stage',   '赢单',       'win',          5),
    ('opp_stage',   '输单',       'lose',         6)
ON CONFLICT (dict_type, dict_value) DO NOTHING;
