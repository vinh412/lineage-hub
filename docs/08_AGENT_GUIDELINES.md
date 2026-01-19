# 🤖 LineageHub - Agent Guidelines

## Mục đích tài liệu

Tài liệu này hướng dẫn AI Agent cách phát triển ứng dụng LineageHub một cách nhất quán, đúng yêu cầu, và không bị lạc đề.

---

## 1. Tổng quan dự án

### 1.1. LineageHub là gì?
- Ứng dụng quản lý gia phả dòng họ
- Cho phép nhiều người cùng xây dựng dữ liệu thành viên
- Hiển thị sơ đồ cây gia phả và export ra file

### 1.2. Tech Stack (KHÔNG thay đổi)

| Component | Technology |
|-----------|------------|
| Frontend | **Next.js 14** (App Router) + TypeScript |
| Backend | **Spring Boot 3.5.9** + Java 21 |
| Database | **PostgreSQL 16** |
| ORM | JPA/Hibernate |
| Auth | JWT + Spring Security |
| Deployment | **Self-hosted** (VPS) hoặc **Cloud** (Vercel + Railway) |

> ⚠️ **QUAN TRỌNG**: Không đề xuất thay đổi tech stack. Nếu cần thư viện mới, hãy giải thích lý do.

### 1.3. Deployment Info

- Ứng dụng sẽ được **public trên internet** (không chỉ local)
- Cần hỗ trợ HTTPS cho production
- Xem chi tiết deployment tại `03_TECH_STACK.md` và `02_ARCHITECTURE.md`

---

## 2. Nguyên tắc phát triển

### 2.1. Đọc tài liệu trước khi code

Trước khi implement bất kỳ feature nào, PHẢI đọc các file sau:

| File | Nội dung |
|------|----------|
| `docs/00_BUSINESS_REQUIREMENTS.md` | Yêu cầu nghiệp vụ gốc |
| `docs/01_PROJECT_OVERVIEW.md` | Tổng quan và scope |
| `docs/02_ARCHITECTURE.md` | Kiến trúc hệ thống |
| `docs/03_TECH_STACK.md` | Công nghệ sử dụng |
| `docs/04_DATABASE_SCHEMA.md` | Thiết kế database |
| `docs/05_API_DESIGN.md` | Thiết kế API |
| `docs/06_DEVELOPMENT_PHASES.md` | Thứ tự ưu tiên |
| `docs/07_CODING_CONVENTIONS.md` | Quy ước code |

### 2.2. Thứ tự ưu tiên phát triển

```
Phase 0: Setup ──▶ Phase 1: Core MVP ──▶ Phase 2: Tree & Export ──▶ Phase 3: Enhancement
```

**Phase 1 (Ưu tiên cao nhất):**
1. Authentication (đăng nhập, đăng ký, JWT)
2. User Management (quản lý tài khoản, gán roles)
3. User Roles Management (gán nhiều roles cho user)
4. Member CRUD (thêm/sửa/xóa thành viên)
5. Relationships (quan hệ gia đình)
6. Authorization Service (kiểm tra quyền theo subtrees)

**Phase 2 (Sau khi hoàn thành Phase 1):**
1. Tree Visualization
2. Export PNG/PDF
3. Search Enhancement

> ⚠️ **KHÔNG** implement các tính năng Phase 3 (OAuth, Multi-tenant, AI, Mobile) cho đến khi được yêu cầu.

### 2.3. Nguyên tắc KHÔNG làm

❌ **KHÔNG** thêm tính năng ngoài scope:
- Không thêm OAuth nếu chưa yêu cầu
- Không thêm multi-language
- Không thêm AI features
- Không thêm notification system

❌ **KHÔNG** thay đổi kiến trúc:
- Không chuyển từ REST sang GraphQL
- Không thêm message queue
- Không thêm caching layer (Redis) khi chưa cần

❌ **KHÔNG** over-engineer:
- Không tạo abstraction không cần thiết
- Không optimize premature
- Giữ code đơn giản, dễ đọc

---

## 3. Hướng dẫn Backend

