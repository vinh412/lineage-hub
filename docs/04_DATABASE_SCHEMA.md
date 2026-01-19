# 🗄️ LineageHub - Database Schema

## 1. Entity Relationship Diagram

```
┌─────────────────────┐         ┌─────────────────────────┐
│       users         │         │      user_roles         │
├─────────────────────┤         ├─────────────────────────┤
│ id (PK)             │◀────────│ user_id (FK)            │
│ email               │         │ role                    │──┐
│ password_hash       │         │ managed_member_id (FK)  │──┼──┐
│ full_name           │         │ created_at              │  │  │
│ status              │         │ created_by (FK)         │  │  │
│ created_at          │         └─────────────────────────┘  │  │
│ updated_at          │                                      │  │
└─────────────────────┘    role: SUPER_ADMIN, BRANCH_ADMIN, USER
                                                             │  │
┌─────────────────────┐                                      │  │
│      members        │◀─────────────────────────────────────┘  │
├─────────────────────┤                                         │
│ id (PK)             │◀────────────────────────────────────────┘
│ full_name           │
│ gender              │
│ birth_date          │
│ death_date          │
│ is_blood_relative   │
│ branch_name         │
│ address             │
│ phone               │
│ email               │
│ avatar_url          │
│ notes               │
│ generation          │
│ created_by (FK)     │
│ created_at          │
│ updated_at          │
└─────────────────────┘
        ▲       ▲
        │       │
┌───────┴───────┴─────┐
│    relationships    │
├─────────────────────┤
│ id (PK)             │
│ from_member_id (FK) │
│ to_member_id (FK)   │
│ relationship_type   │  (PARENT_CHILD, SPOUSE)
│ created_by (FK)     │
│ created_at          │
└─────────────────────┘

┌─────────────────────┐
│     audit_logs      │
├─────────────────────┤
│ id (PK)             │
│ entity_type         │  (USER, MEMBER, RELATIONSHIP, USER_ROLE)
│ entity_id           │
│ action              │  (CREATE, UPDATE, DELETE)
│ old_value (JSONB)   │
│ new_value (JSONB)   │
│ user_id (FK)        │
│ created_at          │
└─────────────────────┘
```

## 2. Mô hình phân quyền với `user_roles`

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    USER ROLES - Ví dụ                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  User: admin@lineagehub.local                                              │
│  └── Role: SUPER_ADMIN (managed_member_id = NULL)                          │
│      → Quản lý toàn bộ hệ thống                                            │
│                                                                             │
│  User: nguyen.van.a@gmail.com                                              │
│  ├── Role: BRANCH_ADMIN (managed_member_id = A2)                           │
│  │   → Quản lý subtree A2 (A2, B1, B2, B3, C1, C2, vợ/chồng)              │
│  └── Role: BRANCH_ADMIN (managed_member_id = A4)                           │
│      → Quản lý subtree A4 (A4, D1, D2, vợ/chồng)                          │
│                                                                             │
│  User: viewer@gmail.com                                                    │
│  └── Role: USER (managed_member_id = NULL)                                 │
│      → Chỉ xem, không được sửa                                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

                    A1 (Tổ tiên - đời 1)
                     │
         ┌───────────┼───────────┐
         │           │           │
        A2          A3          A4           (đời 2)
         │ ◄─ User quản lý       │ ◄─ User cũng quản lý
     ┌───┼───┐               ┌───┼───┐
    B1  B2  B3              D1  D2  D3       (đời 3)
     │
   ┌─┴─┐
  C1   C2                                    (đời 4)
