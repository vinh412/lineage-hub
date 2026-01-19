export type UserStatus = 'PENDING' | 'ACTIVE' | 'INACTIVE';

export type RoleType = 'SUPER_ADMIN' | 'BRANCH_ADMIN' | 'USER';

export interface UserRole {
  id: string;
  role: RoleType;
  managedMemberId: string | null;
  managedMemberName: string | null;
  managedMemberGeneration?: number;
  createdAt?: string;
  createdBy?: {
    id: string;
    fullName: string;
  };
}

export interface User {
  id: string;
  email: string;
  fullName: string;
  status: UserStatus;
  roles: UserRole[];
  createdAt?: string;
  permissions?: {
    canEditMembers: boolean;
    canViewAuditLogs: boolean;
    canManageUsers: boolean;
  };
}

// Helper functions
export function isSuperAdmin(user: User): boolean {
  return user.roles.some((r) => r.role === 'SUPER_ADMIN');
}

export function isBranchAdmin(user: User): boolean {
  return user.roles.some((r) => r.role === 'BRANCH_ADMIN');
}

export function getManagedMemberIds(user: User): string[] {
  return user.roles
    .filter((r) => r.role === 'BRANCH_ADMIN' && r.managedMemberId)
    .map((r) => r.managedMemberId!);
}

export function hasRole(user: User, role: RoleType): boolean {
  return user.roles.some((r) => r.role === role);
}
