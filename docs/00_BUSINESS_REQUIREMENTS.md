# LineageHub

Dưới đây là **mô tả nghiệp vụ chi tiết (Business Requirements)** cho LineageHub (ứng dụng quản lý gia phả).

---

# 📌 1. Giới thiệu ứng dụng

Ứng dụng giúp **dòng họ quản lý tập trung thông tin thành viên**, phân quyền theo nhánh gia đình và **tự động xuất sơ đồ gia phả** dạng cây.

Đối tượng sử dụng:

- Quản trị cấp cao (Super Admin)
- Đại diện nhánh họ (Branch Admin)
- Thành viên thông thường

---

# 👑 2. Nghiệp vụ Quản trị hệ thống

## 2.1. Quản lý người dùng

- Super Admin có thể:
    - Tạo tài khoản người dùng
    - Duyệt tài khoản người dùng
    - Gán vai trò *Branch Admin* / *User*
    - Vô hiệu hóa hoặc xóa tài khoản
- Người dùng tạo tài khoản xong phải được admin phê duyệt.

## 2.2. Quản lý vai trò (RBAC)

- Vai trò chính:
    - **Super Admin**: toàn quyền
    - **Branch Admin**: chỉnh sửa dữ liệu trong nhánh được giao
    - **User**: chỉ xem thông tin
- Mỗi người dùng có thể xem toàn bộ họ

## 2.3. Theo dõi lịch sử chỉnh sửa

- Hệ thống lưu lại:
    - ai chỉnh sửa
    - chỉnh sửa cái gì
    - thời điểm chỉnh sửa

---

# 🧩 3. Nghiệp vụ về quản lý thông tin thành viên

## 3.1. Tạo – Cập nhật – Xóa thành viên

- Branch Admin có thể:
    - Thêm thông tin thành viên mới trong nhánh của mình
    - Cập nhật thông tin cá nhân:
        - Họ tên
        - Ngày tháng năm sinh
        - Ngày mất (nếu có)
        - Giới tính
        - Địa chỉ
        - Ảnh đại diện
        - Số điện thoại (nếu có)
        - Email (nếu có)
        - Ghi chú khác
    - Xóa thành viên **nếu chưa có ràng buộc quan hệ**
- Super Admin có thể chỉnh sửa *mọi thành viên*

## 3.2. Quản lý quan hệ gia đình

- Người dùng có thể thiết lập:
    - Cha → Con (quan hệ 1-n)
    - Chồng ↔ Vợ (quan hệ song phương)
    - Anh chị em được suy ra **tự động từ quan hệ cha mẹ**
- Hệ thống đảm bảo:
    - Không có vòng lặp quan hệ (cha không thể là con của con)
    - Một thành viên có thể có nhiều vợ/chồng (theo lịch sử)

# 🌳 4. Nghiệp vụ xem và duyệt gia phả

## 4.1. Xem toàn bộ sơ đồ

- Người dùng thường:
    - Có thể xem toàn bộ sơ đồ gia phả
    - Xem theo dạng cây gốc (tổ tiên chung)
    - Thu phóng, kéo thả, tìm kiếm trên cây

## 4.2. Xem theo nhánh gia đình

- Người dùng có thể:
    - Lọc theo nhánh để xem riêng
    - Xem danh sách thành viên theo họ, tổ tiên, đời thứ X

## 4.3. Xem thông tin chi tiết

- Click vào thành viên để mở profile:
    - Thông tin cơ bản
    - Quan hệ cha mẹ – vợ/chồng – con cái
    - Hình ảnh
    - Lịch sử thay đổi

---

# 🖨️ 5. Nghiệp vụ xuất gia phả (Export)

## 5.1. Xuất hình ảnh

- Người dùng chọn định dạng:
    - PNG
    - PDF
- Export bao gồm:
    - Sơ đồ cây
    - Ảnh đại diện
    - Tên
    - Năm sinh (có thể ẩn thông tin nhạy cảm nếu cần)
- Kích thước xuất tùy chọn:
    - Dài vô hạn theo chiều ngang
    - Khổ A0/A1/A3 cho in poster

## 5.2. Tự động cập nhật

- Khi thông tin thay đổi:
    - Không cần cập nhật lại file cũ
    - Chỉ cần export lại là có bản mới
- Hệ thống cho phép lưu bản export lịch sử theo **năm** để tham khảo

---

# 🔒 6. Nghiệp vụ bảo mật và phân quyền truy cập

- Thông tin cá nhân chỉ xem khi đã đăng nhập
- Super Admin có thể:
    - Cài đặt chế độ ẩn/hiện dữ liệu nhạy cảm
    - Cấu hình quyền ai nhìn thấy gì

---

# 🌐 7. Tìm kiếm & lọc thông tin

- Tìm theo:
    - Tên / biệt hiệu
    - Năm sinh
    - Nhánh gia đình
    - Quan hệ (con của, cháu của)
- Tính năng nâng cao:
    - Hiển thị “đời thứ X tính từ tổ tiên chung”

---

# 🌱 8. Nghiệp vụ mở rộng tương lai

### 8.1 Nhắc nhở sự kiện

- Giỗ tổ tiên theo lịch âm/dương
- Sinh nhật thành viên

### 8.2 AI hỏi đáp thông tin quan hệ

- Người dùng đặt câu hỏi:
    - “Ai là cụ tổ đời thứ 4?”
    - “Minh là cháu đời mấy của ông A?”
- AI trả ra câu trả lời và đường dẫn đến profile

### 8.3 Gọi điện/xem danh bạ theo quan hệ sử dụng AI

- Người dùng nói "Gọi cho cháu xxx" qua SIM/Zalo:
    - Tự động gọi cho thành viên xxx qua SIM/Zalo (nếu thành viên xxx có lưu thông tin số điện thoại/zalo)

---

# 🧱 9. Quy định dữ liệu buộc phải có

- Mỗi thành viên **bắt buộc**:
    - Họ tên
    - Giới tính
- Dữ liệu **khuyến khích**:
    - Ngày sinh
    - Ảnh
    - Quan hệ cha mẹ

---

# 🎉 Tổng kết

Ứng dụng hướng đến:

- Lưu trữ và bảo tồn thông tin dòng họ
- Phân quyền thích hợp để mọi người cùng xây dựng dữ liệu
- Xuất gia phả nhanh gọn sau nhiều năm không làm mới từ đầu
- Mở rộng dễ dàng khi quy mô họ lớn hơn