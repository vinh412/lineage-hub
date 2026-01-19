# 📝 LineageHub - Coding Conventions

## 1. General Principles

### 1.1. Code Quality
- **Readability** > Cleverness
- **Consistency** > Personal preference
- **Simplicity** > Premature optimization
- **Explicit** > Implicit

### 1.2. Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Files/Folders | kebab-case | `member-list.tsx` |
| Components | PascalCase | `MemberCard` |
| Functions | camelCase | `getMemberById` |
| Constants | UPPER_SNAKE | `MAX_FILE_SIZE` |
| Types/Interfaces | PascalCase | `MemberResponse` |
| Database tables | snake_case | `audit_logs` |
| API endpoints | kebab-case | `/api/members/{id}/avatar` |

---

## 2. Frontend Conventions (Next.js + TypeScript)

### 2.1. File Structure

```
src/
├── app/                     # App Router pages
│   ├── (auth)/             # Route group (không ảnh hưởng URL)
│   │   └── login/
│   │       └── page.tsx    # /login
│   └── (dashboard)/
│       └── members/
│           ├── page.tsx    # /members (list)
│           └── [id]/
│               └── page.tsx # /members/:id (detail)
├── components/
│   ├── ui/                 # Primitive components
│   │   ├── button.tsx
│   │   ├── input.tsx
│   │   └── index.ts        # Barrel export
│   ├── forms/              # Form-specific components
│   └── features/           # Feature-specific components
│       └── members/
│           ├── member-card.tsx
│           └── member-form.tsx
├── lib/
│   ├── api/               # API client functions
│   │   ├── client.ts      # Axios instance
│   │   ├── members.ts     # Member API calls
│   │   └── auth.ts        # Auth API calls
│   ├── hooks/             # Custom hooks
│   ├── utils/             # Utility functions
│   └── types/             # TypeScript types
└── stores/                # Zustand stores
```

### 2.2. Component Structure

```tsx
// 1. Imports (grouped and ordered)
import { useState, useEffect } from 'react';           // React
import { useRouter } from 'next/navigation';          // Next.js
import { useQuery } from '@tanstack/react-query';     // Third-party
import { Button } from '@/components/ui';             // Internal UI
import { getMemberById } from '@/lib/api/members';    // Internal lib
import type { Member } from '@/lib/types';            // Types last

// 2. Types/Interfaces (if component-specific)
interface MemberCardProps {
  member: Member;
  onEdit?: (id: string) => void;
  className?: string;
}

// 3. Component definition
export function MemberCard({ member, onEdit, className }: MemberCardProps) {
  // 3.1. Hooks first
  const router = useRouter();
  const [isExpanded, setIsExpanded] = useState(false);

  // 3.2. Derived state / computations
  const fullName = `${member.firstName} ${member.lastName}`;
  const isDeceased = !!member.deathDate;

  // 3.3. Effects
  useEffect(() => {
    // Effect logic
  }, []);

  // 3.4. Event handlers
  const handleClick = () => {
    router.push(`/members/${member.id}`);
  };

  // 3.5. Render
  return (
    <div className={cn('rounded-lg border p-4', className)}>
      {/* JSX content */}
    </div>
  );
}
```

### 2.3. TypeScript Rules

```typescript
// ✅ DO: Use explicit types for function parameters and returns
function calculateAge(birthDate: Date): number {
  // ...
}

// ✅ DO: Use interfaces for objects, types for unions/primitives
interface Member {
  id: string;
  fullName: string;
  gender: Gender;
}

type Gender = 'MALE' | 'FEMALE' | 'OTHER';

// ✅ DO: Use const assertions for literals
const ROLES = ['SUPER_ADMIN', 'BRANCH_ADMIN', 'USER'] as const;
type Role = typeof ROLES[number];

// ❌ DON'T: Use `any`
function badFunction(data: any) {} // NO!

// ✅ DO: Use `unknown` if type is truly unknown
function safeFunction(data: unknown) {
  if (typeof data === 'string') {
    // Now TypeScript knows it's a string
  }
}

// ❌ DON'T: Use non-null assertion (!) unless absolutely sure
const value = maybeNull!.property; // NO!

// ✅ DO: Use optional chaining and nullish coalescing
const value = maybeNull?.property ?? defaultValue;
```

### 2.4. React Query Conventions

```typescript
// lib/api/members.ts
export async function getMembers(params: GetMembersParams): Promise<MembersResponse> {
  const response = await apiClient.get('/members', { params });
  return response.data;
}

export async function getMemberById(id: string): Promise<Member> {
  const response = await apiClient.get(`/members/${id}`);
  return response.data;
}

// hooks/use-members.ts
export function useMembers(params: GetMembersParams) {
  return useQuery({
    queryKey: ['members', params],
    queryFn: () => getMembers(params),
  });
}

export function useMember(id: string) {
  return useQuery({
    queryKey: ['members', id],
    queryFn: () => getMemberById(id),
    enabled: !!id,
  });
}

// Usage in component
function MemberList() {
  const { data, isLoading, error } = useMembers({ page: 0, size: 20 });
  
  if (isLoading) return <Skeleton />;
  if (error) return <ErrorMessage error={error} />;
  
  return <DataTable data={data.content} />;
}
```

