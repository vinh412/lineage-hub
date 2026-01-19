# LineageHub Frontend

Frontend application for LineageHub - Family Tree Management System.

## Tech Stack

- **Next.js 14** - React framework with App Router
- **TypeScript** - Type safety
- **Tailwind CSS** - Styling
- **React Query (TanStack Query)** - Server state management
- **Zustand** - Client state management
- **React Hook Form + Zod** - Form handling and validation
- **Axios** - HTTP client
- **Radix UI** - Headless UI components
- **Lucide React** - Icons
- **Sonner** - Toast notifications

## Getting Started

### Prerequisites

- Node.js 18+ and npm

### Installation

```bash
# Install dependencies
npm install

# Copy environment file
cp .env.example .env.local

# Update .env.local with your backend API URL
# NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### Development

```bash
# Run development server
npm run dev

# Open http://localhost:3000
```

### Build

```bash
# Create production build
npm run build

# Start production server
npm start
```

## Project Structure

```
src/
├── app/                        # App Router pages
│   ├── (auth)/                # Public auth pages
│   │   ├── login/
│   │   └── register/
│   ├── (dashboard)/           # Protected pages
│   │   ├── members/
│   │   ├── users/
│   │   └── tree/
│   ├── layout.tsx
│   └── page.tsx
├── components/
│   ├── ui/                    # Base UI components
│   ├── forms/                 # Form components
│   ├── tree/                  # Family tree components
│   └── layout/                # Layout components
├── lib/
│   ├── api/                   # API client functions
│   ├── hooks/                 # Custom React hooks
│   ├── types/                 # TypeScript types
│   └── utils/                 # Utility functions
├── providers/                 # React context providers
└── stores/                    # Zustand stores
```

## Features Implemented

### Phase 1 - Core Setup ✅

- [x] Next.js project initialization
- [x] TypeScript configuration
- [x] Tailwind CSS setup
- [x] API client with Axios interceptors
- [x] React Query setup
- [x] Zustand auth store
- [x] TypeScript types matching backend API
- [x] Authentication pages (Login/Register)
- [x] Dashboard layout with sidebar
- [x] Member list page
- [x] Auth guard for protected routes

## API Integration

The frontend communicates with the backend API defined in `docs/05_API_DESIGN.md`.

### Authentication

- Login: `POST /api/auth/login`
- Register: `POST /api/auth/register`
- Get current user: `GET /api/auth/me`

### Authorization

- JWT token stored in localStorage
- Axios interceptor adds `Authorization: Bearer {token}` header
- Auto-redirect to login on 401 responses
- Role-based UI rendering (SUPER_ADMIN, BRANCH_ADMIN, USER)

## Multi-Role Support

Users can have multiple roles simultaneously:
- **SUPER_ADMIN**: Full system access
- **BRANCH_ADMIN**: Manage specific subtrees (can have multiple)
- **USER**: Read-only access

The UI dynamically shows/hides features based on user roles.

## Development Guidelines

### Code Style

- Use functional components with TypeScript
- Follow naming conventions in `docs/07_CODING_CONVENTIONS.md`
- Use React Query for server state
- Use Zustand for client state
- Form validation with react-hook-form + zod
- Always check `canEdit` flag from API before showing edit/delete buttons

### Creating New Pages

1. Create page in appropriate route group
2. Use `AuthGuard` for protected routes
3. Implement loading and error states
4. Follow responsive design principles

### Adding API Endpoints

1. Add types in `src/lib/types/`
2. Create API function in `src/lib/api/`
3. Create React Query hook in `src/lib/hooks/`
4. Use hook in components

## Scripts

```bash
npm run dev          # Start development server
npm run build        # Build for production
npm start            # Start production server
npm run lint         # Run ESLint
npm run type-check   # Run TypeScript compiler check
```

## Environment Variables

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

## Notes

- The frontend strictly follows the API contract defined in backend documentation
- All API response types match the backend DTOs
- JWT authentication with automatic token refresh
- Responsive design using Tailwind CSS
- Accessibility-first with Radix UI components

## Next Steps

To continue development:

1. Implement member detail page (`/members/[id]`)
2. Add member creation/editing forms
3. Build family tree visualization with React Flow
4. Add user management pages (Super Admin only)
5. Implement relationship management
6. Add export functionality
7. Create statistics dashboard

Refer to `docs/06_DEVELOPMENT_PHASES.md` for the complete roadmap.