```

## 3. Chi tiết các bảng

### 3.1. Bảng `users` - Người dùng hệ thống

```sql
CREATE TABLE users (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email               VARCHAR(255) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    full_name           VARCHAR(255) NOT NULL,
    status              VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
```

| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| email | VARCHAR(255) | Email đăng nhập (unique) |
| password_hash | VARCHAR(255) | Mật khẩu đã hash (BCrypt) |
| full_name | VARCHAR(255) | Họ tên người dùng |
| status | VARCHAR(50) | Trạng thái: PENDING, ACTIVE, INACTIVE |
| created_at | TIMESTAMP | Thời điểm tạo |
| updated_at | TIMESTAMP | Thời điểm cập nhật |

> **Lưu ý:** Cột `role` và `managed_member_id` đã được chuyển sang bảng `user_roles`

### 3.2. Bảng `user_roles` - Phân quyền người dùng

```sql
CREATE TABLE user_roles (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role                VARCHAR(50) NOT NULL,
    managed_member_id   UUID REFERENCES members(id) ON DELETE CASCADE,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by          UUID REFERENCES users(id) ON DELETE SET NULL,
    
    CONSTRAINT chk_role CHECK (role IN ('SUPER_ADMIN', 'BRANCH_ADMIN', 'USER')),
    -- SUPER_ADMIN và USER không cần managed_member_id
    -- BRANCH_ADMIN bắt buộc phải có managed_member_id
    CONSTRAINT chk_managed_member CHECK (
        (role IN ('SUPER_ADMIN', 'USER') AND managed_member_id IS NULL)
        OR
        (role = 'BRANCH_ADMIN' AND managed_member_id IS NOT NULL)
    ),
    -- Mỗi user chỉ có 1 role SUPER_ADMIN hoặc USER
    -- Nhưng có thể có nhiều role BRANCH_ADMIN với các managed_member_id khác nhau
    CONSTRAINT uk_user_role_member UNIQUE (user_id, role, managed_member_id)
);

CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role);
CREATE INDEX idx_user_roles_managed_member ON user_roles(managed_member_id);
```

| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| user_id | UUID | **Bắt buộc** - User được gán role |
| role | VARCHAR(50) | **Bắt buộc** - SUPER_ADMIN, BRANCH_ADMIN, USER |
| managed_member_id | UUID | Member gốc (chỉ cho BRANCH_ADMIN) |
| created_at | TIMESTAMP | Thời điểm tạo |
| created_by | UUID | Người gán role |

**Quy tắc phân quyền:**

| Role | managed_member_id | Mô tả |
|------|-------------------|-------|
| SUPER_ADMIN | NULL (bắt buộc) | Quản lý toàn bộ hệ thống |
| BRANCH_ADMIN | UUID (bắt buộc) | Quản lý subtree từ member này |
| USER | NULL (bắt buộc) | Chỉ xem, không sửa |

**Ví dụ data:**

```sql
-- User admin có role SUPER_ADMIN
INSERT INTO user_roles (user_id, role, managed_member_id)
VALUES ('user-admin-uuid', 'SUPER_ADMIN', NULL);

-- User nguyen.van.a có 2 role BRANCH_ADMIN (quản lý 2 subtree)
INSERT INTO user_roles (user_id, role, managed_member_id)
VALUES ('user-a-uuid', 'BRANCH_ADMIN', 'member-a2-uuid');

INSERT INTO user_roles (user_id, role, managed_member_id)
VALUES ('user-a-uuid', 'BRANCH_ADMIN', 'member-a4-uuid');

-- User viewer chỉ có role USER
INSERT INTO user_roles (user_id, role, managed_member_id)
VALUES ('user-viewer-uuid', 'USER', NULL);
```

### 3.3. Bảng `members` - Thành viên gia phả

```sql
CREATE TABLE members (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name           VARCHAR(255) NOT NULL,
    gender              VARCHAR(20) NOT NULL,
    birth_date          DATE,
    death_date          DATE,
    is_blood_relative   BOOLEAN NOT NULL DEFAULT TRUE,
    branch_name         VARCHAR(255),
    address             TEXT,
    phone               VARCHAR(20),
    email               VARCHAR(255),
    avatar_url          VARCHAR(500),
    notes               TEXT,
    generation          INTEGER,
    created_by          UUID,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT chk_dates CHECK (death_date IS NULL OR death_date >= birth_date)
);

CREATE INDEX idx_members_full_name ON members(full_name);
CREATE INDEX idx_members_generation ON members(generation);
CREATE INDEX idx_members_birth_date ON members(birth_date);
CREATE INDEX idx_members_is_blood ON members(is_blood_relative);

