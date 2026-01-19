# 🚀 Getting Started with LineageHub Frontend

Welcome! This guide will help you get the LineageHub frontend up and running.

## ✅ What's Already Set Up

The complete Next.js project structure has been created with all necessary dependencies and configurations. You can start developing immediately!

## 📋 Prerequisites

- Node.js 18+ installed
- Backend server running on `http://localhost:8080`
- Basic knowledge of React, Next.js, and TypeScript

## 🏃 Quick Start

### 1. Install Dependencies (Already Done!)

Dependencies are already installed. If you need to reinstall:

```bash
npm install
```

### 2. Configure Environment

The `.env.local` file has been created with:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

Update this if your backend runs on a different URL.

### 3. Start Development Server

```bash
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

### 4. Test the Setup

1. Navigate to [http://localhost:3000](http://localhost:3000)
2. You should be redirected to `/login`
3. Try registering a new account
4. Login with your credentials
5. You'll see the members list page

## 📁 Project Structure Overview

```
src/
├── app/                    # Next.js App Router
│   ├── (auth)/            # Login & Register pages
│   ├── (dashboard)/       # Protected pages
│   └── layout.tsx         # Root layout with providers
├── components/
│   ├── ui/                # Reusable UI components
│   └── layout/            # Layout components
├── lib/
│   ├── api/               # API client functions
│   ├── hooks/             # Custom React hooks
│   ├── types/             # TypeScript definitions
│   └── utils/             # Helper functions
├── providers/             # React Query & other providers
└── stores/                # Zustand state management
```

## 🎨 Key Technologies

### State Management
- **React Query**: Server state (API data)
- **Zustand**: Client state (auth, UI state)

### Forms
- **react-hook-form**: Form handling
- **zod**: Validation schemas

### UI
- **Tailwind CSS**: Styling
- **Radix UI**: Accessible components
- **Lucide React**: Icons
- **Sonner**: Toast notifications

## 🔧 Development Workflow

### Making API Calls

1. **Define Types** (already done for Phase 1)
   ```typescript
   // src/lib/types/member.ts
   export interface Member {
     id: string;
     fullName: string;
     // ... more fields
   }
   ```

2. **Create API Function** (already done for Phase 1)
   ```typescript
   // src/lib/api/members.ts
   export const membersApi = {
     getAll: (params) => apiClient.get('/members', { params }),
     // ... more methods
   };
   ```

3. **Create React Query Hook** (already done for Phase 1)
   ```typescript
   // src/lib/hooks/use-members.ts
   export function useMembers(params) {
     return useQuery({
       queryKey: ['members', params],
       queryFn: () => membersApi.getAll(params).then(res => res.data),
     });
   }
   ```

4. **Use in Component**
   ```typescript
   function MemberList() {
     const { data, isLoading } = useMembers({ page: 0 });
     // ... render
   }
   ```

### Creating New Pages

1. Create page file in appropriate route group
   ```typescript
   // src/app/(dashboard)/new-page/page.tsx
   export default function NewPage() {
     return <div>New Page</div>;
   }
   ```

2. Add to sidebar navigation (if needed)
   ```typescript
   // src/components/layout/sidebar.tsx
   const navigation = [
     { name: 'New Page', href: '/new-page', icon: Icon },
   ];
   ```

### Creating UI Components

Use the existing base components or create new ones:

```typescript
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';

export function MyComponent() {
  return (
    <Card>
      <Button>Click Me</Button>
    </Card>
  );
}
```

## 🎯 Current Features

### ✅ Implemented
- User authentication (login/register)
- Protected routes with auth guard
- Member list with pagination and search
- Responsive sidebar navigation
- Role-based UI (Super Admin sees Users menu)
- Loading and error states
- Toast notifications
- JWT token management

### 🚧 Ready to Implement
- Member detail page
- Member creation/editing forms
- Family tree visualization
- User management (Super Admin)
- Relationship management
- Statistics dashboard

## 📚 Important Files

### Configuration
- `tailwind.config.ts` - Tailwind setup
- `tsconfig.json` - TypeScript config
- `next.config.ts` - Next.js config
- `.env.local` - Environment variables

### Core Files
- `src/app/layout.tsx` - Root layout
- `src/providers/index.tsx` - React Query provider
- `src/stores/auth-store.ts` - Auth state
- `src/lib/api/client.ts` - Axios instance

## 🔐 Authentication Flow

1. User enters credentials on login page
2. `useLogin` hook calls `/api/auth/login`
3. JWT token saved to localStorage and Zustand store
4. Axios interceptor adds token to all requests
5. Protected routes check auth status via `AuthGuard`
6. 401 responses trigger auto-logout and redirect

## 🎨 Styling Guide

### Using Tailwind

```tsx
<div className="flex items-center gap-4 p-6 rounded-lg bg-card">
  <Button className="w-full" variant="primary">
    Click Me
  </Button>
</div>
```

### Using CSS Variables

The design system uses CSS variables:

```tsx
// These are defined in globals.css
bg-primary        // Primary color
text-primary      // Primary text
bg-background     // Background color
text-foreground   // Foreground text
```

### Component Variants

```tsx
<Button variant="default">Default</Button>
<Button variant="destructive">Delete</Button>
<Button variant="outline">Cancel</Button>
<Button size="sm">Small</Button>
<Button size="lg">Large</Button>
```

## 🐛 Common Issues

### "Module not found" errors
```bash
# Clear Next.js cache
rm -rf .next
npm run dev
```

### TypeScript errors
```bash
# Check types
npx tsc --noEmit
```

### Port already in use
```bash
# Kill process on port 3000 (Windows PowerShell)
Stop-Process -Id (Get-NetTCPConnection -LocalPort 3000).OwningProcess -Force

# Or use different port
npm run dev -- -p 3001
```

## 📖 Additional Resources

- [Next.js Documentation](https://nextjs.org/docs)
- [React Query Documentation](https://tanstack.com/query/latest)
- [Tailwind CSS Documentation](https://tailwindcss.com/docs)
- [Radix UI Documentation](https://www.radix-ui.com/primitives/docs/overview/introduction)

## 🔍 Debugging

### React Query DevTools

Already included! Open your app and look for the React Query DevTools button in the bottom-left corner.

### Browser DevTools

- **Network Tab**: Check API requests/responses
- **Console**: View errors and logs
- **Components Tab**: Inspect React component tree

### VS Code Extensions (Recommended)

- ESLint
- Tailwind CSS IntelliSense
- TypeScript and JavaScript Language Features

## 📝 Next Steps

1. **Explore the code**: Start with `src/app/(dashboard)/members/page.tsx`
2. **Run the app**: `npm run dev`
3. **Make a change**: Try modifying the members list page
4. **Add a feature**: Follow the development phases in `docs/06_DEVELOPMENT_PHASES.md`

## 💡 Tips

- Use TypeScript's autocomplete (Ctrl+Space)
- Check the API documentation in `docs/05_API_DESIGN.md`
- Follow coding conventions in `docs/07_CODING_CONVENTIONS.md`
- Always handle loading and error states
- Use React Query DevTools to debug data fetching

## 🆘 Need Help?

1. Check `README.md` for project overview
2. Check `PROJECT_SETUP.md` for detailed setup info
3. Review documentation in `docs/` folder
4. Check component examples in existing pages

---

**Happy Coding! 🚀**

Start building amazing features for LineageHub!
