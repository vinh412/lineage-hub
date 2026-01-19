# 🛠️ LineageHub - Tech Stack

## 1. Tổng quan công nghệ

| Layer | Technology | Version |
|-------|------------|---------|
| Frontend | Next.js (App Router) | 14.x |
| Backend | Spring Boot | 3.2.x |
| Database | PostgreSQL | 16.x |
| ORM | Hibernate/JPA | 6.x |
| Auth | JWT + Spring Security | - |

## 2. Frontend Stack

### 2.1. Core Framework

| Package | Purpose | Version |
|---------|---------|---------|
| `next` | React framework với App Router | ^14.0.0 |
| `react` | UI library | ^18.2.0 |
| `typescript` | Type safety | ^5.0.0 |

### 2.2. Styling

| Package | Purpose | Version |
|---------|---------|---------|
| `tailwindcss` | Utility-first CSS | ^3.4.0 |
| `@tailwindcss/forms` | Form styling plugin | ^0.5.0 |
| `class-variance-authority` | Component variants | ^0.7.0 |
| `clsx` | Conditional classes | ^2.0.0 |
| `tailwind-merge` | Merge Tailwind classes | ^2.0.0 |

### 2.3. UI Components

| Package | Purpose | Version |
|---------|---------|---------|
| `@radix-ui/react-*` | Headless UI primitives | latest |
| `lucide-react` | Icons | ^0.300.0 |
| `sonner` | Toast notifications | ^1.0.0 |

### 2.4. State Management & Data Fetching

| Package | Purpose | Version |
|---------|---------|---------|
| `zustand` | Global state management | ^4.4.0 |
| `@tanstack/react-query` | Server state & caching | ^5.0.0 |
| `axios` | HTTP client | ^1.6.0 |

### 2.5. Family Tree Visualization

| Package | Purpose | Version |
|---------|---------|---------|
| `reactflow` | Interactive tree/graph | ^11.10.0 |
| `@xyflow/react` | React Flow (new package) | ^12.0.0 |
| `dagre` | Graph layout algorithm | ^0.8.5 |

**Lý do chọn React Flow:**
- Tích hợp tốt với React/Next.js
- Hỗ trợ zoom, pan, drag
- Custom node components
- Export to image
- Active community & documentation

### 2.6. Form & Validation

| Package | Purpose | Version |
|---------|---------|---------|
| `react-hook-form` | Form management | ^7.49.0 |
| `zod` | Schema validation | ^3.22.0 |
| `@hookform/resolvers` | Zod integration | ^3.3.0 |

### 2.7. Utilities

| Package | Purpose | Version |
|---------|---------|---------|
| `date-fns` | Date manipulation | ^3.0.0 |
| `html-to-image` | Export to PNG | ^1.11.0 |
| `jspdf` | Export to PDF | ^2.5.0 |

### 2.8. Development Tools

| Package | Purpose | Version |
|---------|---------|---------|
| `eslint` | Linting | ^8.0.0 |
| `prettier` | Code formatting | ^3.0.0 |
| `eslint-config-next` | Next.js ESLint rules | ^14.0.0 |

## 3. Backend Stack

### 3.1. Core Framework

```xml
<!-- Spring Boot Parent -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```

### 3.2. Dependencies

| Dependency | Purpose |
|------------|---------|
| `spring-boot-starter-web` | REST API |
| `spring-boot-starter-data-jpa` | Database access |
| `spring-boot-starter-security` | Authentication & Authorization |
| `spring-boot-starter-validation` | Input validation |
| `spring-boot-starter-oauth2-resource-server` | JWT support |

### 3.3. Database & ORM

| Dependency | Purpose |
|------------|---------|
| `postgresql` | PostgreSQL JDBC driver |
| `flyway-core` | Database migrations |
| `hibernate-core` | ORM (included in JPA starter) |

### 3.4. Security & JWT

| Dependency | Purpose |
|------------|---------|
| `jjwt-api` | JWT creation & validation |
| `jjwt-impl` | JWT implementation |
| `jjwt-jackson` | JWT JSON support |