-- Full-text search index
CREATE INDEX idx_members_search ON members 
USING gin(to_tsvector('simple', full_name || ' ' || COALESCE(branch_name, '') || ' ' || COALESCE(notes, '')));
```

| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| full_name | VARCHAR(255) | **Bắt buộc** - Họ tên đầy đủ |
| gender | VARCHAR(20) | **Bắt buộc** - MALE, FEMALE, OTHER |
| birth_date | DATE | Ngày sinh |
| death_date | DATE | Ngày mất (NULL nếu còn sống) |
| is_blood_relative | BOOLEAN | **Bắt buộc** - TRUE: con ruột, FALSE: dâu/rể |
| branch_name | VARCHAR(255) | Tên nhánh (VD: "Nhánh Cả", "Chi Hai") |
| address | TEXT | Địa chỉ |
| phone | VARCHAR(20) | Số điện thoại |
| email | VARCHAR(255) | Email |
| avatar_url | VARCHAR(500) | URL ảnh đại diện |
| notes | TEXT | Ghi chú khác |
| generation | INTEGER | Đời thứ X (từ tổ tiên chung) |
| created_by | UUID | Người tạo record |
| created_at | TIMESTAMP | Thời điểm tạo |
| updated_at | TIMESTAMP | Thời điểm cập nhật |

### 3.4. Bảng `relationships` - Quan hệ gia đình

```sql
CREATE TABLE relationships (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_member_id      UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    to_member_id        UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    relationship_type   VARCHAR(50) NOT NULL,
    created_by          UUID,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_relationship_type CHECK (relationship_type IN ('PARENT_CHILD', 'SPOUSE')),
    CONSTRAINT chk_not_self_relation CHECK (from_member_id != to_member_id),
    CONSTRAINT uk_relationship UNIQUE (from_member_id, to_member_id, relationship_type)
);

CREATE INDEX idx_relationships_from ON relationships(from_member_id);
CREATE INDEX idx_relationships_to ON relationships(to_member_id);
CREATE INDEX idx_relationships_type ON relationships(relationship_type);
```

| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| from_member_id | UUID | Thành viên nguồn |
| to_member_id | UUID | Thành viên đích |
| relationship_type | VARCHAR(50) | Loại quan hệ |
| created_by | UUID | Người tạo quan hệ |
| created_at | TIMESTAMP | Thời điểm tạo |

**Quy ước quan hệ:**

| Type | from_member | to_member | Ý nghĩa |
|------|-------------|-----------|---------|
| PARENT_CHILD | Cha/Mẹ | Con | from_member là cha/mẹ của to_member |
| SPOUSE | Vợ/Chồng | Vợ/Chồng | Quan hệ 2 chiều (tạo 2 records) |

### 3.5. Bảng `audit_logs` - Lịch sử chỉnh sửa

```sql
CREATE TABLE audit_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type     VARCHAR(50) NOT NULL,
    entity_id       UUID NOT NULL,
    action          VARCHAR(20) NOT NULL,
    old_value       JSONB,
    new_value       JSONB,
    user_id         UUID,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_entity_type CHECK (entity_type IN ('USER', 'MEMBER', 'RELATIONSHIP', 'USER_ROLE')),
    CONSTRAINT chk_action CHECK (action IN ('CREATE', 'UPDATE', 'DELETE'))
);

CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at DESC);
```

### 3.6. Bảng `export_history` - Lịch sử xuất gia phả (Optional)

```sql
CREATE TABLE export_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    export_type     VARCHAR(20) NOT NULL,
    file_name       VARCHAR(255) NOT NULL,
    file_size       BIGINT,
    export_config   JSONB,
    exported_by     UUID,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_export_type CHECK (export_type IN ('PNG', 'PDF'))
);

CREATE INDEX idx_export_history_user ON export_history(exported_by);
CREATE INDEX idx_export_history_year ON export_history(EXTRACT(YEAR FROM created_at));
```

## 4. Flyway Migrations

### 4.1. V1__init_schema.sql

```sql
-- Migration V1: Initial schema

