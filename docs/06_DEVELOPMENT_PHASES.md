# 📅 LineageHub - Development Phases

## Tổng quan các giai đoạn

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        DEVELOPMENT ROADMAP                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  PHASE 0          PHASE 1           PHASE 2          PHASE 3           │
│  [Setup]          [Core MVP]        [Tree & Export]  [Deploy & Polish] │
│                                                                         │
│  ┌─────┐          ┌─────────┐       ┌──────────┐     ┌───────────┐     │
│  │ 1w  │────────▶ │  4-6w   │─────▶ │  3-4w    │───▶ │  Ongoing  │     │
│  └─────┘          └─────────┘       └──────────┘     └───────────┘     │
│                                                                         │
│  - Project setup  - Auth            - Tree view      - DEPLOYMENT 🚀   │
│  - DB setup       - User mgmt       - Export PNG     - Audit logs      │
│  - Boilerplate    - Member CRUD     - Export PDF     - OAuth           │
│                   - Relationships   - Search         - Performance     │
│                   - Authorization                    - UI polish       │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Phase 0: Project Setup (1 tuần)

### Mục tiêu
Thiết lập môi trường phát triển, cấu trúc dự án, và các công cụ cần thiết.

### Tasks

#### 0.1. Backend Setup
| Task | Priority | Estimated |
|------|----------|-----------|
| Tạo Spring Boot project với dependencies | P0 | 2h |
| Cấu hình PostgreSQL connection | P0 | 1h |
| Setup Flyway migrations | P0 | 2h |
| Chạy migration V1 (init schema) | P0 | 1h |
| Cấu hình CORS cho localhost | P0 | 1h |
| Setup Swagger/OpenAPI | P1 | 2h |
| Cấu hình logging | P1 | 1h |

#### 0.2. Frontend Setup
| Task | Priority | Estimated |
|------|----------|-----------|
| Tạo Next.js project với TypeScript | P0 | 1h |
| Cài đặt và cấu hình Tailwind CSS | P0 | 1h |
| Setup folder structure | P0 | 1h |
| Cài đặt core dependencies | P0 | 1h |
| Setup Axios với base config | P0 | 2h |
| Setup React Query | P0 | 2h |
| Tạo base UI components | P1 | 4h |

#### 0.3. Development Environment
| Task | Priority | Estimated |
|------|----------|-----------|
| Viết README với hướng dẫn setup | P0 | 2h |
| Tạo docker-compose cho PostgreSQL | P1 | 1h |
| Setup ESLint + Prettier | P1 | 1h |

### Deliverables
- [ ] Backend chạy được ở `localhost:8080`
- [ ] Frontend chạy được ở `localhost:3000`
- [ ] Database connected, schema created
- [ ] API test endpoint hoạt động: `GET /api/health`

### Definition of Done
```
✅ Backend: mvn spring-boot:run → http://localhost:8080/swagger-ui.html accessible
✅ Frontend: npm run dev → http://localhost:3000 shows landing page
✅ Database: Tables created via Flyway migrations
```

---

## Phase 1: Core MVP (4-6 tuần)

### Mục tiêu
Hoàn thành tính năng Authentication, phân quyền, và quản lý thành viên để có thể bắt đầu thu thập dữ liệu.

### Sprint 1.1: Authentication (1 tuần)

#### Backend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| Implement User entity + repository | P0 | 2h |
| Implement JWT token generation | P0 | 4h |
| Implement login endpoint | P0 | 4h |
| Implement register endpoint | P0 | 4h |
| Implement JWT filter | P0 | 4h |
| Implement /auth/me endpoint | P0 | 2h |
| Password encoding (BCrypt) | P0 | 1h |
| Seed Super Admin user | P0 | 1h |

#### Frontend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| Tạo Login page UI | P0 | 4h |
| Tạo Register page UI | P0 | 4h |
| Implement auth API calls | P0 | 2h |
| Implement auth context/store | P0 | 4h |
| Protected route HOC/middleware | P0 | 4h |
| Token storage (localStorage/cookie) | P0 | 2h |
| Auto logout on token expiry | P1 | 2h |

#### Deliverables
- [ ] User có thể đăng ký tài khoản
- [ ] User có thể đăng nhập và nhận JWT
- [ ] Protected routes redirect to login nếu chưa auth
- [ ] Super Admin account hoạt động

### Sprint 1.2: User Management (1 tuần)

#### Backend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| GET /api/users (paginated, filtered) | P0 | 4h |
| PATCH /api/users/{id}/approve | P0 | 2h |
| PATCH /api/users/{id}/role | P0 | 2h |
| PATCH /api/users/{id}/deactivate | P0 | 2h |
| DELETE /api/users/{id} | P1 | 2h |
| Authorization checks (Super Admin only) | P0 | 4h |

