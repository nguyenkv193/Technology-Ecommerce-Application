ALTER TABLE brands
    ADD COLUMN IF NOT EXISTS origin_country VARCHAR(100);

UPDATE brands SET origin_country = 'Hoa Kỳ' WHERE slug IN ('apple', 'dell', 'nvidia') AND origin_country IS NULL;
UPDATE brands SET origin_country = 'Hàn Quốc' WHERE slug = 'samsung' AND origin_country IS NULL;
UPDATE brands SET origin_country = 'Đài Loan' WHERE slug = 'asus' AND origin_country IS NULL;
UPDATE brands SET origin_country = 'Nhật Bản' WHERE slug = 'sony' AND origin_country IS NULL;
UPDATE brands SET origin_country = 'Thụy Sĩ' WHERE slug = 'logitech' AND origin_country IS NULL;