-- Members table (tạo trước vì user_roles tham chiếu đến)
CREATE TABLE members (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name           VARCHAR(255) NOT NULL,
    gender              VARCHAR(20) NOT NULL,
    birth_date          DATE,
    death_date          DATE,
    is_blood_relative   BOOLEAN NOT NULL DEFAULT TRUE,
    branch_name         VARCHAR(255),
    address             TEXT,
    phone               VARCHAR(20),
    email               VARCHAR(255),
    avatar_url          VARCHAR(500),
    notes               TEXT,
    generation          INTEGER,
    created_by          UUID,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT chk_dates CHECK (death_date IS NULL OR death_date >= birth_date)
);

-- Users table
CREATE TABLE users (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email               VARCHAR(255) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    full_name           VARCHAR(255) NOT NULL,
    status              VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'ACTIVE', 'INACTIVE'))
);

-- User roles table
CREATE TABLE user_roles (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role                VARCHAR(50) NOT NULL,
    managed_member_id   UUID REFERENCES members(id) ON DELETE CASCADE,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by          UUID REFERENCES users(id) ON DELETE SET NULL,
    
    CONSTRAINT chk_role CHECK (role IN ('SUPER_ADMIN', 'BRANCH_ADMIN', 'USER')),
    CONSTRAINT chk_managed_member CHECK (
        (role IN ('SUPER_ADMIN', 'USER') AND managed_member_id IS NULL)
        OR
        (role = 'BRANCH_ADMIN' AND managed_member_id IS NOT NULL)
    ),
    CONSTRAINT uk_user_role_member UNIQUE (user_id, role, managed_member_id)
);

-- Add FK for members.created_by
ALTER TABLE members 
ADD CONSTRAINT fk_members_created_by 
FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL;

-- Relationships table
CREATE TABLE relationships (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_member_id      UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    to_member_id        UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    relationship_type   VARCHAR(50) NOT NULL,
    created_by          UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_relationship_type CHECK (relationship_type IN ('PARENT_CHILD', 'SPOUSE')),
    CONSTRAINT chk_not_self_relation CHECK (from_member_id != to_member_id),
    CONSTRAINT uk_relationship UNIQUE (from_member_id, to_member_id, relationship_type)
);

-- Audit logs table
CREATE TABLE audit_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type     VARCHAR(50) NOT NULL,
    entity_id       UUID NOT NULL,
    action          VARCHAR(20) NOT NULL,
    old_value       JSONB,
    new_value       JSONB,
    user_id         UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_entity_type CHECK (entity_type IN ('USER', 'MEMBER', 'RELATIONSHIP', 'USER_ROLE')),
    CONSTRAINT chk_action CHECK (action IN ('CREATE', 'UPDATE', 'DELETE'))
);

-- Export history table
CREATE TABLE export_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    export_type     VARCHAR(20) NOT NULL,
    file_name       VARCHAR(255) NOT NULL,
    file_size       BIGINT,
    export_config   JSONB,
    exported_by     UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_export_type CHECK (export_type IN ('PNG', 'PDF'))
);

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);

CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role);
CREATE INDEX idx_user_roles_managed_member ON user_roles(managed_member_id);

CREATE INDEX idx_members_full_name ON members(full_name);
CREATE INDEX idx_members_generation ON members(generation);
CREATE INDEX idx_members_birth_date ON members(birth_date);
CREATE INDEX idx_members_is_blood ON members(is_blood_relative);

CREATE INDEX idx_relationships_from ON relationships(from_member_id);
CREATE INDEX idx_relationships_to ON relationships(to_member_id);
CREATE INDEX idx_relationships_type ON relationships(relationship_type);

CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at DESC);

CREATE INDEX idx_export_history_user ON export_history(exported_by);
```

### 4.2. V2__seed_super_admin.sql

```sql
-- Migration V2: Seed Super Admin user
-- Password: Admin@123 (BCrypt hash)