### 3.1. Cấu trúc thư mục

```
backend/src/main/java/com/lineagehub/
├── config/           # Cấu hình (Security, CORS, JWT)
├── controller/       # REST Controllers
│   ├── AuthController.java
│   ├── UserController.java
│   ├── UserRoleController.java   # Quản lý roles
│   ├── MemberController.java
│   └── ...
├── service/          # Business logic (interface + impl)
│   ├── AuthorizationService.java # Kiểm tra quyền subtrees
│   ├── UserRoleService.java      # Quản lý roles
│   └── ...
├── repository/       # JPA Repositories
│   ├── UserRoleRepository.java
│   └── ...
├── entity/           # JPA Entities
│   ├── User.java
│   ├── UserRole.java             # Bảng user_roles
│   ├── Member.java
│   └── ...
├── dto/
│   ├── request/      # Input DTOs
│   └── response/     # Output DTOs
├── mapper/           # MapStruct mappers
├── exception/        # Custom exceptions + Handler
└── security/         # JWT, UserDetails
```

### 3.2. Quy tắc Controller

```java
// ✅ ĐÚNG: Controller chỉ handle HTTP, delegate logic cho Service
@PostMapping
public ResponseEntity<MemberResponse> create(@Valid @RequestBody CreateMemberRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(memberService.createMember(request));
}

// ❌ SAI: Không đặt business logic trong Controller
@PostMapping
public ResponseEntity<MemberResponse> create(@RequestBody CreateMemberRequest request) {
    // KHÔNG validate ở đây
    // KHÔNG gọi repository trực tiếp
    // KHÔNG có if/else business logic
}
```

### 3.3. Quy tắc Service

```java
// ✅ ĐÚNG: Service chứa business logic
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    
    @Override
    @Transactional
    public MemberResponse createMember(CreateMemberRequest request) {
        // 1. Validate business rules
        authorizationService.checkCanEditMember(request.getParentIds());
        
        // 2. Map DTO to Entity
        Member member = memberMapper.toEntity(request);
        
        // 3. Save
        Member saved = memberRepository.save(member);
        
        // 4. Audit log
        auditService.log(EntityType.MEMBER, saved.getId(), Action.CREATE);
        
        // 5. Return response
        return memberMapper.toResponse(saved);
    }
}
```

### 3.4. Quy tắc Entity

```java
// ✅ ĐÚNG: User Entity với OneToMany đến UserRole
@Entity
@Table(name = "users")
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    // One user có nhiều roles
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRole> roles = new ArrayList<>();
    
    // Helper methods
    public boolean isSuperAdmin() {
        return roles.stream().anyMatch(r -> r.getRole() == Role.SUPER_ADMIN);
    }
    
    public List<UUID> getManagedMemberIds() {
        return roles.stream()
            .filter(r -> r.getRole() == Role.BRANCH_ADMIN && r.getManagedMember() != null)
            .map(r -> r.getManagedMember().getId())
            .collect(Collectors.toList());
    }
}

// ✅ ĐÚNG: UserRole Entity
@Entity
@Table(name = "user_roles")
@Getter @Setter
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
}
```

### 3.5. API Endpoints

Tuân thủ đúng thiết kế trong `05_API_DESIGN.md`:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/login` | POST | Đăng nhập |
| `/api/auth/register` | POST | Đăng ký |
| `/api/users` | GET | Danh sách users |
| `/api/users/{id}/roles` | GET | Roles của user |
| `/api/users/{id}/roles` | POST | Thêm role cho user |
| `/api/users/{id}/roles/{roleId}` | DELETE | Xóa role |
| `/api/members` | GET | Danh sách thành viên |
| `/api/members/{id}` | GET | Chi tiết thành viên |
| `/api/members` | POST | Tạo thành viên |
| `/api/members/{id}` | PUT | Cập nhật thành viên |

---

## 4. Hướng dẫn Frontend

### 4.1. Cấu trúc thư mục

```
frontend/src/
├── app/                    # Pages (App Router)
│   ├── (auth)/            # Auth pages (login, register)
│   └── (dashboard)/       # Protected pages
│       └── users/
│           └── [id]/
│               └── roles/ # Quản lý roles của user
├── components/
│   ├── ui/                # Reusable UI (Button, Input, etc.)
│   └── features/          # Feature components
│       └── user-roles/    # Components cho role management
├── lib/
│   ├── api/               # API calls
│   ├── hooks/             # Custom hooks
│   └── types/             # TypeScript types
└── stores/                # Zustand stores
```

### 4.2. Component Structure

```tsx
// ✅ ĐÚNG: Functional component với hooks
export function MemberCard({ member }: { member: Member }) {
  const router = useRouter();
  
  const handleClick = () => {
    router.push(`/members/${member.id}`);
  };
  
  return (
    <div onClick={handleClick}>
      {member.fullName}
    </div>
  );
}

