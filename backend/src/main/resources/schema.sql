-- Idempotent upgrades for existing H2 databases.
-- On a fresh install the users table may not exist yet; spring.sql.init.continue-on-error=true skips these safely
-- before Hibernate creates the schema.

ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS bio VARCHAR(2000);
ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(255) DEFAULT 'Institutional Investor';
ALTER TABLE users ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS security_progress INT DEFAULT 75;
ALTER TABLE users ADD COLUMN IF NOT EXISTS two_factor_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS notify_portfolio BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS notify_withdrawals BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS notify_compliance BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS notify_reports BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS notify_marketing BOOLEAN DEFAULT FALSE;

UPDATE users SET role = 'Institutional Investor' WHERE role IS NULL;
UPDATE users SET verified = TRUE WHERE verified IS NULL;
UPDATE users SET security_progress = 75 WHERE security_progress IS NULL;
UPDATE users SET two_factor_enabled = FALSE WHERE two_factor_enabled IS NULL;
UPDATE users SET notify_portfolio = TRUE WHERE notify_portfolio IS NULL;
UPDATE users SET notify_withdrawals = TRUE WHERE notify_withdrawals IS NULL;
UPDATE users SET notify_compliance = TRUE WHERE notify_compliance IS NULL;
UPDATE users SET notify_reports = TRUE WHERE notify_reports IS NULL;
UPDATE users SET notify_marketing = FALSE WHERE notify_marketing IS NULL;
