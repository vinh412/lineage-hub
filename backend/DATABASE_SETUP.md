# 🗄️ PostgreSQL Database Setup Guide

Hướng dẫn setup database PostgreSQL cho LineageHub.

## 📋 Thông tin Database

- **Database Name:** `lineagehub`
- **Username:** `lineagehub`
- **Password:** `lineagehub123`
- **Host:** `localhost`
- **Port:** `5432`

---

## 🚀 Option 1: Setup với Docker (Recommended)

### Bước 1: Chạy PostgreSQL với Docker Compose

```bash
# Từ thư mục root của project
docker-compose up -d postgres
```

✅ **Database đã sẵn sàng!** Docker Compose tự động tạo database, user và cấp quyền.

### Bước 2: Verify

```bash
# Kiểm tra container đang chạy
docker ps

# Connect vào database để test
docker exec -it lineagehub-postgres psql -U lineagehub -d lineagehub
```

---

## 🛠️ Option 2: Setup Manual với PostgreSQL Local

### Bước 1: Cài đặt PostgreSQL 16

Download và cài đặt từ: https://www.postgresql.org/download/

### Bước 2: Chạy Setup Script

**Windows PowerShell:**
```powershell
# Chạy từ thư mục backend
$env:PGPASSWORD='postgres_password'
psql -U postgres -f src/main/resources/db/setup/setup_database.sql
```

**Windows Command Prompt:**
```cmd
REM Chạy từ thư mục backend
set PGPASSWORD=postgres_password
psql -U postgres -f src/main/resources/db/setup/setup_database.sql
```

**Linux/Mac:**
```bash
# Chạy từ thư mục backend
PGPASSWORD=postgres_password psql -U postgres -f src/main/resources/db/setup/setup_database.sql
```

### Bước 3: Verify Setup

```bash
# Connect vào database mới tạo
psql -U lineagehub -d lineagehub

# Trong psql console
lineagehub=> \l              # List databases
lineagehub=> \du             # List users
lineagehub=> \c lineagehub   # Connect to database
lineagehub=> \dt             # List tables (should be empty initially)
lineagehub=> \q              # Quit
```

---

## 🧪 Option 3: Tạo Database Thủ Công

### Bước 1: Connect vào PostgreSQL

```bash
psql -U postgres
```

### Bước 2: Tạo User

```sql
CREATE USER lineagehub WITH PASSWORD 'lineagehub123';
```

### Bước 3: Tạo Database

```sql
CREATE DATABASE lineagehub
    WITH 
    OWNER = lineagehub
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8';
```

### Bước 4: Cấp Quyền

```sql
GRANT ALL PRIVILEGES ON DATABASE lineagehub TO lineagehub;

-- Connect vào database
\c lineagehub

-- Cấp quyền schema
GRANT ALL ON SCHEMA public TO lineagehub;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO lineagehub;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO lineagehub;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

### Bước 5: Exit

```sql
\q
```

---

## ✅ Verify Database Configuration

### 1. Test Connection từ Command Line

```bash
# Test connection
psql -U lineagehub -d lineagehub -h localhost -p 5432

# Nếu thành công, bạn sẽ thấy:
# lineagehub=>
```

### 2. Test Connection từ Spring Boot

```bash
# Chạy từ thư mục backend
cd backend
mvn spring-boot:run
```

Nếu thấy log:
```
Flyway: Migrating schema "public" to version "1 - init schema"
Flyway: Successfully applied 3 migrations
Started LineageHubApplication in X.XXX seconds
```

✅ **Database setup thành công!**

---

## 🗑️ Cleanup/Reset Database

Nếu muốn xóa database và bắt đầu lại:

### Option 1: Dùng Script

```bash
# Chạy từ thư mục backend
psql -U postgres -f src/main/resources/db/setup/drop_database.sql
```

### Option 2: Manual

```bash
psql -U postgres

DROP DATABASE lineagehub;
DROP USER lineagehub;
```

---

## 🔧 Troubleshooting

### Lỗi: "role 'lineagehub' does not exist"

**Giải pháp:** Tạo user trước khi tạo database
```sql
CREATE USER lineagehub WITH PASSWORD 'lineagehub123';
```

### Lỗi: "database 'lineagehub' does not exist"

**Giải pháp:** Chạy setup script hoặc tạo database thủ công

### Lỗi: "password authentication failed"

**Giải pháp:** 
1. Kiểm tra password trong `application.yml`
2. Kiểm tra file `pg_hba.conf` của PostgreSQL
3. Đảm bảo method authentication là `md5` hoặc `scram-sha-256`

### Lỗi: Connection timeout

**Giải pháp:**
1. Kiểm tra PostgreSQL service đang chạy
```bash
# Windows
services.msc  # Tìm "postgresql"

# Linux
sudo systemctl status postgresql

# Mac
brew services list
```

2. Kiểm tra port 5432 không bị block
```bash
netstat -an | grep 5432
```

---

## 📊 Database Management Tools

### pgAdmin 4
- URL: http://localhost:5050 (nếu dùng Docker Compose)
- Email: `admin@lineagehub.local`
- Password: `admin123`

### DBeaver (Recommended)
Download: https://dbeaver.io/

**Connection settings:**
- Host: `localhost`
- Port: `5432`
- Database: `lineagehub`
- Username: `lineagehub`
- Password: `lineagehub123`

---

## 🔒 Production Setup

Đối với môi trường production:

1. **Đổi password mạnh hơn**
```sql
ALTER USER lineagehub WITH PASSWORD 'strong-random-password';
```

2. **Set environment variables**
```bash
export DB_PASSWORD=strong-random-password
```

3. **Update application.yml**
```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
```

4. **Enable SSL connection**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/lineagehub?ssl=true&sslmode=require
```

---

## 📝 Next Steps

Sau khi setup database thành công:

1. ✅ Chạy Spring Boot application: `mvn spring-boot:run`
2. ✅ Flyway sẽ tự động chạy migrations
3. ✅ Super Admin account tự động được tạo
4. ✅ Access Swagger UI: http://localhost:8080/swagger-ui.html
5. ✅ Login với: `admin@lineagehub.local` / `Admin@123`
