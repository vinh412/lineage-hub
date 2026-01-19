-- Migration V1: Initial schema for LineageHub

-- Members table (created first because user_roles references it)
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

-- Add FK for members.created_by (after users table is created)
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

-- Export history table (optional for Phase 1, but included for completeness)
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

-- Create Indexes for better query performance
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
