'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useAuth, useLogout } from '@/lib/hooks';
import { isSuperAdmin } from '@/lib/types';
import { cn } from '@/lib/utils';
import { Users, UserCircle, Home, LogOut, GitBranch } from 'lucide-react';
import { Button } from '@/components/ui/button';

const navigation = [
  { name: 'Trang chủ', href: '/members', icon: Home },
  { name: 'Thành viên', href: '/members', icon: UserCircle },
  { name: 'Cây gia phả', href: '/tree', icon: GitBranch },
];

export function Sidebar() {
  const pathname = usePathname();
  const { user } = useAuth();
  const logout = useLogout();

  const navItems = [...navigation];

  // Add Users menu for Super Admin
  if (user && isSuperAdmin(user)) {
    navItems.push({
      name: 'Quản lý Users',
      href: '/users',
      icon: Users,
    });
  }

  return (
    <div className="flex h-screen w-64 flex-col border-r bg-card">
      <div className="flex h-16 items-center border-b px-6">
        <h1 className="text-xl font-bold text-primary">LineageHub</h1>
      </div>

      <nav className="flex-1 space-y-1 p-4">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = pathname === item.href || pathname?.startsWith(item.href);
          return (
            <Link
              key={item.name}
              href={item.href}
              className={cn(
                'flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-primary text-primary-foreground'
                  : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
              )}
            >
              <Icon className="h-5 w-5" />
              {item.name}
            </Link>
          );
        })}
      </nav>

      <div className="border-t p-4">
        <div className="mb-3 rounded-lg bg-muted p-3">
          <p className="text-sm font-medium">{user?.fullName}</p>
          <p className="text-xs text-muted-foreground">{user?.email}</p>
        </div>
        <Button
          variant="outline"
          className="w-full"
          onClick={logout}
        >
          <LogOut className="mr-2 h-4 w-4" />
          Đăng xuất
        </Button>
      </div>
    </div>
  );
}
