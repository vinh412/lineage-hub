# 📋 LineageHub - Tổng quan dự án

## 1. Giới thiệu

**LineageHub** là ứng dụng quản lý gia phả dòng họ, cho phép:
- Quản lý tập trung thông tin thành viên trong dòng họ
- Phân quyền theo nhánh gia đình
- Tự động xuất sơ đồ gia phả dạng cây

## 2. Mục tiêu chính

| Mục tiêu | Mô tả |
|----------|-------|
| **Lưu trữ** | Bảo tồn thông tin dòng họ một cách có hệ thống |
| **Phân quyền** | Cho phép nhiều người cùng xây dựng dữ liệu với quyền hạn phù hợp |
| **Xuất gia phả** | Export sơ đồ cây nhanh chóng, không cần làm mới từ đầu |
| **Mở rộng** | Kiến trúc linh hoạt để scale khi quy mô họ lớn hơn |

## 3. Đối tượng người dùng

### 3.1. Super Admin
- Toàn quyền quản lý hệ thống
- Quản lý tài khoản người dùng
- Cấu hình quyền và bảo mật

### 3.2. Branch Admin
- Được gán quản lý **subtree** từ một thành viên cụ thể (managed_member)
- Quản lý thông tin thành viên trong subtree (con cháu + vợ/chồng)
- Thiết lập quan hệ gia đình trong subtree
- **Không thể** sửa quan hệ của managed_member với đời trên (cha, mẹ)

### 3.3. User (Thành viên thường)
- Xem toàn bộ sơ đồ gia phả
- Xem thông tin chi tiết thành viên
- Tìm kiếm và lọc thông tin

## 4. Quy mô dự kiến

| Metric | Giá trị |
|--------|---------|
| Số thành viên gia phả | ~200 |
| Số nhánh gia đình | 5-10 |
| Số người dùng hệ thống | 10-30 |

## 5. Phạm vi dự án

### 5.1. Trong phạm vi (In Scope)
- ✅ Quản lý người dùng và phân quyền (RBAC)
- ✅ Quản lý thông tin thành viên gia phả
- ✅ Quản lý quan hệ gia đình (cha-con, vợ-chồng)
- ✅ Hiển thị sơ đồ cây gia phả
- ✅ Export gia phả (PNG, PDF)
- ✅ Tìm kiếm và lọc thông tin
- ✅ Lịch sử chỉnh sửa (Audit log)

### 5.2. Ngoài phạm vi MVP (Out of Scope - Tương lai)
- ❌ Mobile App (React Native)
- ❌ Multi-tenant/SaaS
- ❌ AI hỏi đáp quan hệ
- ❌ Nhắc nhở sự kiện (giỗ, sinh nhật)
- ❌ Tích hợp gọi điện Zalo/SIM
- ❌ Đa ngôn ngữ (i18n)

## 6. Ràng buộc và giả định

### 6.1. Ràng buộc
- Authentication phải thực hiện ở backend (không để FE)
- Database phải hỗ trợ quan hệ phức tạp
- Ứng dụng sẽ được **public trên internet** (không chỉ local)

### 6.2. Deployment Options
| Option | Mô tả |
|--------|-------|
| **Self-hosted** | Deploy trên VPS/Server riêng (DigitalOcean, Linode, own server) |
| **Cloud** | Deploy trên cloud platform (AWS, GCP, Azure, Vercel + Railway) |

### 6.3. Giả định
- Dữ liệu thành viên được nhập thủ công (không import từ nguồn khác)
- Cần HTTPS cho production (bảo mật dữ liệu gia đình)

## 7. Định nghĩa thuật ngữ

| Thuật ngữ | Định nghĩa |
|-----------|------------|
| **Thành viên (Member)** | Một người trong dòng họ, có thể còn sống hoặc đã mất |
| **Nhánh/Subtree** | Một chi nhánh gồm member gốc + tất cả con cháu + vợ/chồng |
| **Quan hệ (Relationship)** | Mối liên hệ giữa các thành viên (cha-con, vợ-chồng) |
| **Đời (Generation)** | Thứ bậc tính từ tổ tiên chung (đời 1, đời 2, ...) |
| **Gia phả (Family Tree)** | Sơ đồ cây thể hiện quan hệ huyết thống |

## 8. Tài liệu liên quan

- [Yêu cầu nghiệp vụ](./00_BUSINESS_REQUIREMENTS.md)
- [Kiến trúc hệ thống](./02_ARCHITECTURE.md)
- [Tech Stack](./03_TECH_STACK.md)
- [Database Schema](./04_DATABASE_SCHEMA.md)
- [API Design](./05_API_DESIGN.md)
- [Giai đoạn phát triển](./06_DEVELOPMENT_PHASES.md)
- [Coding Conventions](./07_CODING_CONVENTIONS.md)
- [Hướng dẫn Agent](./08_AGENT_GUIDELINES.md)
