/**
 * 云客数智 获客官网后端
 * 客户在线留资 -> 自动写入 CRM 数据库（customer / contact / operation_log）
 */
const path = require('path');
const fs = require('fs');
const express = require('express');
const { Pool } = require('pg');

const PORT = process.env.PORT || 3000;
const DB_CONFIG_FILE = process.env.CRM_DB_CONFIG_FILE
  || path.resolve(__dirname, '..', 'docs', 'db.json');

// ---------- 数据库连接（复用 CRM 的 db.json） ----------
function loadDbConfig() {
  if (!fs.existsSync(DB_CONFIG_FILE)) {
    throw new Error(`数据库配置文件不存在: ${DB_CONFIG_FILE}`);
  }
  const raw = fs.readFileSync(DB_CONFIG_FILE, 'utf8').replace(/^\uFEFF/, '');
  const cfg = JSON.parse(raw);
  return {
    host: cfg.host,
    port: cfg.port || 5432,
    database: cfg.database,
    user: cfg.user,
    password: cfg.password,
    ssl: cfg.ssl ? { rejectUnauthorized: false } : false,
    max: 5,
    connectionTimeoutMillis: 10000,
  };
}

const pool = new Pool(loadDbConfig());

// ---------- 简单的 IP 限流（每小时每 IP 最多 10 次） ----------
const HOUR = 60 * 60 * 1000;
const submits = new Map();
function rateLimited(ip) {
  const now = Date.now();
  const rec = submits.get(ip);
  if (!rec) {
    submits.set(ip, { count: 1, windowStart: now });
    return false;
  }
  if (now - rec.windowStart > HOUR) {
    submits.set(ip, { count: 1, windowStart: now });
    return false;
  }
  rec.count += 1;
  return rec.count > 10;
}

const app = express();
app.use(express.json({ limit: '50kb' }));
app.use(express.static(path.join(__dirname, 'public')));

// 健康检查
app.get('/api/health', async (req, res) => {
  try {
    const { rows } = await pool.query('SELECT 1 AS ok');
    res.json({ ok: rows[0].ok === 1, time: new Date().toISOString() });
  } catch (e) {
    res.status(503).json({ ok: false, error: e.message });
  }
});

// 线索提交
app.post('/api/leads', async (req, res) => {
  const ip = req.ip || req.socket.remoteAddress || 'unknown';

  // 蜜罐字段：机器人会填写，正常用户不会
  if (req.body.company_website_hp) {
    return res.status(200).json({ code: 200, message: '已收到' });
  }
  if (rateLimited(ip)) {
    return res.status(429).json({ code: 429, message: '提交过于频繁，请稍后再试' });
  }

  const b = req.body || {};
  const name = String(b.name || '').trim();          // 公司名称 -> customer.name
  const contactName = String(b.contact_name || '').trim(); // 联系人 -> contact.name
  const phone = String(b.phone || '').trim();        // 手机号
  const email = String(b.email || '').trim();
  const wechat = String(b.wechat || '').trim();
  const position = String(b.position || '').trim();
  const industry = String(b.industry || '').trim();
  const website = String(b.website || '').trim();
  const remark = String(b.remark || '').trim();

  // 基础校验
  if (!name || !contactName || !phone) {
    return res.status(400).json({ code: 400, message: '请填写公司名称、联系人和手机号' });
  }
  if (!/^1[3-9]\d{9}$/.test(phone) && !/^[+\d][\d\s-]{5,19}$/.test(phone)) {
    return res.status(400).json({ code: 400, message: '手机号格式不正确' });
  }
  if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    return res.status(400).json({ code: 400, message: '邮箱格式不正确' });
  }

  const client = await pool.connect();
  try {
    await client.query('BEGIN');

    // 按手机号查重：已存在的客户不再重复创建，只补充联系人
    const dup = await client.query(
      `SELECT id, name FROM customer WHERE phone = $1 AND deleted = 0 LIMIT 1`,
      [phone]
    );

    let customerId;
    if (dup.rows.length > 0) {
      customerId = dup.rows[0].id;
      const c = dup.rows[0];
      await client.query(
        `UPDATE customer SET remark = CASE
           WHEN remark IS NULL OR remark = '' THEN $1
           ELSE remark || '；' || $1 END, update_time = now() WHERE id = $2`,
        [remark ? `官网再次留资：${remark}` : '官网再次留资', customerId]
      );
    } else {
      const ins = await client.query(
        `INSERT INTO customer
           (name, industry, source, level, status, phone, email, website, owner_id, remark)
         VALUES ($1, $2, 'website', NULL, 'potential', $3, $4, $5, NULL, $6)
         RETURNING id`,
        [name, industry || null, phone, email || null, website || null, remark || null]
      );
      customerId = ins.rows[0].id;
    }

    await client.query(
      `INSERT INTO contact
         (customer_id, name, phone, email, position, wechat, is_primary, remark)
       VALUES ($1, $2, $3, $4, $5, $6, 1, $7)`,
      [customerId, contactName, phone, email || null, position || null, wechat || null, remark || null]
    );

    await client.query(
      `INSERT INTO operation_log (user_id, module, action, target_id, content)
       VALUES (NULL, 'customer', 'lead_from_website', $1, $2)`,
      [customerId, `官网获客：${name} - ${contactName} ${phone}`]
    );

    await client.query('COMMIT');
    res.json({ code: 200, message: '提交成功，我们将在 1 个工作日内与您联系', customerId });
  } catch (e) {
    await client.query('ROLLBACK');
    console.error('[lead] insert error:', e);
    res.status(500).json({ code: 500, message: '服务繁忙，请稍后重试或直接致电我们' });
  } finally {
    client.release();
  }
});

app.listen(PORT, () => {
  console.log(`云客数智获客官网已启动: http://localhost:${PORT}`);
  console.log(`数据库配置: ${DB_CONFIG_FILE}`);
});
