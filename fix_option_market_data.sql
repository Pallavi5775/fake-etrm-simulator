-- Fix Option Market Data Duplicates
-- Remove duplicates from all option tables and add unique constraints

SET search_path TO ctrm;

-- Check for duplicates in all tables
SELECT 'option_forward_curves' as table_name, COUNT(*) as duplicates
FROM option_forward_curves
GROUP BY instrument_code, delivery_date, curve_date
HAVING COUNT(*) > 1;

SELECT 'option_volatility' as table_name, COUNT(*) as duplicates
FROM option_volatility
GROUP BY instrument_code, date
HAVING COUNT(*) > 1;

SELECT 'option_yield_curves' as table_name, COUNT(*) as duplicates
FROM option_yield_curves
GROUP BY curve_name, date
HAVING COUNT(*) > 1;

-- Remove duplicates from option_forward_curves
DELETE FROM option_forward_curves
WHERE id NOT IN (
    SELECT MIN(id)
    FROM option_forward_curves
    GROUP BY instrument_code, delivery_date, curve_date
);

-- Remove duplicates from option_volatility
DELETE FROM option_volatility
WHERE id NOT IN (
    SELECT MIN(id)
    FROM option_volatility
    GROUP BY instrument_code, date
);

-- Remove duplicates from option_yield_curves
DELETE FROM option_yield_curves
WHERE id NOT IN (
    SELECT MIN(id)
    FROM option_yield_curves
    GROUP BY curve_name, date
);

-- Add unique constraints to prevent future duplicates
ALTER TABLE option_forward_curves
ADD CONSTRAINT uk_option_forward_curves_unique
UNIQUE (instrument_code, delivery_date, curve_date);

ALTER TABLE option_volatility
ADD CONSTRAINT uk_option_volatility_unique
UNIQUE (instrument_code, date);

ALTER TABLE option_yield_curves
ADD CONSTRAINT uk_option_yield_curves_unique
UNIQUE (curve_name, date);

-- Verify the fixes
SELECT 'option_forward_curves' as table_name, COUNT(*) as total_records FROM option_forward_curves
UNION ALL
SELECT 'option_volatility' as table_name, COUNT(*) as total_records FROM option_volatility
UNION ALL
SELECT 'option_yield_curves' as table_name, COUNT(*) as total_records FROM option_yield_curves;

COMMIT;