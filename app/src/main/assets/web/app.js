/**
 * Denvork Mobile Web Portal - Complete Client-side Application
 */
const APP_STATE = {
  currentCurrency: 'PKR',
  usdToPkrRate: 280.0,
  activeTab: 'dashboard',
  user: null,
  activeTaskTimer: null,
  taskTimeRemaining: 0,
  currentRunningTask: null
};

const DEFAULT_TASKS = [
  {
    id: 'task_yt_1',
    title: 'Watch Crypto & Tech Tutorial',
    platform: 'YouTube',
    category: 'video',
    icon: '▶️',
    iconBg: '#fee2e2',
    iconColor: '#ef4444',
    rewardPKR: 45.0,
    rewardUSD: 0.16,
    durationSec: 25,
    completed: false,
    dailyLimit: '1/1 Available',
    url: 'https://www.youtube.com'
  },
  {
    id: 'task_tt_1',
    title: 'Engage with Sponsor Video Reel',
    platform: 'TikTok',
    category: 'social',
    icon: '🎵',
    iconBg: '#f3e8ff',
    iconColor: '#a855f7',
    rewardPKR: 35.0,
    rewardUSD: 0.125,
    durationSec: 20,
    completed: false,
    dailyLimit: '1/1 Available',
    url: 'https://www.tiktok.com'
  },
  {
    id: 'task_yt_2',
    title: 'Watch E-Commerce Growth Guide',
    platform: 'YouTube',
    category: 'video',
    icon: '▶️',
    iconBg: '#fee2e2',
    iconColor: '#ef4444',
    rewardPKR: 60.0,
    rewardUSD: 0.21,
    durationSec: 30,
    completed: false,
    dailyLimit: '1/1 Available',
    url: 'https://www.youtube.com'
  },
  {
    id: 'task_web_1',
    title: 'Visit & Explore Sponsor Blog',
    platform: 'Web Surfing',
    category: 'web',
    icon: '🌐',
    iconBg: '#cffafe',
    iconColor: '#06b6d4',
    rewardPKR: 30.0,
    rewardUSD: 0.11,
    durationSec: 15,
    completed: false,
    dailyLimit: '1/1 Available',
    url: 'https://denvork.com'
  },
  {
    id: 'task_app_1',
    title: 'Review E-Wallet Mobile App',
    platform: 'Play Store',
    category: 'app',
    icon: '⭐',
    iconBg: '#fef3c7',
    iconColor: '#f59e0b',
    rewardPKR: 120.0,
    rewardUSD: 0.43,
    durationSec: 40,
    completed: false,
    dailyLimit: '1/1 Available',
    url: 'https://play.google.com'
  }
];