-- Create admin user
INSERT INTO users (id, email, password_hash, full_name, status)
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'admin@lineagehub.local',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjqQBqAGLNAX8T9FqHFm4eWlKpWYKe',
    'Super Admin',
    'ACTIVE'
);

-- Assign SUPER_ADMIN role
INSERT INTO user_roles (id, user_id, role, managed_member_id, created_by)
VALUES (
    'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'SUPER_ADMIN',
    NULL,
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'
);
```

### 4.3. V3__add_fulltext_search.sql

```sql
-- Migration V3: Add full-text search index

CREATE INDEX idx_members_search ON members 
USING gin(to_tsvector('simple', full_name || ' ' || COALESCE(branch_name, '') || ' ' || COALESCE(notes, '')));
```

## 5. Query Examples

### 5.1. Lấy tất cả roles của một user

```sql
SELECT 
    ur.id,
    ur.role,
    ur.managed_member_id,
    m.full_name AS managed_member_name,
    m.generation AS managed_member_generation
FROM user_roles ur
LEFT JOIN members m ON ur.managed_member_id = m.id
WHERE ur.user_id = :user_id
ORDER BY ur.role, m.full_name;
```

### 5.2. Kiểm tra user có quyền cao nhất là gì

```sql
SELECT 
    CASE 
        WHEN EXISTS (SELECT 1 FROM user_roles WHERE user_id = :user_id AND role = 'SUPER_ADMIN')
        THEN 'SUPER_ADMIN'
        WHEN EXISTS (SELECT 1 FROM user_roles WHERE user_id = :user_id AND role = 'BRANCH_ADMIN')
        THEN 'BRANCH_ADMIN'
        WHEN EXISTS (SELECT 1 FROM user_roles WHERE user_id = :user_id AND role = 'USER')
        THEN 'USER'
        ELSE 'NO_ROLE'
    END AS highest_role;
```

### 5.3. Lấy tất cả managed_member_ids của user (BRANCH_ADMIN)

```sql
SELECT managed_member_id
FROM user_roles
WHERE user_id = :user_id
AND role = 'BRANCH_ADMIN'
AND managed_member_id IS NOT NULL;
```

### 5.4. Kiểm tra user có quyền sửa member không (hỗ trợ nhiều subtree)

```sql
-- Function kiểm tra quyền
CREATE OR REPLACE FUNCTION can_edit_member(
    p_user_id UUID,
    p_member_id UUID
) RETURNS BOOLEAN AS $$
DECLARE
    v_managed_member_id UUID;
BEGIN
    -- Kiểm tra SUPER_ADMIN
    IF EXISTS (SELECT 1 FROM user_roles WHERE user_id = p_user_id AND role = 'SUPER_ADMIN') THEN
        RETURN TRUE;
    END IF;
    
    -- Kiểm tra BRANCH_ADMIN với nhiều subtrees
    FOR v_managed_member_id IN 
        SELECT managed_member_id FROM user_roles 
        WHERE user_id = p_user_id AND role = 'BRANCH_ADMIN'
    LOOP
        -- Kiểm tra member có trong subtree của managed_member_id không
        IF EXISTS (
            WITH RECURSIVE subtree AS (
                SELECT id FROM members WHERE id = v_managed_member_id
                UNION
                SELECT m.id
                FROM members m
                INNER JOIN relationships r ON r.to_member_id = m.id
                INNER JOIN subtree s ON r.from_member_id = s.id
            )
            SELECT 1 FROM subtree WHERE id = p_member_id
        ) THEN
            RETURN TRUE;
        END IF;
    END LOOP;
    
    RETURN FALSE;