#### Frontend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| User list page với DataTable | P0 | 6h |
| User approval flow | P0 | 4h |
| Role assignment dialog | P0 | 4h |
| User status toggle | P0 | 2h |
| Filter/search users | P1 | 4h |

#### Deliverables
- [ ] Super Admin có thể xem danh sách users
- [ ] Super Admin có thể approve pending users
- [ ] Super Admin có thể gán role cho user

### Sprint 1.3: Authorization Service (1 tuần)

#### Backend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| Implement AuthorizationService | P0 | 4h |
| canEditMember(user, memberId) method | P0 | 4h |
| canEditRelationship(user, relationshipId) method | P0 | 4h |
| getSubtreeIds(managedMemberId) recursive query | P0 | 6h |
| Integrate authorization check vào MemberService | P0 | 4h |
| Integrate authorization check vào RelationshipService | P0 | 4h |

#### Frontend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| canEdit flag hiển thị trên member list/detail | P0 | 2h |
| Disable edit buttons khi không có quyền | P0 | 2h |
| Hiển thị thông báo quyền hạn cho user | P1 | 2h |

#### Deliverables
- [ ] BRANCH_ADMIN chỉ edit được subtree của managed_member
- [ ] BRANCH_ADMIN không xóa được quan hệ với đời trên
- [ ] UI hiển thị rõ member nào có thể edit

### Sprint 1.4: Member CRUD (2 tuần)

#### Backend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| Implement Member entity + repository | P0 | 4h |
| GET /api/members (paginated, filtered) | P0 | 4h |
| GET /api/members/{id} | P0 | 2h |
| POST /api/members | P0 | 4h |
| PUT /api/members/{id} | P0 | 4h |
| DELETE /api/members/{id} | P0 | 2h |
| POST /api/members/{id}/avatar | P0 | 4h |
| File upload handling | P0 | 4h |
| Authorization: check subtree scope | P0 | 4h |
| Validation rules | P0 | 4h |

#### Frontend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| Member list page với filters | P0 | 8h |
| Member detail page | P0 | 6h |
| Create member form | P0 | 6h |
| Edit member form | P0 | 4h |
| Avatar upload component | P0 | 4h |
| Delete confirmation dialog | P0 | 2h |
| Form validation với Zod | P0 | 4h |
| Responsive design | P1 | 4h |

#### Deliverables
- [ ] Xem danh sách members với pagination/filter
- [ ] Tạo member mới với validation
- [ ] Edit member information
- [ ] Upload avatar cho member
- [ ] Delete member (nếu không có quan hệ)

### Sprint 1.5: Relationships (1 tuần)

#### Backend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| Implement Relationship entity | P0 | 2h |
| POST /api/relationships/parent-child | P0 | 4h |
| POST /api/relationships/spouse | P0 | 4h |
| DELETE /api/relationships/{id} | P0 | 2h |
| GET /api/members/{id}/relationships | P0 | 4h |
| Cycle detection algorithm | P0 | 6h |
| Auto-calculate generation | P1 | 4h |

#### Frontend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| Add parent dialog | P0 | 4h |
| Add spouse dialog | P0 | 4h |
| Add child dialog | P0 | 4h |
| Relationship display on member detail | P0 | 4h |
| Remove relationship button | P0 | 2h |
| Member search/select component | P0 | 4h |

#### Deliverables
- [ ] Thiết lập quan hệ cha-con
- [ ] Thiết lập quan hệ vợ-chồng
- [ ] Xem quan hệ trên member detail
- [ ] Xóa quan hệ
- [ ] Hệ thống detect và block vòng lặp

### Phase 1 Completion Criteria
```
✅ User có thể đăng ký, chờ approve, đăng nhập
✅ Super Admin có thể quản lý users và branches
✅ Branch Admin có thể CRUD members trong branch
✅ Relationships được thiết lập đúng
✅ Không có vòng lặp quan hệ cha-con
✅ Data validation hoạt động đầy đủ
```

---

## Phase 2: Tree Visualization & Export (3-4 tuần)

### Mục tiêu
Hiển thị sơ đồ cây gia phả interactive và export ra file.

### Sprint 2.1: Tree Visualization (2 tuần)

#### Backend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| GET /api/tree (nodes + edges format) | P0 | 8h |
| Recursive CTE for descendants | P0 | 4h |
| Filter by branch | P0 | 2h |
| Filter by root member | P0 | 2h |
| Performance optimization | P1 | 4h |

#### Frontend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| Setup React Flow | P0 | 4h |
| Custom MemberNode component | P0 | 8h |
| Custom edge styles | P0 | 4h |
| Dagre layout algorithm | P0 | 6h |
| Zoom/Pan controls | P0 | 4h |
| Mini map | P1 | 2h |
| Click node → member detail | P0 | 4h |
| Branch filter UI | P0 | 4h |
| Search member in tree | P0 | 6h |
| Highlight selected path | P1 | 4h |
| Mobile responsive tree | P1 | 6h |

