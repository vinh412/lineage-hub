# LineageHub Backend

Spring Boot backend for LineageHub - Family Tree Management System.

## Tech Stack

- **Java 21** - LTS version
- **Spring Boot 3.5.9** - Latest stable
- **Spring Security 6.x** - JWT authentication
- **PostgreSQL 16** - Primary database
- **JPA/Hibernate** - ORM
- **Flyway** - Database migrations
- **MapStruct** - DTO mapping
- **Lombok** - Boilerplate reduction
- **Swagger/OpenAPI** - API documentation

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 16
- (Optional) Docker for running PostgreSQL

## Setup

### 1. Database Setup

**Option A: Using Docker Compose (Recommended)**

```bash
# From project root
docker-compose up -d postgres
```

**Option B: Manual Setup**

See [DATABASE_SETUP.md](DATABASE_SETUP.md) for detailed instructions.

**Quick Manual Setup:**

```bash
# Run setup script (from backend directory)
psql -U postgres -f src/main/resources/db/setup/setup_database.sql
```

### 2. Configuration

The default configuration works with the Docker setup above. If you need to change database credentials, edit `src/main/resources/application.yml` or set environment variables:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=lineagehub
export DB_USER=lineagehub
export DB_PASSWORD=lineagehub123
export JWT_SECRET=your-256-bit-secret-key
```

### 3. Run the Application

```bash
# Clean and build
mvn clean install

# Run
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

### 4. Access Swagger UI

Open `http://localhost:8080/swagger-ui.html` to view and test the API documentation.

## Default Credentials

A Super Admin account is automatically seeded:

- **Email:** `admin@lineagehub.local`
- **Password:** `Admin@123`

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `GET /api/auth/me` - Get current user info

### User Management (Super Admin only)

- `GET /api/users` - List users
- `GET /api/users/{id}` - Get user details
- `PATCH /api/users/{id}/approve` - Approve pending user
- `PATCH /api/users/{id}/deactivate` - Deactivate user
- `DELETE /api/users/{id}` - Delete user

### User Roles (Super Admin only)

- `GET /api/users/{userId}/roles` - Get user's roles
- `POST /api/users/{userId}/roles` - Add role to user
- `PUT /api/users/{userId}/roles` - Replace all roles
- `DELETE /api/users/{userId}/roles/{roleId}` - Remove role

### Members

- `GET /api/members` - List members (with filters)
- `GET /api/members/{id}` - Get member details
- `POST /api/members` - Create member (Admin only)
- `PUT /api/members/{id}` - Update member (Admin only)
- `DELETE /api/members/{id}` - Delete member (Admin only)

### Relationships

- `POST /api/relationships/parent-child` - Create parent-child relationship
- `POST /api/relationships/spouse` - Create spouse relationship
- `DELETE /api/relationships/{id}` - Delete relationship

## Authorization Model

### Roles

1. **SUPER_ADMIN** - Full system access
2. **BRANCH_ADMIN** - Manage specific subtree(s)
   - Can have multiple BRANCH_ADMIN roles with different `managed_member_id`
   - Can edit members in their subtrees
   - Cannot edit relationships with ancestors
3. **USER** - Read-only access

### Subtree-Based Permissions

BRANCH_ADMIN users can manage all members in their assigned subtrees (including descendants and spouses).

Example:
```
         A1 (ancestor)
          |
    ┌─────┼─────┐
    |     |     |
   A2    A3    A4
(managed)     (managed)
```

If a user has BRANCH_ADMIN roles for both A2 and A4, they can edit:
- A2, A4, and all their descendants
- Spouses of A2, A4, and their descendants
- Relationships within those subtrees

## Database Migrations

Flyway automatically runs migrations on startup. Migration files are in `src/main/resources/db/migration/`:

- `V1__init_schema.sql` - Initial schema
- `V2__seed_super_admin.sql` - Seed admin account
- `V3__add_fulltext_search.sql` - Add search indexes

## Development

### Run Tests

```bash
mvn test
```

### Build JAR

```bash
mvn clean package
```

The JAR file will be in `target/` directory.

### Hot Reload (Spring DevTools)

The project includes Spring DevTools for automatic restart during development.

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `lineagehub` | Database name |
| `DB_USER` | `lineagehub` | Database user |
| `DB_PASSWORD` | `lineagehub123` | Database password |
| `JWT_SECRET` | (default) | JWT signing secret |
| `JWT_EXPIRATION` | `86400000` | Token expiration (24h in ms) |
| `SERVER_PORT` | `8080` | Server port |

## Project Structure

```
src/main/java/com/lineagehub/
├── config/                 # Configuration classes
├── controller/             # REST Controllers
├── service/                # Business logic
├── repository/             # Data access (JPA)
├── entity/                 # JPA Entities
│   └── enums/             # Enums
├── dto/                    # Data Transfer Objects
│   ├── request/           # Request DTOs
│   └── response/          # Response DTOs
├── mapper/                 # MapStruct mappers
├── exception/              # Custom exceptions
└── security/               # Security & JWT
```

## Documentation

See the `docs/` folder in the project root for detailed documentation:

- `00_BUSINESS_REQUIREMENTS.md` - Business requirements
- `02_ARCHITECTURE.md` - System architecture
- `04_DATABASE_SCHEMA.md` - Database schema
- `05_API_DESIGN.md` - API specifications
- `PROMPT_BACKEND_AGENT.md` - Backend development guidelines

## License

Private - LineageHub Team