END;
$$ LANGUAGE plpgsql;
```

### 5.5. Lấy subtree từ một managed_member

```sql
WITH RECURSIVE subtree AS (
    -- Base case: member gốc
    SELECT 
        m.id, m.full_name, m.is_blood_relative,
        0 AS depth
    FROM members m
    WHERE m.id = :managed_member_id
    
    UNION
    
    -- Recursive case: tìm con và vợ/chồng
    SELECT 
        m.id, m.full_name, m.is_blood_relative,
        s.depth + 1
    FROM members m
    INNER JOIN relationships r ON (
        (r.from_member_id IN (SELECT id FROM subtree WHERE is_blood_relative = TRUE) 
         AND r.to_member_id = m.id 
         AND r.relationship_type = 'PARENT_CHILD')
        OR
        (r.from_member_id IN (SELECT id FROM subtree) 
         AND r.to_member_id = m.id 
         AND r.relationship_type = 'SPOUSE')
    )
    INNER JOIN subtree s ON r.from_member_id = s.id
    WHERE m.id NOT IN (SELECT id FROM subtree)
)
SELECT * FROM subtree ORDER BY depth, full_name;
```

### 5.6. Kiểm tra có thể sửa/xóa relationship không (hỗ trợ nhiều subtree)

```sql
CREATE OR REPLACE FUNCTION can_edit_relationship(
    p_user_id UUID,
    p_relationship_id UUID
) RETURNS BOOLEAN AS $$
DECLARE
    v_from_member_id UUID;
    v_to_member_id UUID;
    v_rel_type VARCHAR(50);
    v_managed_member_id UUID;
BEGIN
    -- SUPER_ADMIN có thể edit tất cả
    IF EXISTS (SELECT 1 FROM user_roles WHERE user_id = p_user_id AND role = 'SUPER_ADMIN') THEN
        RETURN TRUE;
    END IF;
    
    -- Lấy thông tin relationship
    SELECT from_member_id, to_member_id, relationship_type
    INTO v_from_member_id, v_to_member_id, v_rel_type
    FROM relationships WHERE id = p_relationship_id;
    
    -- Kiểm tra từng BRANCH_ADMIN role
    FOR v_managed_member_id IN 
        SELECT managed_member_id FROM user_roles 
        WHERE user_id = p_user_id AND role = 'BRANCH_ADMIN'
    LOOP
        -- Nếu là quan hệ PARENT_CHILD và to_member = managed_member
        -- → Đây là quan hệ cha→managed_member → KHÔNG được sửa
        IF v_rel_type = 'PARENT_CHILD' AND v_to_member_id = v_managed_member_id THEN
            CONTINUE; -- Thử subtree khác
        END IF;
        
        -- Kiểm tra cả 2 member đều trong subtree
        IF can_edit_member(p_user_id, v_from_member_id) 
           AND can_edit_member(p_user_id, v_to_member_id) THEN
            RETURN TRUE;
        END IF;
    END LOOP;
    
    RETURN FALSE;
END;
$$ LANGUAGE plpgsql;
```

### 5.7. Lấy cây gia phả cho hiển thị

```sql
WITH RECURSIVE family_tree AS (
    -- Base case: tổ tiên (member không có cha trong bảng)
    SELECT 
        m.id, m.full_name, m.gender, m.generation,
        m.birth_date, m.death_date, m.is_blood_relative,
        m.branch_name, m.avatar_url,
        NULL::UUID AS parent_id,
        0 AS depth
    FROM members m
    WHERE NOT EXISTS (
        SELECT 1 FROM relationships r 
        WHERE r.to_member_id = m.id 
        AND r.relationship_type = 'PARENT_CHILD'
    )
    AND m.is_blood_relative = TRUE
    
    UNION ALL
    
    -- Recursive case: tìm con
    SELECT 
        m.id, m.full_name, m.gender, m.generation,
        m.birth_date, m.death_date, m.is_blood_relative,
        m.branch_name, m.avatar_url,
        r.from_member_id AS parent_id,
        ft.depth + 1
    FROM members m
    INNER JOIN relationships r ON r.to_member_id = m.id
    INNER JOIN family_tree ft ON r.from_member_id = ft.id
    WHERE r.relationship_type = 'PARENT_CHILD'
)
SELECT * FROM family_tree ORDER BY depth, full_name;
```

### 5.8. Tìm kiếm full-text

```sql
SELECT * FROM members
WHERE to_tsvector('simple', full_name || ' ' || COALESCE(branch_name, '') || ' ' || COALESCE(notes, '')) 
      @@ plainto_tsquery('simple', :search_term)
