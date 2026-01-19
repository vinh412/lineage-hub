# 🌳 LineageHub

A comprehensive family tree management system for Vietnamese families, enabling genealogy tracking, relationship management, and family tree visualization.

## 📋 Project Overview

LineageHub helps families manage and preserve their genealogy with features including:

- **Family Tree Management** - Add, edit, and organize family members
- **Relationship Tracking** - Parent-child and spouse relationships
- **Role-Based Access Control** - Super Admin, Branch Admin, and User roles
- **Subtree Management** - Branch admins manage specific family branches
- **Family Tree Visualization** - Interactive tree view (coming in Phase 2)
- **Export Features** - Export family trees as PNG/PDF (coming in Phase 2)

## 🏗️ Architecture

```
├── backend/           # Spring Boot REST API
├── frontend/          # Next.js application (Phase 2)
└── docs/              # Project documentation
```

### Tech Stack

**Backend:**
- Java 21 + Spring Boot 3.5.9
- Spring Security with JWT
- PostgreSQL 16
- Flyway migrations
- MapStruct, Lombok

**Frontend:** (Phase 2)
- Next.js 14 with App Router
- TypeScript
- Tailwind CSS
- React Flow for tree visualization

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Maven 3.6+
- Docker & Docker Compose (recommended)
- Node.js 18+ (for frontend, Phase 2)

### 1. Start Database

```bash
docker-compose up -d postgres
```

This starts PostgreSQL on `localhost:5432` with:
- Database: `lineagehub`
- User: `lineagehub`
- Password: `lineagehub123`

### 2. Run Backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`

### 3. Access Swagger UI

Open `http://localhost:8080/swagger-ui.html`

### 4. Login

Default Super Admin account:
- **Email:** `admin@lineagehub.local`
- **Password:** `Admin@123`

## 📚 Documentation

Detailed documentation is available in the `docs/` folder:

- [Business Requirements](docs/00_BUSINESS_REQUIREMENTS.md)
- [Architecture Overview](docs/02_ARCHITECTURE.md)
- [Tech Stack Details](docs/03_TECH_STACK.md)
- [Database Schema](docs/04_DATABASE_SCHEMA.md)
- [API Design](docs/05_API_DESIGN.md)
- [Development Phases](docs/06_DEVELOPMENT_PHASES.md)
- [Coding Conventions](docs/07_CODING_CONVENTIONS.md)

### Backend Development

See [Backend README](backend/README.md) for detailed backend setup and API documentation.

## 🔐 Authorization Model

### Roles

1. **SUPER_ADMIN** - Full system access, manage users and roles
2. **BRANCH_ADMIN** - Manage specific family subtree(s)
   - Can have multiple subtrees
   - Edit members within subtrees
   - Cannot modify ancestor relationships
3. **USER** - Read-only access to view family tree

### Subtree Permissions

Branch Admins manage members based on assigned subtrees:

```
         A1 (Ancestor)
          |
    ┌─────┼─────┐
    |     |     |
   A2    A3    A4
    |           |
   B1...       D1...
```

If a user has `BRANCH_ADMIN` roles for A2 and A4:
- ✅ Can edit A2, A4, and their descendants
- ✅ Can add spouses to subtree members
- ❌ Cannot edit A1 or relationship A1→A2

## 🗂️ Project Structure

```
LineageHub/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/lineagehub/
│   │   │   │   ├── config/         # Configuration
│   │   │   │   ├── controller/     # REST Controllers
│   │   │   │   ├── service/        # Business logic
│   │   │   │   ├── repository/     # Data access
│   │   │   │   ├── entity/         # JPA entities
│   │   │   │   ├── dto/            # DTOs
│   │   │   │   ├── mapper/         # MapStruct mappers
│   │   │   │   ├── security/       # JWT & Security
│   │   │   │   └── exception/      # Exception handling
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/   # Flyway migrations
│   │   └── test/
│   └── pom.xml
├── frontend/                        # Phase 2
├── docs/                            # Documentation
├── docker-compose.yml              # Docker setup
└── README.md
```

## 📖 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/auth/me` - Get current user info

### Users (Super Admin)
- `GET /api/users` - List all users
- `PATCH /api/users/{id}/approve` - Approve pending user
- `PATCH /api/users/{id}/deactivate` - Deactivate user

### User Roles (Super Admin)
- `GET /api/users/{userId}/roles` - Get user's roles
- `POST /api/users/{userId}/roles` - Add role
- `DELETE /api/users/{userId}/roles/{roleId}` - Remove role

### Members
- `GET /api/members` - List members (with filters)
- `GET /api/members/{id}` - Get member details
- `POST /api/members` - Create member (Admin only)
- `PUT /api/members/{id}` - Update member (Admin only)
- `DELETE /api/members/{id}` - Delete member (Admin only)

### Relationships
- `POST /api/relationships/parent-child` - Add parent-child relationship
- `POST /api/relationships/spouse` - Add spouse relationship
- `DELETE /api/relationships/{id}` - Remove relationship

For complete API documentation, see [API Design](docs/05_API_DESIGN.md) or the Swagger UI.

## 🛠️ Development

### Environment Variables

Create `.env` file in backend directory:

```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=lineagehub
DB_USER=lineagehub
DB_PASSWORD=lineagehub123
JWT_SECRET=your-256-bit-secret-key-here
SPRING_PROFILES_ACTIVE=dev
```

### Database Management

Access pgAdmin at `http://localhost:5050`:
- Email: `admin@lineagehub.local`
- Password: `admin123`

### Run Tests

```bash
cd backend
mvn test
```

## 🗺️ Development Roadmap

### ✅ Phase 1: Core MVP (Completed)
- [x] Authentication & JWT
- [x] User management
- [x] Multi-role support (user_roles table)
- [x] Member CRUD with authorization
- [x] Parent-child & spouse relationships
- [x] Subtree-based permissions

### 🚧 Phase 2: Tree Visualization & Export (Next)
- [ ] Family tree visualization (React Flow)
- [ ] Export to PNG/PDF
- [ ] Full-text search
- [ ] Advanced filters

### 📅 Phase 3: Enhancement & Polish
- [ ] Audit logs viewer
- [ ] OAuth integration (Google, Facebook)
- [ ] Performance optimization
- [ ] Production deployment

See [Development Phases](docs/06_DEVELOPMENT_PHASES.md) for details.

## 🤝 Contributing

This is a private project. For development guidelines, see:
- [Backend Agent Prompt](docs/PROMPT_BACKEND_AGENT.md)
- [Coding Conventions](docs/07_CODING_CONVENTIONS.md)

## 📝 License

Private - LineageHub Team

## 🙋 Support

For questions or issues, contact the development team.
