# 🔧 Troubleshooting Guide

## Lỗi thường gặp và cách khắc phục

### 1. "Project configuration is not up-to-date with pom.xml"

**Nguyên nhân:** IDE chưa reload Maven project sau khi thay đổi pom.xml

**Giải pháp:**

#### Cursor/VS Code:
```bash
# Clean và rebuild
mvn clean install -DskipTests

# Hoặc reload Maven project trong IDE
```

#### IntelliJ IDEA:
- Right-click vào `pom.xml` → **Maven** → **Reload Project**
- Hoặc: `Ctrl+Shift+O` (Reload Maven Projects)
- Hoặc: File → Invalidate Caches / Restart

---

### 2. "Bad HQL grammar" hoặc Query syntax error

**Nguyên nhân:** 
- Code đã fix nhưng compiled classes cũ vẫn còn trong `target/`
- IDE cache chưa được clear

**Giải pháp:**

```bash
# Xóa target directory
mvn clean

# Rebuild
mvn compile

# Hoặc rebuild và run
mvn clean spring-boot:run
```

---

### 3. Database Connection Error

**Lỗi:** `Connection refused` hoặc `Cannot connect to database`

**Giải pháp:**

1. **Kiểm tra PostgreSQL đang chạy:**

```bash
# Docker
docker ps | grep postgres

# Windows Service
services.msc  # Tìm postgresql

# Linux
sudo systemctl status postgresql
```

2. **Kiểm tra connection string trong application.yml:**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/lineagehub
    username: lineagehub
    password: lineagehub123
```

3. **Test connection trực tiếp:**

```bash
psql -U lineagehub -d lineagehub -h localhost -p 5432
```

---

### 4. Flyway Migration Error

**Lỗi:** `FlywayException: Found non-empty schema(s) "public" but no schema history table`

**Giải pháp:**

**Option 1: Reset database (Development only)**
```bash
# Drop và recreate
psql -U postgres -f src/main/resources/db/setup/drop_database.sql
psql -U postgres -f src/main/resources/db/setup/setup_database.sql
```

**Option 2: Baseline existing database**
```bash
# Run với Spring Boot
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.flyway.baseline-on-migrate=true"
```

---

### 5. Lombok Annotation Processing Error

**Lỗi:** `cannot find symbol` cho getter/setter methods

**Giải pháp:**

1. **Enable Annotation Processing trong IDE:**

**IntelliJ:**
- Settings → Build → Compiler → Annotation Processors
- Check "Enable annotation processing"

**VS Code/Cursor:**
- Install Java Extension Pack
- Reload Window

2. **Rebuild project:**
```bash
mvn clean compile
```

---

### 6. MapStruct Error

**Lỗi:** `Cannot find implementation for mapper`

**Giải pháp:**

1. **Đảm bảo annotation processor được config đúng trong pom.xml** (đã có sẵn)

2. **Clean và rebuild:**
```bash
mvn clean install
```

3. **Check generated mappers:**
```bash
# Generated mappers sẽ ở:
target/generated-sources/annotations/com/lineagehub/mapper/
```

---

### 7. JWT Token Error

**Lỗi:** `JWT signature does not match locally computed signature`

**Giải pháp:**

**Nguyên nhân:** JWT secret bị thay đổi giữa các lần chạy

**Fix:**
1. Set JWT_SECRET cố định trong environment variable
2. Hoặc dùng secret mặc định trong application.yml

```bash
# Set environment variable
export JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

---

### 8. CORS Error từ Frontend

**Lỗi:** `CORS policy: No 'Access-Control-Allow-Origin' header`

**Giải pháp:**

Check `application-dev.yml`:

```yaml
cors:
  allowed-origins: http://localhost:3000,http://localhost:3001
  allowed-methods: GET,POST,PUT,PATCH,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true
```

---

### 9. Port Already in Use

**Lỗi:** `Port 8080 was already in use`

**Giải pháp:**

**Option 1: Kill process trên port 8080**

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

**Option 2: Đổi port**

```bash
# Chạy với port khác
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

---

### 10. Maven Dependency Download Error

**Lỗi:** `Could not resolve dependencies` hoặc `Failed to download`

**Giải pháp:**

1. **Clear Maven cache:**
```bash
# Windows
rmdir /s /q %USERPROFILE%\.m2\repository

# Linux/Mac
rm -rf ~/.m2/repository
```

2. **Force update:**
```bash
mvn clean install -U
```

3. **Check internet connection và Maven settings**

---

## 🚀 Quick Fix Commands

### Khi gặp vấn đề về compilation:

```bash
# Full clean và rebuild
mvn clean install -DskipTests

# Clean target directory
mvn clean

# Run with fresh build
mvn clean spring-boot:run
```

### Khi gặp vấn đề về database:

```bash
# Reset database (Development only!)
cd backend
psql -U postgres -f src/main/resources/db/setup/drop_database.sql
psql -U postgres -f src/main/resources/db/setup/setup_database.sql

# Run application
mvn spring-boot:run
```

### Khi gặp vấn đề về IDE:

1. Reload Maven project
2. Invalidate caches (nếu IntelliJ)
3. Restart IDE
4. Run `mvn clean install`

---

## 📝 Useful Commands

### Maven Commands

```bash
# Clean project
mvn clean

# Compile only
mvn compile

# Run tests
mvn test

# Package to JAR
mvn package

# Install to local repository
mvn install

# Run application
mvn spring-boot:run

# Skip tests
mvn clean install -DskipTests

# Force update dependencies
mvn clean install -U

# Show dependency tree
mvn dependency:tree

# Check for plugin updates
mvn versions:display-plugin-updates
```

### Docker Commands

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f postgres

# Restart service
docker-compose restart postgres

# Remove volumes (delete data)
docker-compose down -v
```

### PostgreSQL Commands

```bash
# Connect to database
psql -U lineagehub -d lineagehub

# List databases
\l

# List tables
\dt

# Describe table
\d table_name

# Show table data
SELECT * FROM users LIMIT 10;

# Quit
\q
```

---

## 🔍 Debug Mode

Để debug chi tiết hơn, chạy với profile debug:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.root=DEBUG"
```

Hoặc thêm vào `application-dev.yml`:

```yaml
logging:
  level:
    root: DEBUG
    com.lineagehub: TRACE
```

---

## 📞 Khi cần help thêm

1. Check logs trong console output
2. Check file `logs/application.log` (nếu có configure file logging)
3. Check Swagger UI để test API: http://localhost:8080/swagger-ui.html
4. Xem docs: `backend/README.md` và `backend/DATABASE_SETUP.md`