ORDER BY full_name;
```

## 6. JPA Entity Examples

### 6.1. User Entity

```java
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING;
    
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRole> roles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
    
    // Helper methods
    public boolean isSuperAdmin() {
        return roles.stream().anyMatch(r -> r.getRole() == Role.SUPER_ADMIN);
    }
    
    public boolean isBranchAdmin() {
        return roles.stream().anyMatch(r -> r.getRole() == Role.BRANCH_ADMIN);
    }
    
    public List<UUID> getManagedMemberIds() {
        return roles.stream()
            .filter(r -> r.getRole() == Role.BRANCH_ADMIN && r.getManagedMember() != null)
            .map(r -> r.getManagedMember().getId())
            .collect(Collectors.toList());
    }
}

public enum UserStatus {
    PENDING,
    ACTIVE,
    INACTIVE
}
```

### 6.2. UserRole Entity

```java
@Entity
@Table(name = "user_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managed_member_id")
    private Member managedMember;  // Chỉ cho BRANCH_ADMIN
    
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}

public enum Role {
    SUPER_ADMIN,
    BRANCH_ADMIN,
    USER
}
```

### 6.3. Member Entity

```java
@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "death_date")
    private LocalDate deathDate;
    
    @Column(name = "is_blood_relative", nullable = false)
    private Boolean isBloodRelative = true;
    
    @Column(name = "branch_name")
    private String branchName;
    
    private String address;
    private String phone;
    private String email;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    private Integer generation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "fromMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Relationship> relationshipsAsParent = new ArrayList<>();
    
    @OneToMany(mappedBy = "toMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Relationship> relationshipsAsChild = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
    
    public boolean isDeceased() {
        return deathDate != null;
    }
}

public enum Gender {
    MALE,
    FEMALE,
    OTHER
}
```

### 6.4. Relationship Entity

```java
@Entity
@Table(name = "relationships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relationship {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id", nullable = false)
    private Member fromMember;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id", nullable = false)
    private Member toMember;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private RelationshipType relationshipType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}

public enum RelationshipType {
    PARENT_CHILD,
    SPOUSE
}
```

## 7. Authorization Logic (Java Service)

```java
@Service
@RequiredArgsConstructor
public class AuthorizationService {
    
    private final UserRoleRepository userRoleRepository;
    private final RelationshipRepository relationshipRepository;
    
