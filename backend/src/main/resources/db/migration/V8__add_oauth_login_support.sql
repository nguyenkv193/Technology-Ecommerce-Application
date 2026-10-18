-- =========================================================
-- FLYWAY MIGRATION V8: OAUTH LOGIN SUPPORT
-- =========================================================

ALTER TABLE users
    ALTER COLUMN password DROP NOT NULL;

ALTER TABLE users
    ADD COLUMN auth_provider VARCHAR(50) NOT NULL DEFAULT 'LOCAL';

ALTER TABLE users
    ADD COLUMN provider_subject VARCHAR(255);

ALTER TABLE users
    ADD CONSTRAINT uk_users_auth_provider_subject
        UNIQUE (auth_provider, provider_subject);
