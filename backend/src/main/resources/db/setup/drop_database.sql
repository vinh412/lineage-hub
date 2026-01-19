-- ============================================================
-- LineageHub PostgreSQL Database Cleanup Script
-- ============================================================
-- Run this script as PostgreSQL superuser (postgres)
-- Usage: psql -U postgres -f drop_database.sql
-- WARNING: This will delete all data!
-- ============================================================

-- 1. Terminate all connections to the database
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'lineagehub'
  AND pid <> pg_backend_pid();

-- 2. Drop database
DROP DATABASE IF EXISTS lineagehub;

-- 3. Drop user
DROP USER IF EXISTS lineagehub;

\echo '✅ Database cleanup completed!'
\echo '   Database "lineagehub" dropped'
\echo '   User "lineagehub" dropped'