### 2.5. Styling with Tailwind

```tsx
// ✅ DO: Use cn() utility for conditional classes
import { cn } from '@/lib/utils';

function Button({ variant, className, ...props }) {
  return (
    <button
      className={cn(
        // Base styles
        'px-4 py-2 rounded-md font-medium transition-colors',
        // Variant styles
        variant === 'primary' && 'bg-blue-600 text-white hover:bg-blue-700',
        variant === 'secondary' && 'bg-gray-200 text-gray-800 hover:bg-gray-300',
        // Custom className
        className
      )}
      {...props}
    />
  );
}

// ✅ DO: Extract repeated patterns into components
// ❌ DON'T: Repeat long className strings

// ✅ DO: Use design tokens via CSS variables
// tailwind.config.ts
{
  theme: {
    extend: {
      colors: {
        primary: 'hsl(var(--primary))',
        secondary: 'hsl(var(--secondary))',
      }
    }
  }
}
```

### 2.6. Form Handling

```tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

// Define schema
const memberSchema = z.object({
  fullName: z.string().min(1, 'Họ tên không được để trống').max(255),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER'], {
    required_error: 'Vui lòng chọn giới tính',
  }),
  birthDate: z.string().optional(),
  email: z.string().email('Email không hợp lệ').optional().or(z.literal('')),
});

type MemberFormData = z.infer<typeof memberSchema>;

// Use in component
function MemberForm({ onSubmit }: { onSubmit: (data: MemberFormData) => void }) {
  const form = useForm<MemberFormData>({
    resolver: zodResolver(memberSchema),
    defaultValues: {
      fullName: '',
      gender: undefined,
      birthDate: '',
      email: '',
    },
  });

  return (
    <form onSubmit={form.handleSubmit(onSubmit)}>
      <Input
        {...form.register('fullName')}
        error={form.formState.errors.fullName?.message}
      />
      {/* More fields */}
    </form>
  );
}
```

---

## 3. Backend Conventions (Spring Boot + Java)

### 3.1. Package Structure

```
com.lineagehub/
├── config/                 # Configuration classes
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   └── JwtConfig.java
├── controller/             # REST Controllers
│   ├── AuthController.java
│   └── MemberController.java
├── service/                # Business logic
│   ├── AuthService.java
│   ├── impl/
│   │   └── AuthServiceImpl.java
│   └── MemberService.java
├── repository/             # Data access
│   └── MemberRepository.java
├── entity/                 # JPA Entities
│   └── Member.java
├── dto/                    # Data Transfer Objects
│   ├── request/
│   │   └── CreateMemberRequest.java
│   └── response/
│       └── MemberResponse.java
├── mapper/                 # DTO mappers
│   └── MemberMapper.java
├── exception/              # Custom exceptions
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
├── security/               # Security related
│   └── JwtTokenProvider.java
└── util/                   # Utilities
    └── DateUtils.java
```

### 3.2. Controller Convention

```java
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Member management APIs")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    @Operation(summary = "Lấy danh sách thành viên")
    public ResponseEntity<Page<MemberResponse>> getMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName"));
        Page<MemberResponse> members = memberService.getMembers(pageable, branchId, search);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết thành viên")
    public ResponseEntity<MemberDetailResponse> getMember(@PathVariable UUID id) {
        MemberDetailResponse member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    @PostMapping
    @Operation(summary = "Tạo thành viên mới")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<MemberResponse> createMember(
            @Valid @RequestBody CreateMemberRequest request) {
        
        MemberResponse created = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thành viên")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMemberRequest request) {
        
        MemberResponse updated = memberService.updateMember(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thành viên")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 3.3. Service Convention

```java
// Interface
public interface MemberService {
    Page<MemberResponse> getMembers(Pageable pageable, UUID branchId, String search);
    MemberDetailResponse getMemberById(UUID id);
    MemberResponse createMember(CreateMemberRequest request);
    MemberResponse updateMember(UUID id, UpdateMemberRequest request);
    void deleteMember(UUID id);
}