function initDatabase() {
  const urlParams = new URLSearchParams(window.location.search);
  const refFromUrl = urlParams.get('ref_id') || 'Njk4MDc4';

  if (!localStorage.getItem('denvork_users')) {
    const demoUser = {
      id: 'usr_1001',
      name: 'Rabnawaz Official',
      email: 'rabnawaztraders302@gmail.com',
      phone: '+92 300 1234567',
      password: 'password123',
      pin: '1234',
      refId: 'Njk4MDc4',
      sponsorId: 'DENVORK_HQ',
      tier: 'Gold VIP Earner',
      kycStatus: 'verified',
      kycDocument: '35201-1234567-1',
      balancePKR: 3850.0,
      totalEarnedPKR: 12450.0,
      todayEarnedPKR: 175.0,
      totalWithdrawnPKR: 8600.0,
      tasksCompleted: 42,
      streakDays: 4,
      lastStreakClaim: new Date().toDateString(),
      joinDate: '12 Sep 2026'
    };
    localStorage.setItem('denvork_users', JSON.stringify([demoUser]));
  }

  if (!localStorage.getItem('denvork_tasks')) {
    localStorage.setItem('denvork_tasks', JSON.stringify(DEFAULT_TASKS));
  }

  if (!localStorage.getItem('denvork_transactions')) {
    const defaultTx = [
      { id: 'TXN-9841', gateway: 'JazzCash', account: '03001234567', amountPKR: 2500, status: 'Completed', date: '24 Sep 2026, 04:30 PM' },
      { id: 'TXN-8723', gateway: 'Easypaisa', account: '03459876543', amountPKR: 1800, status: 'Completed', date: '21 Sep 2026, 11:15 AM' },
      { id: 'TXN-6512', gateway: 'Bank Transfer', account: 'PK36MEZN000123456789', amountPKR: 4300, status: 'Completed', date: '17 Sep 2026, 02:45 PM' }
    ];
    localStorage.setItem('denvork_transactions', JSON.stringify(defaultTx));
  }

  if (!localStorage.getItem('denvork_referrals')) {
    const defaultRefs = [
      { name: 'Muhammad Usman', date: '23 Sep 2026', tier: 'Tier 1 (15%)', commission: 'Rs 420.00', status: 'Active' },
      { name: 'Ali Raza Khan', date: '21 Sep 2026', tier: 'Tier 1 (15%)', commission: 'Rs 315.00', status: 'Active' },
      { name: 'Tariq Mehmood', date: '19 Sep 2026', tier: 'Tier 2 (5%)', commission: 'Rs 140.00', status: 'Active' },
      { name: 'Shahid Iqbal', date: '16 Sep 2026', tier: 'Tier 1 (15%)', commission: 'Rs 560.00', status: 'Active' }
    ];
    localStorage.setItem('denvork_referrals', JSON.stringify(defaultRefs));
  }

  const savedSession = localStorage.getItem('denvork_session');
  if (savedSession) {
    APP_STATE.user = JSON.parse(savedSession);
  } else {
    const users = JSON.parse(localStorage.getItem('denvork_users'));
    APP_STATE.user = users[0];
    localStorage.setItem('denvork_session', JSON.stringify(APP_STATE.user));
  }
}

