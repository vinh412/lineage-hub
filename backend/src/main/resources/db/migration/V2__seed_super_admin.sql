-- Migration V2: Seed Super Admin user
-- Password: Admin@123 (BCrypt hash with strength 10)
-- Use this to generate new hash: BCryptPasswordEncoder().encode("yourpassword")

-- Create super admin user
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
