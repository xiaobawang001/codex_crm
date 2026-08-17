(function () {
  'use strict';

  var form = document.getElementById('leadForm');
  var msg = document.getElementById('formMsg');
  var btn = document.getElementById('submitBtn');

  function showMsg(type, text) {
    msg.className = 'form-msg ' + type;
    msg.textContent = text;
    msg.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  }

  function clearMsg() {
    msg.className = 'form-msg';
    msg.textContent = '';
  }

  function markErr(el) {
    el.classList.add('err');
  }

  function clearErr() {
    form.querySelectorAll('.err').forEach(function (el) { el.classList.remove('err'); });
  }

  form.addEventListener('submit', async function (e) {
    e.preventDefault();
    clearErr();
    clearMsg();

    var name = form.name.value.trim();
    var contactName = form.contact_name.value.trim();
    var phone = form.phone.value.trim();
    var email = form.email.value.trim();
    var remark = form.remark.value.trim();

    var invalid = false;
    if (!name) { markErr(form.name); invalid = true; }
    if (!contactName) { markErr(form.contact_name); invalid = true; }
    if (!phone) { markErr(form.phone); invalid = true; }
    else if (!/^1[3-9]\d{9}$/.test(phone) && !/^[+\d][\d\s-]{5,19}$/.test(phone)) { markErr(form.phone); invalid = true; }
    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) { markErr(form.email); invalid = true; }
    if (!remark) { markErr(form.remark); invalid = true; }

    if (invalid) {
      showMsg('err', '请检查填写内容：标 * 为必填项，手机号 / 邮箱格式需正确。');
      return;
    }

    btn.disabled = true;
    btn.textContent = '提交中…';

    try {
      var res = await fetch('/api/leads', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: name,
          contact_name: contactName,
          phone: phone,
          email: email,
          wechat: form.wechat.value.trim(),
          position: form.position.value.trim(),
          industry: form.industry.value.trim(),
          website: form.website.value.trim(),
          remark: remark,
          company_website_hp: form.company_website_hp.value
        })
      });
      var data = await res.json();
      if (res.ok && data.code === 200) {
        showMsg('ok', '✅ 提交成功！我们将在 1 个工作日内与您联系。');
        form.reset();
      } else {
        showMsg('err', data.message || '提交失败，请稍后重试。');
      }
    } catch (err) {
      showMsg('err', '网络异常，请稍后重试。');
    } finally {
      btn.disabled = false;
      btn.textContent = '提交需求，获取方案';
    }
  });
})();
