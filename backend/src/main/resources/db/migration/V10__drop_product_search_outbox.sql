-- V10: Drop product search outbox table as Elasticsearch is deprecated in favor of PostgreSQL search
DROP TABLE IF EXISTS product_search_outbox CASCADE;