// Implementation
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BranchRepository branchRepository;
    private final MemberMapper memberMapper;
    private final AuditService auditService;

    @Override
    public Page<MemberResponse> getMembers(Pageable pageable, UUID branchId, String search) {
        Page<Member> members;
        
        if (branchId != null && search != null) {
            members = memberRepository.findByBranchIdAndFullNameContaining(
                branchId, search, pageable);
        } else if (branchId != null) {
            members = memberRepository.findByBranchId(branchId, pageable);
        } else if (search != null) {
            members = memberRepository.findByFullNameContaining(search, pageable);
        } else {
            members = memberRepository.findAll(pageable);
        }
        
        return members.map(memberMapper::toResponse);
    }

    @Override
    public MemberDetailResponse getMemberById(UUID id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        return memberMapper.toDetailResponse(member);
    }

    @Override
    @Transactional
    public MemberResponse createMember(CreateMemberRequest request) {
        // Validate branch exists
        Branch branch = branchRepository.findById(request.getBranchId())
            .orElseThrow(() -> new ResourceNotFoundException("Branch", request.getBranchId()));

        // Create member
        Member member = memberMapper.toEntity(request);
        member.setBranch(branch);
        
        Member saved = memberRepository.save(member);
        
        // Audit log
        auditService.log(EntityType.MEMBER, saved.getId(), Action.CREATE, null, saved);
        
        return memberMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteMember(UUID id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        
        // Check for relationships
        if (hasRelationships(member)) {
            throw new BusinessException("Không thể xóa thành viên có quan hệ gia đình");
        }
        
        memberRepository.delete(member);
        auditService.log(EntityType.MEMBER, id, Action.DELETE, member, null);
    }
}
```

### 3.4. Entity Convention

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

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    private Integer generation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

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
    public boolean isDeceased() {
        return deathDate != null;
    }
}
```

### 3.5. DTO Convention

```java
// Request DTO - dùng cho input
@Data
@Builder
public class CreateMemberRequest {
    
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 255, message = "Họ tên không được quá 255 ký tự")
    private String fullName;

    @NotNull(message = "Giới tính không được để trống")
    private Gender gender;

    private LocalDate birthDate;
    
    private LocalDate deathDate;
    
    @Size(max = 500)
    private String address;
    
    @Pattern(regexp = "^\\d{10,11}$", message = "Số điện thoại không hợp lệ")
    private String phone;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private String notes;

    @NotNull(message = "Nhánh không được để trống")
    private UUID branchId;
    
    private List<UUID> parentIds;
    
    private List<UUID> spouseIds;
}

// Response DTO - dùng cho output
@Data
@Builder
public class MemberResponse {
    private UUID id;
    private String fullName;
    private Gender gender;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private String address;
    private String phone;
    private String email;
    private String avatarUrl;
    private UUID branchId;
    private String branchName;
    private Integer generation;
    private boolean isDeceased;
    private Instant createdAt;
    private Instant updatedAt;
}

// Detail Response - khi cần thêm thông tin
@Data
@Builder
public class MemberDetailResponse {
    private UUID id;
    private String fullName;
    // ... all fields from MemberResponse
    private String notes;
    private RelationshipsDto relationships;
    private UserSummaryDto createdBy;
}
```

### 3.6. Exception Handling

```java
// Custom exceptions
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, UUID id) {
        super(String.format("%s với ID %s không tồn tại", resource, id));
    }
}

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

// Global exception handler
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .message(message)
            .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONFLICT.value())
            .error("Business Rule Violation")
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
```

---

## 4. Database Conventions

### 4.1. Naming
- Tables: `snake_case`, plural (`members`, `audit_logs`)
- Columns: `snake_case` (`full_name`, `created_at`)
- Primary keys: `id`
- Foreign keys: `{table_singular}_id` (`branch_id`, `user_id`)
- Indexes: `idx_{table}_{columns}`
- Constraints: `chk_{table}_{description}`, `uk_{table}_{columns}`

### 4.2. Migration Files

```
V1__init_schema.sql          # Initial tables
V2__seed_super_admin.sql     # Seed data
V3__add_index_members.sql    # Add indexes
V4__alter_members_notes.sql  # Schema changes
```

---

## 5. Git Conventions

### 5.1. Branch Naming
```
main                    # Production-ready
develop                 # Integration branch
feature/auth-login      # New feature
bugfix/member-delete    # Bug fix
hotfix/security-patch   # Urgent fix
```

### 5.2. Commit Messages
```
feat: add member search functionality
fix: resolve login redirect issue
docs: update API documentation
refactor: extract member validation logic
test: add member service unit tests
chore: update dependencies
```

### 5.3. Pull Request Template
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing performed

## Checklist
- [ ] Code follows conventions
- [ ] Self-reviewed
- [ ] No console.log / print statements
- [ ] No hardcoded values
```

---

## 6. API Documentation

### 6.1. Swagger Annotations

```java
@Operation(
    summary = "Tạo thành viên mới",
    description = "Tạo một thành viên mới trong nhánh gia đình"
)
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Tạo thành công"),
    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
    @ApiResponse(responseCode = "401", description = "Chưa đăng nhập"),
    @ApiResponse(responseCode = "403", description = "Không có quyền")
})
@PostMapping
public ResponseEntity<MemberResponse> createMember(
    @Parameter(description = "Thông tin thành viên") 
    @Valid @RequestBody CreateMemberRequest request) {
    // ...
}
```

---

## 7. Security Conventions

### 7.1. Password Rules
- Minimum 8 characters
- At least 1 uppercase, 1 lowercase, 1 number
- BCrypt encoding with strength 10

### 7.2. JWT Claims
```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "role": "BRANCH_ADMIN",
  "branchId": "branch-uuid",
  "iat": 1704067200,
  "exp": 1704153600
}
```

### 7.3. API Security
- All endpoints require authentication (except `/api/auth/**`)
- Use `@PreAuthorize` for role-based access
- Validate ownership for resource operations