    /**
     * Kiểm tra user có quyền sửa member không
     */
    public boolean canEditMember(User user, UUID memberId) {
        // Kiểm tra SUPER_ADMIN
        if (user.isSuperAdmin()) {
            return true;
        }
        
        // Kiểm tra từng BRANCH_ADMIN role
        List<UUID> managedMemberIds = user.getManagedMemberIds();
        for (UUID managedMemberId : managedMemberIds) {
            Set<UUID> subtreeIds = getSubtreeIds(managedMemberId);
            if (subtreeIds.contains(memberId)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Kiểm tra user có quyền sửa/xóa relationship không
     */
    public boolean canEditRelationship(User user, Relationship relationship) {
        // SUPER_ADMIN có thể edit tất cả
        if (user.isSuperAdmin()) {
            return true;
        }
        
        // Kiểm tra từng BRANCH_ADMIN role
        List<UUID> managedMemberIds = user.getManagedMemberIds();
        for (UUID managedMemberId : managedMemberIds) {
            // Không được sửa quan hệ cha→managed_member (quan hệ với đời trên)
            if (relationship.getRelationshipType() == RelationshipType.PARENT_CHILD
                && relationship.getToMember().getId().equals(managedMemberId)) {
                continue; // Thử subtree khác
            }
            
            // Kiểm tra cả 2 member đều trong subtree
            Set<UUID> subtreeIds = getSubtreeIds(managedMemberId);
            if (subtreeIds.contains(relationship.getFromMember().getId())
                && subtreeIds.contains(relationship.getToMember().getId())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Lấy tất cả member IDs mà user có thể edit
     */
    public Set<UUID> getEditableMemberIds(User user) {
        Set<UUID> result = new HashSet<>();
        
        if (user.isSuperAdmin()) {
            // SUPER_ADMIN có thể edit tất cả - return empty để bypass check
            return null; // null = all members
        }
        
        // Gộp tất cả subtrees của BRANCH_ADMIN
        List<UUID> managedMemberIds = user.getManagedMemberIds();
        for (UUID managedMemberId : managedMemberIds) {
            result.addAll(getSubtreeIds(managedMemberId));
        }
        
        return result;
    }
    
    /**
     * Lấy tất cả member IDs trong subtree
     */
    private Set<UUID> getSubtreeIds(UUID rootMemberId) {
        Set<UUID> result = new HashSet<>();
        Queue<UUID> queue = new LinkedList<>();
        queue.add(rootMemberId);
        
        while (!queue.isEmpty()) {
            UUID currentId = queue.poll();
            if (result.contains(currentId)) continue;
            
            result.add(currentId);
            
            // Tìm con
            List<Relationship> childRelations = relationshipRepository
                .findByFromMemberIdAndRelationshipType(currentId, RelationshipType.PARENT_CHILD);
            for (Relationship r : childRelations) {
                queue.add(r.getToMember().getId());
            }
            
            // Tìm vợ/chồng
            List<Relationship> spouseRelations = relationshipRepository
                .findByFromMemberIdAndRelationshipType(currentId, RelationshipType.SPOUSE);
            for (Relationship r : spouseRelations) {
                queue.add(r.getToMember().getId());
            }
        }
        
        return result;
    }
}
```

## 8. Data Integrity Rules

### 8.1. Business Rules được enforce ở Database

| Rule | Implementation |
|------|----------------|
| Email unique | UNIQUE constraint on users.email |
| Gender values | CHECK constraint |
| Death after birth | CHECK constraint |
| No self-relation | CHECK constraint |
| Unique relationship | UNIQUE constraint |
| Relationship type values | CHECK constraint |
| Role values | CHECK constraint |
| BRANCH_ADMIN phải có managed_member_id | CHECK constraint |
| SUPER_ADMIN/USER không có managed_member_id | CHECK constraint |
| Unique user role per member | UNIQUE constraint |

### 8.2. Business Rules enforce ở Application

| Rule | Reason |
|------|--------|
| Không tạo vòng lặp cha-con | Cần recursive check |
| BRANCH_ADMIN không sửa quan hệ với đời trên | Authorization logic phức tạp |
| BRANCH_ADMIN chỉ edit member trong subtree(s) | Cần recursive query |
| User pending cần được approve | Workflow logic |
| Vợ/chồng tạo 2 chiều | Business logic |

## 9. Tổng kết thiết kế

### 9.1. So sánh các phiên bản thiết kế

| Aspect | v1 (branches) | v2 (managed_member_id) | v3 (user_roles) |
|--------|---------------|------------------------|-----------------|
| Bảng | users, branches, members | users, members | users, user_roles, members |
| Phân quyền | users.branch_id | users.managed_member_id | user_roles.managed_member_id |
| Nhiều subtrees/user | ❌ | ❌ | ✅ |
| Nhiều roles/user | ❌ | ❌ | ✅ |
| Tính linh hoạt | Thấp | Trung bình | Cao |
| Độ phức tạp | Đơn giản | Đơn giản | Trung bình |

### 9.2. Ưu điểm của thiết kế v3 (user_roles)

1. **Một user có thể quản lý nhiều subtree**: User A có thể là BRANCH_ADMIN của cả A2 và A4
2. **Dễ mở rộng**: Có thể thêm roles mới trong tương lai
3. **Audit tốt hơn**: Biết ai gán role, khi nào
4. **Linh hoạt**: Dễ dàng thêm/bỏ quyền mà không cần sửa user
5. **Chuẩn bị cho multi-tenant**: Có thể mở rộng thêm tenant_id sau này