function formatAmount(pkrAmount) {
  if (APP_STATE.currentCurrency === 'USD') {
    const usd = pkrAmount / APP_STATE.usdToPkrRate;
    return `$${usd.toFixed(2)}`;
  }
  return `Rs ${pkrAmount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function showToast(message, icon = '✅') {
  const container = document.getElementById('toastContainer');
  if (!container) return;
  const toast = document.createElement('div');
  toast.className = 'toast';
  toast.innerHTML = `<span>${icon}</span><span>${message}</span>`;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(-10px)';
    setTimeout(() => toast.remove(), 300);
  }, 3200);
}

function setCurrency(curr) {
  APP_STATE.currentCurrency = curr;
  document.querySelectorAll('.currency-opt').forEach(el => {
    el.classList.toggle('active', el.dataset.curr === curr);
  });
  renderCurrentView();
}

function switchTab(tabName) {
  APP_STATE.activeTab = tabName;
  document.querySelectorAll('.nav-item').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.tab === tabName);
  });
  renderCurrentView();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function renderCurrentView() {
  const appBody = document.getElementById('viewContent');
  if (!appBody || !APP_STATE.user) return;
  const u = APP_STATE.user;
  const headAvatar = document.getElementById('headerUserAvatar');
  if (headAvatar) headAvatar.textContent = u.name.charAt(0);

  switch (APP_STATE.activeTab) {
    case 'dashboard': renderDashboard(appBody, u); break;
    case 'work': renderWork(appBody, u); break;
    case 'referrals': renderReferrals(appBody, u); break;
    case 'wallet': renderWallet(appBody, u); break;
    case 'profile': renderProfile(appBody, u); break;
    default: renderDashboard(appBody, u);
  }
}

function renderDashboard(container, u) {
  const isKycVerified = u.kycStatus === 'verified';
  container.innerHTML = `
    <div class="user-banner">
      <div class="user-info-group">
        <div class="user-avatar-lg">${u.name.charAt(0)}</div>
        <div class="user-title-box">
          <h2>Assalam-o-Alaikum, ${u.name}</h2>
          <div class="user-badges-row">
            <span class="badge-tag badge-vip">👑 ${u.tier}</span>
            <span class="badge-tag ${isKycVerified ? 'badge-kyc-verified' : 'badge-kyc-pending'}">
              ${isKycVerified ? '✓ Verified KYC' : '⚠ KYC Pending'}
            </span>
          </div>
        </div>
      </div>
      <button class="btn btn-secondary btn-sm" onclick="openModal('announcementModal')">
        📢 Notices
      </button>
    </div>

    <div class="balance-hero">
      <div class="balance-header">
        <span>Available Wallet Balance</span>
        <span>ID: ${u.refId}</span>
      </div>
      <div class="balance-amount">
        <span>${formatAmount(u.balancePKR)}</span>
      </div>
      <div class="balance-actions">
        <button class="btn btn-primary" onclick="switchTab('wallet')">⚡ Withdraw Cash</button>
        <button class="btn btn-secondary" onclick="switchTab('work')">🎯 Start Work</button>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(16, 185, 129, 0.15); color: #10b981;">💰</div>
        <div class="stat-val">${formatAmount(u.todayEarnedPKR)}</div>
        <div class="stat-lbl">Today's Earnings</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(6, 182, 212, 0.15); color: #06b6d4;">✅</div>
        <div class="stat-val">${u.tasksCompleted}</div>
        <div class="stat-lbl">Completed Tasks</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(245, 158, 11, 0.15); color: #f59e0b;">👥</div>
        <div class="stat-val">4 Active</div>
        <div class="stat-lbl">Affiliate Team</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(168, 85, 247, 0.15); color: #a855f7;">💳</div>
        <div class="stat-val">${formatAmount(u.totalWithdrawnPKR)}</div>
        <div class="stat-lbl">Total Withdrawn</div>
      </div>
    </div>

    <div class="streak-box">
      <div class="streak-header">
        <div>
          <strong style="color: #f59e0b;">🔥 7-Day Earning Streak</strong>
          <p style="font-size: 0.75rem; color: var(--text-secondary);">Claim daily login bonus rewards</p>
        </div>
        <button class="btn btn-primary btn-sm" onclick="claimDailyStreak()">
          Claim Bonus (+Rs 25)
        </button>
      </div>
      <div class="streak-days">
        ${[1, 2, 3, 4, 5, 6, 7].map(day => `
          <div class="day-pill ${day < u.streakDays ? 'completed' : day === u.streakDays ? 'active' : ''}">
            <span>Day ${day}</span>
            <span>${day <= u.streakDays ? '✓' : '🔒'}</span>
          </div>
        `).join('')}
      </div>
    </div>

    <h3 style="font-size: 0.95rem; margin-bottom: 10px; color: var(--text-secondary);">Quick Actions</h3>
    <div class="quick-shortcuts">
      <div class="shortcut-item" onclick="switchTab('work')">
        <div class="shortcut-icon-circle" style="background: rgba(239, 68, 68, 0.15); color: #ef4444;">🎬</div>
        <span class="shortcut-title">Watch Ads</span>
      </div>
      <div class="shortcut-item" onclick="switchTab('wallet')">
        <div class="shortcut-icon-circle" style="background: rgba(16, 185, 129, 0.15); color: #10b981;">⚡</div>
        <span class="shortcut-title">JazzCash</span>
      </div>
      <div class="shortcut-item" onclick="switchTab('referrals')">
        <div class="shortcut-icon-circle" style="background: rgba(6, 182, 212, 0.15); color: #06b6d4;">🤝</div>
        <span class="shortcut-title">Invite & Earn</span>
      </div>
      <div class="shortcut-item" onclick="switchTab('profile')">
        <div class="shortcut-icon-circle" style="background: rgba(245, 158, 11, 0.15); color: #f59e0b;">🛡️</div>
        <span class="shortcut-title">Verify KYC</span>
      </div>
    </div>
  `;
}

function renderWork(container, u) {
  const tasks = JSON.parse(localStorage.getItem('denvork_tasks')) || DEFAULT_TASKS;
  container.innerHTML = `
    <div class="card card-glow" style="margin-bottom: 16px;">
      <h2 style="font-size: 1.15rem; margin-bottom: 4px;">Denvork Work Center</h2>
      <p style="font-size: 0.8rem; color: var(--text-secondary);">
        Complete high-paying video and sponsor tasks. Balance is credited instantly!
      </p>
    </div>
    <div class="filter-chips">
      <button class="chip active" onclick="filterTasks('all', this)">All Tasks</button>
      <button class="chip" onclick="filterTasks('video', this)">YouTube Watch</button>
      <button class="chip" onclick="filterTasks('social', this)">TikTok & Social</button>
      <button class="chip" onclick="filterTasks('web', this)">Web Visits</button>
      <button class="chip" onclick="filterTasks('app', this)">App Testing</button>
    </div>
    <div id="tasksListContainer">
      ${tasks.map(t => `
        <div class="task-item" data-category="${t.category}">
          <div class="task-left">
            <div class="task-platform-icon" style="background: ${t.iconBg}; color: ${t.iconColor};">${t.icon}</div>
            <div>
              <div class="task-title">${t.title}</div>
              <div class="task-meta">
                <span>⏱️ ${t.durationSec}s</span> • <span>${t.platform}</span> • <span style="color: var(--emerald-light);">${t.dailyLimit}</span>
              </div>
            </div>
          </div>
          <div class="task-right">
            <span class="task-reward">+${formatAmount(t.rewardPKR)}</span>
            <button class="btn btn-primary btn-sm" onclick="startTask('${t.id}')">Start</button>
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

function filterTasks(cat, element) {
  document.querySelectorAll('.filter-chips .chip').forEach(c => c.classList.remove('active'));
  element.classList.add('active');
  const items = document.querySelectorAll('#tasksListContainer .task-item');
  items.forEach(item => {
    item.style.display = (cat === 'all' || item.dataset.category === cat) ? 'flex' : 'none';
  });
}

function renderReferrals(container, u) {
  const refList = JSON.parse(localStorage.getItem('denvork_referrals')) || [];
  const fullRefUrl = `${window.location.origin}/portal.php?ref_id=${u.refId}`;
  container.innerHTML = `
    <div class="ref-box">
      <span style="font-size: 0.75rem; text-transform: uppercase; color: var(--cyan-accent); font-weight: 700;">Affiliate Network</span>
      <h2 style="font-size: 1.2rem; margin: 4px 0 8px 0;">Earn 15% Lifetime Commissions</h2>
      <p style="font-size: 0.8rem; color: var(--text-secondary); margin-bottom: 12px;">Share your unique referral link to earn recurring commissions!</p>
      <div class="ref-link-input-group">
        <input type="text" class="ref-link-input" id="referralUrlInput" value="${fullRefUrl}" readonly />
        <button class="btn btn-primary" onclick="copyReferralLink()">📋 Copy</button>
      </div>
      <div class="share-buttons-row">
        <a class="share-btn share-wa" href="https://api.whatsapp.com/send?text=Join%20Denvork%20Portal:%20${encodeURIComponent(fullRefUrl)}" target="_blank">WhatsApp</a>
        <a class="share-btn share-tg" href="https://t.me/share/url?url=${encodeURIComponent(fullRefUrl)}&text=Join%20Denvork" target="_blank">Telegram</a>
        <a class="share-btn share-fb" href="https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(fullRefUrl)}" target="_blank">Facebook</a>
        <a class="share-btn share-x" href="https://twitter.com/intent/tweet?url=${encodeURIComponent(fullRefUrl)}" target="_blank">X</a>
      </div>
    </div>
    <div class="tiers-grid">
      <div class="tier-card"><div class="tier-pct">15%</div><div class="tier-name">Level 1</div></div>
      <div class="tier-card"><div class="tier-pct">5%</div><div class="tier-name">Level 2</div></div>
      <div class="tier-card"><div class="tier-pct">2%</div><div class="tier-name">Level 3</div></div>
    </div>
    <div class="card">
      <h3 style="font-size: 0.95rem; margin-bottom: 12px;">Active Referred Members (${refList.length})</h3>
      ${refList.map(r => `
        <div style="display: flex; justify-content: space-between; border-bottom: 1px solid var(--border-color); padding-bottom: 8px; margin-bottom: 8px;">
          <div><strong>${r.name}</strong><div style="font-size: 0.72rem; color: var(--text-muted);">${r.date} • ${r.tier}</div></div>
          <div style="text-align: right;"><span style="color: var(--emerald-light); font-weight: 700;">${r.commission}</span></div>
        </div>
      `).join('')}
    </div>
  `;
}

function copyReferralLink() {
  const input = document.getElementById('referralUrlInput');
  if (!input) return;
  input.select();
  navigator.clipboard.writeText(input.value).then(() => {
    showToast('Referral link copied!', '🔗');
  });
}

function renderWallet(container, u) {
  const txList = JSON.parse(localStorage.getItem('denvork_transactions')) || [];
  container.innerHTML = `
    <div class="card card-glow">
      <span style="font-size: 0.75rem; color: var(--emerald-light); text-transform: uppercase; font-weight: 700;">Withdrawal Center</span>
      <div style="display: flex; justify-content: space-between; align-items: baseline; margin-top: 6px;">
        <span style="font-size: 1.8rem; font-weight: 900;">${formatAmount(u.balancePKR)}</span>
        <span style="font-size: 0.78rem; color: var(--text-muted);">Min Payout: Rs 500 / $2.00</span>
      </div>
    </div>

    <div class="card">
      <h3 style="font-size: 1rem; margin-bottom: 12px;">Request Instant Payout</h3>
      <div class="form-group">
        <label class="form-label">Select Payment Gateway</label>
        <div class="gateway-selector" id="gwSelector">
          <div class="gateway-chip active" data-gw="JazzCash" onclick="selectGateway('JazzCash', this)"><span>⚡</span><span class="gateway-name">JazzCash</span></div>
          <div class="gateway-chip" data-gw="Easypaisa" onclick="selectGateway('Easypaisa', this)"><span>🟢</span><span class="gateway-name">Easypaisa</span></div>
          <div class="gateway-chip" data-gw="Sadapay" onclick="selectGateway('Sadapay', this)"><span>📱</span><span class="gateway-name">Sadapay</span></div>
          <div class="gateway-chip" data-gw="Bank" onclick="selectGateway('Bank', this)"><span>🏦</span><span class="gateway-name">Bank Transfer</span></div>
          <div class="gateway-chip" data-gw="USDT" onclick="selectGateway('USDT', this)"><span>💎</span><span class="gateway-name">USDT</span></div>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label" id="accNumberLabel">Account Number / Mobile Number</label>
        <input type="text" class="form-control" id="withAccNumber" value="03001234567" />
      </div>
      <div class="form-group">
        <label class="form-label">Account Title / Full Name</label>
        <input type="text" class="form-control" id="withAccTitle" value="${u.name}" />
      </div>
      <div class="form-group">
        <label class="form-label">Withdrawal Amount (${APP_STATE.currentCurrency})</label>
        <input type="number" class="form-control" id="withAmount" value="1000" />
      </div>
      <div class="form-group">
        <label class="form-label">4-Digit Security PIN</label>
        <input type="password" class="form-control" id="withPin" maxlength="4" value="${u.pin}" />
      </div>
      <button class="btn btn-primary btn-block" onclick="submitWithdrawal()">🚀 Submit Payout Request</button>
    </div>

    <div class="card">
      <h3 style="font-size: 0.95rem; margin-bottom: 12px;">Recent Withdrawals</h3>
      ${txList.map(tx => `
        <div style="display: flex; justify-content: space-between; border-bottom: 1px solid var(--border-color); padding-bottom: 8px; margin-bottom: 8px;">
          <div><strong>${tx.gateway} - ${tx.account}</strong><div style="font-size: 0.72rem; color: var(--text-muted);">${tx.date}</div></div>
          <div style="text-align: right;"><span style="color: var(--emerald-light); font-weight: 800;">-Rs ${tx.amountPKR}</span></div>
        </div>
      `).join('')}
    </div>
  `;
}

function selectGateway(gw, element) {
  document.querySelectorAll('.gateway-chip').forEach(c => c.classList.remove('active'));
  element.classList.add('active');
  const label = document.getElementById('accNumberLabel');
  if (gw === 'Bank') label.textContent = 'Bank Name & IBAN';
  else if (gw === 'USDT') label.textContent = 'USDT Wallet Address (TRC-20)';
  else label.textContent = 'Account Number / Mobile Number';
}

function submitWithdrawal() {
  const u = APP_STATE.user;
  const gwEl = document.querySelector('.gateway-chip.active');
  const gw = gwEl ? gwEl.dataset.gw : 'JazzCash';
  const acc = document.getElementById('withAccNumber').value.trim();
  const amt = parseFloat(document.getElementById('withAmount').value.trim());
  const pin = document.getElementById('withPin').value.trim();

  if (!amt || amt < 500) { showToast('Minimum withdrawal is Rs 500', '⚠️'); return; }
  if (amt > u.balancePKR) { showToast('Insufficient balance!', '❌'); return; }
  if (pin !== u.pin) { showToast('Incorrect security PIN!', '❌'); return; }

  u.balancePKR -= amt;
  u.totalWithdrawnPKR += amt;

  const newTx = { id: `TXN-${Math.floor(1000 + Math.random() * 9000)}`, gateway: gw, account: acc, amountPKR: amt, status: 'Processing', date: 'Just now' };
  const txList = JSON.parse(localStorage.getItem('denvork_transactions')) || [];
  txList.unshift(newTx);
  localStorage.setItem('denvork_transactions', JSON.stringify(txList));

  saveUser(u);
  showToast(`Withdrawal of Rs ${amt} submitted!`, '🎉');
  renderCurrentView();
}

function renderProfile(container, u) {
  const isKycVerified = u.kycStatus === 'verified';
  container.innerHTML = `
    <div class="card card-glow" style="text-align: center; padding: 24px;">
      <div class="user-avatar-lg" style="margin: 0 auto 12px auto; width: 64px; height: 64px; font-size: 1.8rem;">${u.name.charAt(0)}</div>
      <h2>${u.name}</h2>
      <p style="font-size: 0.8rem; color: var(--text-secondary);">${u.email}</p>
      <div style="margin: 8px 0;"><span class="badge-tag badge-vip">${u.tier}</span></div>
      <div style="font-size: 0.78rem; color: var(--text-muted);">Sponsor: ${u.sponsorId}</div>
    </div>
    <div class="card">
      <h3 style="font-size: 0.95rem; margin-bottom: 8px;">🛡️ KYC & Identity</h3>
      ${isKycVerified ? `
        <div style="background: rgba(16, 185, 129, 0.1); border: 1px solid var(--emerald-primary); padding: 12px; border-radius: var(--radius-md);">
          <strong style="color: var(--emerald-light);">✓ KYC Approved & Verified</strong>
          <p style="font-size: 0.75rem; color: var(--text-secondary);">CNIC: ${u.kycDocument}</p>
        </div>
      ` : `
        <input type="text" class="form-control" id="kycCnicInput" placeholder="CNIC Number" style="margin-bottom: 8px;" />
        <button class="btn btn-primary btn-block" onclick="submitKYC()">Submit KYC</button>
      `}
    </div>
  `;
}

function submitKYC() {
  const cnic = document.getElementById('kycCnicInput').value.trim();
  if (!cnic) return;
  const u = APP_STATE.user;
  u.kycStatus = 'verified';
  u.kycDocument = cnic;
  saveUser(u);
  showToast('KYC verified successfully!', '🎉');
  renderCurrentView();
}

function claimDailyStreak() {
  const u = APP_STATE.user;
  u.balancePKR += 25.0;
  u.todayEarnedPKR += 25.0;
  if (u.streakDays < 7) u.streakDays += 1;
  saveUser(u);
  showToast('Claimed Daily Streak Bonus +Rs 25.00!', '🔥');
  renderCurrentView();
}

function startTask(taskId) {
  const tasks = JSON.parse(localStorage.getItem('denvork_tasks')) || DEFAULT_TASKS;
  const task = tasks.find(t => t.id === taskId);
  if (!task) return;

  APP_STATE.currentRunningTask = task;
  APP_STATE.taskTimeRemaining = task.durationSec;

  const modal = document.getElementById('taskRunnerModal');
  document.getElementById('taskModalTitle').textContent = task.title;
  document.getElementById('taskTimerDisplay').textContent = `${task.durationSec}s`;
  document.getElementById('taskProgressBar').style.width = '0%';
  const completeBtn = document.getElementById('taskCompleteBtn');
  completeBtn.disabled = true;
  completeBtn.textContent = 'Watching Task...';

  modal.classList.add('active');

  clearInterval(APP_STATE.activeTaskTimer);
  const total = task.durationSec;

  APP_STATE.activeTaskTimer = setInterval(() => {
    APP_STATE.taskTimeRemaining -= 1;
    const elapsed = total - APP_STATE.taskTimeRemaining;
    const pct = Math.min(100, Math.round((elapsed / total) * 100));
    document.getElementById('taskTimerDisplay').textContent = `${Math.max(0, APP_STATE.taskTimeRemaining)}s`;
    document.getElementById('taskProgressBar').style.width = `${pct}%`;

    if (APP_STATE.taskTimeRemaining <= 0) {
      clearInterval(APP_STATE.activeTaskTimer);
      completeBtn.disabled = false;
      completeBtn.textContent = `Claim +${formatAmount(task.rewardPKR)}`;
      completeBtn.classList.add('btn-primary');
    }
  }, 1000);
}

function claimTaskReward() {
  const task = APP_STATE.currentRunningTask;
  if (!task) return;
  const u = APP_STATE.user;
  u.balancePKR += task.rewardPKR;
  u.todayEarnedPKR += task.rewardPKR;
  u.totalEarnedPKR += task.rewardPKR;
  u.tasksCompleted += 1;
  saveUser(u);
  closeModal('taskRunnerModal');
  showToast(`Congratulations! +${formatAmount(task.rewardPKR)} earned!`, '💰');
  renderCurrentView();
}

function fastForwardTask() {
  if (APP_STATE.taskTimeRemaining > 1) APP_STATE.taskTimeRemaining = 1;
}

function openModal(id) {
  const m = document.getElementById(id);
  if (m) m.classList.add('active');
}

function closeModal(id) {
  const m = document.getElementById(id);
  if (m) m.classList.remove('active');
  if (id === 'taskRunnerModal') clearInterval(APP_STATE.activeTaskTimer);
}

function saveUser(u) {
  APP_STATE.user = u;
  localStorage.setItem('denvork_session', JSON.stringify(u));
  const users = JSON.parse(localStorage.getItem('denvork_users')) || [];
  const idx = users.findIndex(x => x.id === u.id);
  if (idx !== -1) {
    users[idx] = u;
    localStorage.setItem('denvork_users', JSON.stringify(users));
  }
}

window.addEventListener('DOMContentLoaded', () => {
  initDatabase();
  renderCurrentView();
});
