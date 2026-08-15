# 云客 CRM 系统

基于 Spring Boot 3 + Vue 3 + PostgreSQL 的轻量客户关系管理系统。

## 技术栈

- 后端：Spring Boot 3.5（Java 17+）、MyBatis-Plus、JWT、BCrypt
- 前端：Vue 3 + TypeScript + Vite、Element Plus、Pinia、ECharts
- 数据库：PostgreSQL 16+（连接信息从 `docs/db.json` 读取，支持 SSL）

## 目录结构

```
codex_crm/
├── docs/                 # 设计文档、db.json、SQL 脚本
│   ├── CRM系统设计文档.md
│   ├── db.json           # 数据库连接信息（含密码，勿提交到公开仓库）
│   └── sql/init.sql      # 建表与种子数据
├── crm-server/           # Spring Boot 后端（DDD 分层）
│   └── src/main/java/com/example/crm/
│       ├── shared/       # 共享内核：统一响应、异常、审计
│       ├── config/       # 数据源、MyBatis-Plus、CORS 等全局配置
│       ├── security/     # JWT 与鉴权拦截器
│       └── modules/      # 领域模块：user / customer / followup / opportunity / dashboard / dict
│           └── <模块>/
│               ├── interfaces/    # 接口层：Controller + DTO
│               ├── application/   # 应用层：用例编排、事务、权限
│               ├── domain/        # 领域层：实体、值对象、仓储接口、领域服务
│               └── infrastructure/# 基础设施层：MyBatis-Plus 实现
└── crm-web/              # Vue 3 前端
    └── src/
        ├── api/          # Axios 封装与接口模块
        ├── stores/       # Pinia（用户、字典）
        ├── layouts/      # 主布局
        └── views/        # 页面
```

## 快速开始

### 1. 初始化数据库

确保 `docs/db.json` 中连接信息正确，然后执行：

```bash
# 方式一：psql
psql "postgresql://<user>:<password>@<host>:<port>/<database>?sslmode=require" -f docs/sql/init.sql

# 方式二：Docker 中的 psql
docker run --rm -v "$PWD/docs/sql:/sql" postgres:17 \
  psql "postgresql://<user>:<password>@<host>:<port>/<database>?sslmode=require" -f /sql/init.sql
```

### 2. 启动后端

```bash
cd crm-server
mvn spring-boot:run
# 默认端口 8080，自动读取 ../docs/db.json
```

### 3. 启动前端

```bash
cd crm-web
npm install
npm run dev
# 访问 http://localhost:5173，开发代理 /api -> http://localhost:8080
```

### 4. 生产构建

```bash
cd crm-server && mvn package -DskipTests
cd crm-web && npm run build   # 产物在 crm-web/dist
```

## 默认账号

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| admin | admin123 | 管理员（登录后请修改密码） |

## Mock 数据（可选）

执行 `docs/sql/mock_data.sql` 可写入演示数据（18 客户 / 36 联系人 / 26 跟进 / 14 商机，客户与商机创建时间跨 10 个月，便于查看看板趋势）：

| 账号 | 密码 | 角色 | 说明 |
| --- | --- | --- | --- |
| manager1 | 123456 | 销售主管 | 可见全部数据 |
| sales1 | 123456 | 销售 | 本人 4 个客户 + 公海 4 个 |
| sales2 / sales3 | 123456 | 销售 | 同上 |
| guest1 | 123456 | 访客 | 只读账号 |

脚本可重复执行，会清空业务表（保留 admin）后重新写入。

## 环境变量

| 变量 | 说明 | 默认值 |
| --- | --- | --- |
| `CRM_JWT_SECRET` | JWT 签名密钥（生产必改） | 内置开发密钥 |
| `CRM_DB_CONFIG_FILE` | db.json 路径 | `../docs/db.json` |

## 说明

- 数据权限模型：管理员/主管可见全部；销售仅见本人客户与公海客户
- 数据库连接信息（`docs/db.json`）含明文密码，已加入 `.gitignore`，请勿提交到公开仓库

## UI 技能

项目内已安装 `ui-ux-pro-max` 设计技能（`.codex/skills/ui-ux-pro-max/`），来源：`nextlevelbuilder/ui-ux-pro-max-skill`（MIT）。

调用方式（下次对话自动生效）：

```bash
python .codex/skills/ui-ux-pro-max/scripts/search.py "b2b crm dashboard" --design-system -p "CRM"
python .codex/skills/ui-ux-pro-max/scripts/search.py "form feedback validation" --domain ux
python .codex/skills/ui-ux-pro-max/scripts/search.py "data table" --stack vue
```

当前前端采用其生成的企业 CRM 设计系统：Flat Design、主色 `#2563EB`、强调色 `#059669`、字体 Inter + 中文回退，设计令牌集中在 `crm-web/src/styles/design-tokens.css`。