// ❌ SAI: Class component
class MemberCard extends React.Component { } // KHÔNG dùng
```

### 4.3. Data Fetching

```tsx
// ✅ ĐÚNG: Sử dụng React Query
function MemberList() {
  const { data, isLoading, error } = useQuery({
    queryKey: ['members'],
    queryFn: () => api.getMembers(),
  });
  
  if (isLoading) return <Skeleton />;
  if (error) return <Error message={error.message} />;
  
  return <DataTable data={data} />;
}

// ❌ SAI: useEffect + useState cho data fetching
function MemberList() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    fetch('/api/members')
      .then(res => res.json())
      .then(setData)
      .finally(() => setLoading(false));
  }, []); // KHÔNG làm như này
}
```

### 4.4. Form Handling

```tsx
// ✅ ĐÚNG: react-hook-form + zod
const schema = z.object({
  fullName: z.string().min(1, 'Bắt buộc'),
  gender: z.enum(['MALE', 'FEMALE']),
});

function MemberForm() {
  const form = useForm({
    resolver: zodResolver(schema),
  });
  
  return (
    <form onSubmit={form.handleSubmit(onSubmit)}>
      <Input {...form.register('fullName')} />
    </form>
  );
}
```

### 4.5. Styling

```tsx
// ✅ ĐÚNG: Tailwind CSS
<button className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
  Submit
</button>

// ❌ SAI: Inline styles
<button style={{ padding: '16px', backgroundColor: 'blue' }}>
  Submit
</button>

// ❌ SAI: CSS modules (trừ khi có lý do đặc biệt)
import styles from './Button.module.css';
```

---

## 5. Hướng dẫn Database

### 5.1. Migration

- Sử dụng Flyway cho database migrations
- Đặt tên file: `V{version}__{description}.sql`
- KHÔNG sửa migration đã chạy, tạo migration mới

```sql
-- V3__add_member_nickname.sql
ALTER TABLE members ADD COLUMN nickname VARCHAR(100);
```

### 5.2. Query Guidelines

```java
// ✅ ĐÚNG: Sử dụng JPA Repository methods
List<Member> findByGenderAndIsBloodRelative(Gender gender, Boolean isBloodRelative);

// ✅ ĐÚNG: JPQL cho search
@Query("SELECT m FROM Member m WHERE m.fullName LIKE %:search%")
Page<Member> searchByName(@Param("search") String search, Pageable pageable);

// ✅ ĐÚNG: Native query cho recursive CTE (lấy subtree)
@Query(value = """
    WITH RECURSIVE subtree AS (
        SELECT id FROM members WHERE id = :rootId
        UNION
        SELECT m.id FROM members m
        JOIN relationships r ON r.to_member_id = m.id
        JOIN subtree s ON r.from_member_id = s.id
    )
    SELECT * FROM members WHERE id IN (SELECT id FROM subtree)
    """, nativeQuery = true)