### 3.5. Utilities

| Dependency | Purpose |
|------------|---------|
| `lombok` | Reduce boilerplate |
| `mapstruct` | DTO mapping |
| `springdoc-openapi-starter-webmvc-ui` | API documentation (Swagger) |

### 3.6. Testing

| Dependency | Purpose |
|------------|---------|
| `spring-boot-starter-test` | Testing framework |
| `spring-security-test` | Security testing |
| `h2` | In-memory database for tests |
| `testcontainers` | Integration testing with Docker |

### 3.7. Full pom.xml Dependencies

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Utilities -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    
    <!-- API Documentation -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.3.0</version>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 4. Database

### 4.1. PostgreSQL Configuration

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/lineagehub
    username: lineagehub
    password: ${DB_PASSWORD:lineagehub123}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate  # Sử dụng Flyway cho migrations
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: false
  
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
```

### 4.2. Lý do chọn PostgreSQL

| Feature | Benefit cho LineageHub |
|---------|------------------------|
| ACID compliance | Đảm bảo tính toàn vẹn quan hệ gia đình |
| Complex queries | Truy vấn quan hệ đa thế hệ |
| JSON support | Lưu metadata linh hoạt |
| Full-text search | Tìm kiếm thành viên |
| Recursive CTEs | Truy vấn cây gia phả |

## 5. Development Tools

### 5.1. IDE & Editor
- **Cursor IDE** (primary)
- IntelliJ IDEA (optional for Java)

### 5.2. Database Tools
- **pgAdmin 4** hoặc **DBeaver**
- DataGrip (optional)

### 5.3. API Testing
- **Postman** hoặc **Insomnia**
- Swagger UI (built-in at `/swagger-ui.html`)

### 5.4. Version Control
- Git
- GitHub / GitLab

## 6. Project Setup Commands

### 6.1. Frontend Setup

```bash
# Tạo Next.js project
npx create-next-app@latest frontend --typescript --tailwind --eslint --app --src-dir

# Cài đặt dependencies
cd frontend
npm install zustand @tanstack/react-query axios
npm install @radix-ui/react-dialog @radix-ui/react-dropdown-menu
npm install lucide-react sonner
npm install react-hook-form zod @hookform/resolvers
npm install @xyflow/react dagre
npm install date-fns html-to-image jspdf
npm install class-variance-authority clsx tailwind-merge

# Dev dependencies
npm install -D @types/node
```

### 6.2. Backend Setup

```bash
# Tạo Spring Boot project (sử dụng Spring Initializr)
# https://start.spring.io/

# Hoặc dùng Spring CLI
spring init --dependencies=web,data-jpa,security,validation,postgresql,flyway \
  --java-version=17 \
  --type=maven-project \
  --group-id=com.lineagehub \
  --artifact-id=backend \
  --name=LineageHub \
  backend
```

### 6.3. Database Setup

```bash
# Chạy PostgreSQL với Docker
docker run --name lineagehub-postgres \
  -e POSTGRES_DB=lineagehub \
  -e POSTGRES_USER=lineagehub \
  -e POSTGRES_PASSWORD=lineagehub123 \
  -p 5432:5432 \
  -d postgres:16

# Hoặc cài PostgreSQL local và tạo database
createdb lineagehub
```

## 7. Environment Variables

### 7.1. Frontend (.env.local)

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### 7.2. Backend (application.yml hoặc .env)

```yaml
# JWT
jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-for-jwt-signing}
  expiration: 86400000  # 24 hours

# Database
spring:
  datasource:
    password: ${DB_PASSWORD:lineagehub123}
