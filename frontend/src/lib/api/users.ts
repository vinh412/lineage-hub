import apiClient from './client';
import type { User, UserRole, PaginatedResponse } from '@/lib/types';

export interface GetUsersParams {
  page?: number;
  size?: number;
  status?: string;
  role?: string;
  search?: string;
}

export interface AddUserRoleRequest {
  role: 'SUPER_ADMIN' | 'BRANCH_ADMIN' | 'USER';
  managedMemberId?: string | null;
}

export interface UpdateUserRolesRequest {
  roles: Array<{
    role: 'SUPER_ADMIN' | 'BRANCH_ADMIN' | 'USER';
    managedMemberId?: string | null;
  }>;
}

export const usersApi = {
  getAll: (params?: GetUsersParams) =>
    apiClient.get<PaginatedResponse<User>>('/users', { params }),

  getById: (id: string) =>
    apiClient.get<User>(`/users/${id}`),

  approve: (id: string) =>
    apiClient.patch(`/users/${id}/approve`),

  deactivate: (id: string) =>
    apiClient.patch(`/users/${id}/deactivate`),

  delete: (id: string) =>
    apiClient.delete(`/users/${id}`),

  // User Roles
  getRoles: (userId: string) =>
    apiClient.get<{ userId: string; userEmail: string; roles: UserRole[] }>(
      `/users/${userId}/roles`
    ),

  addRole: (userId: string, data: AddUserRoleRequest) =>
    apiClient.post<UserRole>(`/users/${userId}/roles`, data),

  deleteRole: (userId: string, roleId: string) =>
    apiClient.delete(`/users/${userId}/roles/${roleId}`),

  updateRoles: (userId: string, data: UpdateUserRolesRequest) =>
    apiClient.put(`/users/${userId}/roles`, data),
};