#### Deliverables
- [ ] Sơ đồ cây gia phả hiển thị đầy đủ
- [ ] Zoom, pan, navigate tree
- [ ] Click vào node để xem detail
- [ ] Filter theo branch
- [ ] Tìm kiếm member trong tree

### Sprint 2.2: Export Feature (1-2 tuần)

#### Backend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| POST /api/export/tree | P0 | 2h |
| Generate export job | P0 | 4h |
| GET /api/export/download/{file} | P0 | 2h |
| Export history storage | P1 | 4h |

#### Frontend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| html-to-image integration | P0 | 6h |
| Export to PNG | P0 | 4h |
| jsPDF integration | P0 | 4h |
| Export to PDF | P0 | 6h |
| Export options dialog | P0 | 4h |
| Paper size selection | P0 | 2h |
| Loading state during export | P0 | 2h |
| Download progress | P1 | 2h |

#### Deliverables
- [ ] Export cây gia phả ra PNG
- [ ] Export cây gia phả ra PDF
- [ ] Tùy chọn khổ giấy (A0, A1, A3)
- [ ] Tùy chọn hiển thị (ảnh, năm sinh, etc.)

### Sprint 2.3: Search & Filter Enhancement (0.5 tuần)

#### Backend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| Full-text search implementation | P0 | 4h |
| GET /api/search/members | P0 | 4h |
| Search by relationship | P1 | 4h |

#### Frontend Tasks
| Task | Priority | Estimated |
|------|----------|-----------|
| Global search component | P0 | 4h |
| Search results page | P0 | 4h |
| Advanced filter dialog | P1 | 4h |

### Phase 2 Completion Criteria
```
✅ Sơ đồ cây hiển thị chính xác quan hệ
✅ Tree có thể zoom, pan, search
✅ Export PNG hoạt động với chất lượng cao
✅ Export PDF với các khổ giấy
✅ Tìm kiếm thành viên nhanh chóng
```

---

## Phase 3: Enhancement & Polish (Ongoing)

### Mục tiêu
Cải thiện UX, performance, và chuẩn bị cho tương lai.

### 3.1. Audit Logs (1 tuần)
| Task | Priority |
|------|----------|
| Implement audit log service | P0 |
| Auto-log all entity changes | P0 |
| Audit log viewer UI | P0 |
| Filter logs by entity/user/date | P1 |

### 3.2. OAuth Integration (1 tuần)
| Task | Priority |
|------|----------|
| Google OAuth setup | P1 |
| Facebook OAuth setup | P2 |
| Link social account to existing | P2 |

### 3.3. Production Deployment (1-2 tuần)
| Task | Priority |
|------|----------|
| Dockerize Frontend + Backend | P0 |
| Setup docker-compose.prod.yml | P0 |
| Configure Nginx reverse proxy | P0 |
| Setup SSL/HTTPS (Let's Encrypt) | P0 |
| Configure production environment variables | P0 |
| Setup database backups | P1 |
| Configure monitoring/logging | P1 |
| Deploy to VPS/Cloud | P0 |

> 📚 Chi tiết cấu hình xem tại `TECH_STACK.md` Section 9

### 3.4. Performance Optimization (1 tuần)
| Task | Priority |
|------|----------|
| Database query optimization | P1 |
| Caching layer (Redis) | P2 |
| Image optimization | P1 |
| Lazy loading | P1 |

### 3.5. UI/UX Polish (Ongoing)
| Task | Priority |
|------|----------|
| Loading skeletons | P1 |
| Error boundaries | P1 |
| Toast notifications | P1 |
| Keyboard shortcuts | P2 |
| Dark mode | P2 |

---

## Future Phases (Out of MVP Scope)

### Phase 4: Multi-tenancy
- Database per tenant hoặc tenant_id column
- Tenant registration flow
- Billing integration

### Phase 5: Mobile App
- React Native setup
- Shared TypeScript types
- Mobile-optimized tree view

### Phase 6: AI Features
- Natural language queries
- Relationship pathfinding
- Smart suggestions

### Phase 7: Event Reminders
- Lunar calendar integration
- Push notifications
- Email reminders

---

## Development Guidelines

### Priority Levels
| Level | Meaning |
|-------|---------|
| P0 | Must have - Blocking release |
| P1 | Should have - Important but not blocking |
| P2 | Nice to have - Can be deferred |

### Estimation Guidelines
- Tasks > 8h should be broken down
- Add 20% buffer for unknowns
- Include testing time in estimates

### Testing Requirements
- Unit tests cho business logic
- Integration tests cho API endpoints
- E2E tests cho critical flows (auth, member CRUD)

### Code Review Checklist
- [ ] Code follows conventions
- [ ] Tests written and passing
- [ ] No security vulnerabilities
- [ ] Performance considered
- [ ] Error handling implemented