List<Member> findSubtree(@Param("rootId") UUID rootId);
```

---

## 6. Security Guidelines

### 6.1. Authentication Flow

```
1. User POST /api/auth/login với email + password
2. Backend validate và trả về JWT token
3. Frontend lưu token (localStorage hoặc cookie)
4. Mọi request kèm header: Authorization: Bearer {token}
5. Backend validate token ở mọi protected endpoint
```

### 6.2. Authorization Rules (với user_roles)

| Role | Permissions |
|------|-------------|
| SUPER_ADMIN | Tất cả (bao gồm quản lý roles) |
| BRANCH_ADMIN | CRUD members trong **các subtrees** được gán |
| USER | Chỉ xem (read-only) |

**BRANCH_ADMIN với nhiều managed_member_ids (A2 và A4):**
```
✅ CÓ THỂ:
- Xem toàn bộ gia phả
- Sửa A2, A4 và tất cả subtrees của chúng
- Thêm vợ/chồng cho A2, A4
- Thêm/sửa/xóa quan hệ trong subtrees

❌ KHÔNG THỂ:
- Sửa/xóa quan hệ A2, A4 với cha/mẹ (đời trên)
- Sửa member ngoài subtrees (A1, A3,...)
```

```java
// ✅ ĐÚNG: Sử dụng @PreAuthorize cho role check
@PreAuthorize("hasRole('SUPER_ADMIN')")
public void deleteUser(UUID id) { }

// ✅ ĐÚNG: Kiểm tra quyền theo subtrees trong service
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
public void updateMember(UUID id, UpdateRequest request) {
    // Phải gọi AuthorizationService.canEditMember(currentUser, id)
    if (!authorizationService.canEditMember(currentUser, id)) {
        throw new ForbiddenException("Không có quyền sửa member này");
    }
    // ... proceed with update
}
```

### 6.3. AuthorizationService Pattern

```java
@Service
@RequiredArgsConstructor
public class AuthorizationService {
    
    private final RelationshipRepository relationshipRepository;
    
    /**
     * Kiểm tra user có quyền sửa member không
     * Duyệt qua TẤT CẢ subtrees của user
     */
    public boolean canEditMember(User user, UUID memberId) {
        if (user.isSuperAdmin()) {
            return true;
        }
        
        // Lấy tất cả managed_member_ids từ user_roles
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
        if (user.isSuperAdmin()) {
            return true;
        }
        
        List<UUID> managedMemberIds = user.getManagedMemberIds();
        
        for (UUID managedMemberId : managedMemberIds) {
            // Không được sửa quan hệ cha→managed_member
            if (relationship.getRelationshipType() == RelationshipType.PARENT_CHILD
                && relationship.getToMember().getId().equals(managedMemberId)) {
                continue; // Thử subtree khác
            }
            
            Set<UUID> subtreeIds = getSubtreeIds(managedMemberId);
            if (subtreeIds.contains(relationship.getFromMember().getId())
                && subtreeIds.contains(relationship.getToMember().getId())) {
                return true;
            }
        }
        
        return false;
    }
    
    private Set<UUID> getSubtreeIds(UUID rootMemberId) {
        // Recursive query để lấy tất cả member IDs trong subtree
        // Xem 04_DATABASE_SCHEMA.md để biết chi tiết
    }
}
```

### 6.4. Input Validation

```java
// ✅ ĐÚNG: Validate ở DTO
public class CreateMemberRequest {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
    
    @NotNull
    private Gender gender;
    
    @Email(message = "Email không hợp lệ")
    private String email;
}

// ✅ ĐÚNG: Validate business rules ở Service
if (hasCircularRelationship(parentId, childId)) {
    throw new BusinessException("Không thể tạo quan hệ vòng lặp");
}
```

---

## 7. Error Handling

### 7.1. Backend Errors

```java
// Throw custom exceptions
throw new ResourceNotFoundException("Member", memberId);
throw new BusinessException("Không thể xóa thành viên có quan hệ");
throw new UnauthorizedException("Bạn không có quyền thực hiện");

// GlobalExceptionHandler sẽ convert thành response:
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Member với ID xxx không tồn tại"
}
```

### 7.2. Frontend Errors

```tsx
// ✅ ĐÚNG: Handle errors với React Query
const { data, error, isError } = useQuery({...});

if (isError) {
  return <ErrorMessage message={error.message} />;
}

