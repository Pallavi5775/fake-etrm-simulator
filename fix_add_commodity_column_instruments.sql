-- Add missing 'commodity' column to ctrm.instruments
ALTER TABLE ctrm.instruments ADD COLUMN commodity character varying(255);
-- Optionally, if you want to set NOT NULL and a default, use:
-- ALTER TABLE ctrm.instruments ALTER COLUMN commodity SET NOT NULL;
-- ALTER TABLE ctrm.instruments ALTER COLUMN commodity SET DEFAULT '';
