# Denvork Mobile Web Portal (Vercel Deployment Guide)

This is the responsive, high-performance Mobile Web Portal for **Denvork** (`denvork.com/portal.php?ref_id=Njk4MDc4`) designed for deployment on **Vercel** and full responsiveness across all mobile, tablet, and desktop devices.

---

## 🚀 How to Deploy on Vercel (1-Minute Setup)

### Option 1: Deploy with GitHub (Recommended)
1. Push this repository to your GitHub account.
2. Go to **[vercel.com](https://vercel.com)** and click **"Add New Project"**.
3. Import your GitHub repository.
4. Leave all build settings as default (Framework Preset: **Other** / Static).
5. Click **"Deploy"**.
6. Your live website is instantly ready on your custom `*.vercel.app` domain!

### Option 2: Deploy with Vercel CLI
Run the following in the project root:
```bash
npm install -g vercel
vercel
```
Follow the interactive prompts and select defaults.

---

## 🌟 Key Features Included

- **Exact Denvork Fintech Design**:
  - Dark mode aesthetic (`#0B0F19`) with Emerald Green (`#10B981`) and Cyan accents.
  - Native mobile app feel with bottom navigation bar and touch-friendly controls.
  - Responsive desktop/tablet layout with collapsible sidebar and wide grids.
- **Affiliate & Referral Network (`ref_id=Njk4MDc4`)**:
  - Automatically captures `?ref_id=...` parameter from URL queries.
  - Generates 1-click shareable referral link: `https://<your-vercel-domain>/portal.php?ref_id=Njk4MDc4`.
  - 1-click sharing to WhatsApp, Telegram, Facebook, and Twitter.
  - 3-Tier commission breakdown (15% Level 1, 5% Level 2, 2% Level 3).
- **Interactive Work & Task Center (`portal.php`)**:
  - Real-time countdown timer with anti-fraud progress bar.
  - Instant reward credit into user's wallet.
  - Daily check-in streak bonus (7-day streak).
- **Pakistani & International Payment Gateways**:
  - **JazzCash** Mobile Account
  - **Easypaisa** Mobile Account
  - **Sadapay / Nayapay**
  - **Bank Transfer** (IBAN)
  - **Crypto USDT (TRC-20)**
  - 4-digit security PIN protection on withdrawals.
- **Multi-Currency Converter**:
  - Switch between **PKR (Rs)** and **USD ($)** in real-time.
- **KYC & Identity Management**:
  - CNIC verification workflow with document status.
- **Zero-Dependency Persistence**:
  - Instant client-side persistence via `localStorage` with pre-configured demo account and transaction history.
