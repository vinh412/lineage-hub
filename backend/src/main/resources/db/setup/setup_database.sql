-- ============================================================
-- LineageHub PostgreSQL Database Setup Script
-- ============================================================
-- Run this script as PostgreSQL superuser (postgres)
-- Usage: psql -U postgres -f setup_database.sql
-- ============================================================

-- 1. Create user (role) for LineageHub
CREATE USER lineagehub WITH PASSWORD 'lineagehub123';

-- 2. Create database
CREATE DATABASE lineagehub
    WITH 
    OWNER = lineagehub
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- 3. Grant privileges to user
GRANT ALL PRIVILEGES ON DATABASE lineagehub TO lineagehub;

-- 4. Connect to the lineagehub database
\c lineagehub

-- 5. Grant schema privileges
GRANT ALL ON SCHEMA public TO lineagehub;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO lineagehub;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO lineagehub;

-- 6. Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO lineagehub;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO lineagehub;

-- 7. Enable UUID extension (required for the application)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 8. Verify setup
SELECT version();
SELECT current_database();
SELECT current_user;

\echo '✅ Database setup completed successfully!'
\echo '   Database: lineagehub'
\echo '   User: lineagehub'
\echo '   Password: lineagehub123'
\echo ''
\echo 'You can now run the Spring Boot application with:'
\echo '   mvn spring-boot:run'