// ✅ ĐÚNG: Toast cho mutations
const mutation = useMutation({
  onError: (error) => {
    toast.error(error.message);
  },
  onSuccess: () => {
    toast.success('Đã lưu thành công');
  },
});
```

---

## 8. Testing Guidelines

### 8.1. Backend Tests

```java
// Unit test cho Service
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    
    @InjectMocks
    private MemberServiceImpl memberService;
    
    @Test
    void createMember_Success() {
        // Arrange
        CreateMemberRequest request = ...;
        when(memberRepository.save(any())).thenReturn(member);
        
        // Act
        MemberResponse result = memberService.createMember(request);
        
        // Assert
        assertThat(result.getFullName()).isEqualTo("Test");
    }
}

// Integration test cho API
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void getMembers_ReturnsPagedResult() throws Exception {
        mockMvc.perform(get("/api/members"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }
}
```

### 8.2. Frontend Tests

```tsx
// Component test với Testing Library
describe('MemberCard', () => {
  it('displays member name', () => {
    render(<MemberCard member={mockMember} />);
    expect(screen.getByText('Nguyễn Văn A')).toBeInTheDocument();
  });
});
```

---

## 9. Checklist khi implement feature

### 9.1. Trước khi code

- [ ] Đọc yêu cầu trong `00_BUSINESS_REQUIREMENTS.md`
- [ ] Xem thiết kế API trong `05_API_DESIGN.md`
- [ ] Xem schema database trong `04_DATABASE_SCHEMA.md`
- [ ] Feature có trong scope Phase hiện tại không?

### 9.2. Backend checklist

- [ ] Entity đúng với schema (bao gồm UserRole)
- [ ] DTO có validation annotations
- [ ] Service có business logic
- [ ] AuthorizationService kiểm tra quyền theo subtrees
- [ ] Controller chỉ handle HTTP
- [ ] Exception được handle đúng
- [ ] Audit log được ghi
- [ ] Authorization được check

### 9.3. Frontend checklist

- [ ] Component đúng folder structure
- [ ] Sử dụng React Query cho data fetching
- [ ] Form dùng react-hook-form + zod
- [ ] Error handling với toast
- [ ] Loading states
- [ ] Responsive design
- [ ] TypeScript types đầy đủ (bao gồm UserRole types)

### 9.4. Sau khi code

- [ ] Test API với Swagger/Postman
- [ ] Test UI trên browser
- [ ] Không có console.log/print statements
- [ ] Không có hardcoded values
- [ ] Code follows conventions

---

## 10. Câu hỏi thường gặp

### Q: Khi nào cần tạo migration mới?
A: Khi thay đổi database schema (thêm/sửa/xóa column, table, index).

### Q: Có nên dùng lombok @Data cho Entity không?
A: Dùng @Getter @Setter thay vì @Data để tránh issues với equals/hashCode.

### Q: State management dùng gì?
A: Zustand cho global state, React Query cho server state.

### Q: Cần tối ưu performance không?
A: Chưa cần ở MVP. Focus vào correctness trước.

### Q: Có cần viết test không?
A: Có, nhưng focus vào critical paths (auth, member CRUD, authorization).

### Q: Một user có thể có nhiều roles không?
A: User có thể có **nhiều role BRANCH_ADMIN** với các `managed_member_id` khác nhau, nhưng chỉ **1 role SUPER_ADMIN** hoặc **1 role USER**.

### Q: Làm sao kiểm tra quyền cho BRANCH_ADMIN với nhiều subtrees?
A: `AuthorizationService` sẽ duyệt qua **tất cả** `managed_member_ids` từ `user_roles` và kiểm tra member có thuộc bất kỳ subtree nào không.

---

## 11. Liên hệ & Escalation

Khi gặp vấn đề không rõ:
1. Đọc lại tài liệu liên quan
2. Kiểm tra xem có trong scope không
3. Nếu không chắc chắn, HỎI user trước khi implement

> 💡 **Nguyên tắc vàng**: Khi không chắc chắn, hỏi thay vì đoán.