```

## 8. Ports Configuration (Development)

| Service | Port | URL |
|---------|------|-----|
| Frontend (Next.js) | 3000 | http://localhost:3000 |
| Backend (Spring Boot) | 8080 | http://localhost:8080 |
| PostgreSQL | 5432 | localhost:5432 |
| Swagger UI | 8080 | http://localhost:8080/swagger-ui.html |

## 9. Deployment Options

Ứng dụng sẽ được **public trên internet**. Có 2 lựa chọn chính:

### 9.1. Self-hosted (VPS/Server riêng)

```
┌─────────────────────────────────────────────────────────────┐
│                    VPS / Dedicated Server                    │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │   Nginx     │  │  Docker     │  │     PostgreSQL      │ │
│  │  (Reverse   │──│  Compose    │──│     (Container      │ │
│  │   Proxy +   │  │             │  │      or native)     │ │
│  │   SSL)      │  │  FE + BE    │  │                     │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

**Providers:** DigitalOcean, Linode, Vultr, Hetzner, hoặc server riêng

**Yêu cầu:**
- VPS tối thiểu: 2GB RAM, 2 vCPU
- Docker + Docker Compose
- Nginx làm reverse proxy
- Let's Encrypt cho SSL/HTTPS
- Domain name

### 9.2. Cloud Platform

| Component | Option A (Budget) | Option B (Scalable) |
|-----------|-------------------|---------------------|
| Frontend | Vercel (free tier) | AWS CloudFront + S3 |
| Backend | Railway / Render | AWS ECS / GCP Cloud Run |
| Database | Railway PostgreSQL | AWS RDS / GCP Cloud SQL |
| File Storage | Local / S3 | AWS S3 / GCP Cloud Storage |

### 9.3. Docker Compose (Production)

```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  postgres:
    image: postgres:16
    restart: always
    environment:
      POSTGRES_DB: lineagehub
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - backend

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    restart: always
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_HOST: postgres
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      - postgres
    networks:
      - backend
      - frontend

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
      args:
        NEXT_PUBLIC_API_URL: ${API_URL}
    restart: always
    depends_on:
      - backend
    networks:
      - frontend

  nginx:
    image: nginx:alpine
    restart: always
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./nginx/ssl:/etc/nginx/ssl
      - ./certbot/conf:/etc/letsencrypt
    depends_on:
      - frontend
      - backend
    networks:
      - frontend

networks:
  frontend:
  backend:

volumes:
  postgres_data:
```

### 9.4. Environment Variables (Production)

```env
# .env.production

# Database
DB_USER=lineagehub_prod
DB_PASSWORD=<strong-password-here>
DB_HOST=postgres

# JWT (generate with: openssl rand -base64 32)
JWT_SECRET=<random-256-bit-key>

# API URL (public domain)
API_URL=https://api.lineagehub.example.com

# Frontend URL
FRONTEND_URL=https://lineagehub.example.com
```

### 9.5. SSL/HTTPS Setup (Certbot)

```bash
# Cài đặt Certbot cho Let's Encrypt
sudo apt install certbot python3-certbot-nginx

# Tạo certificate
sudo certbot --nginx -d lineagehub.example.com -d api.lineagehub.example.com

# Auto-renewal (crontab)
0 0 1 * * certbot renew --quiet
```

### 9.6. Nginx Configuration

```nginx
# /etc/nginx/sites-available/lineagehub
server {
    listen 80;
    server_name lineagehub.example.com api.lineagehub.example.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name lineagehub.example.com;

    ssl_certificate /etc/letsencrypt/live/lineagehub.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/lineagehub.example.com/privkey.pem;

    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
}

server {
    listen 443 ssl http2;
    server_name api.lineagehub.example.com;

    ssl_certificate /etc/letsencrypt/live/lineagehub.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/lineagehub.example.com/privkey.pem;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # File upload limit
    client_max_body_size 10M;
}
```

### 9.7. Security Checklist (Production)

- [ ] HTTPS enabled (SSL/TLS certificate)
- [ ] Strong JWT secret (256-bit random)
- [ ] Database password strong & unique
- [ ] Firewall configured (only 80, 443 open)
- [ ] Rate limiting enabled
- [ ] CORS configured for production domain only
- [ ] Environment variables not in code
- [ ] Regular database backups
- [ ] Monitoring & alerting setup
