import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { usersApi, type GetUsersParams, type AddUserRoleRequest } from '@/lib/api';
import { toast } from 'sonner';

export function useUsers(params?: GetUsersParams) {
  return useQuery({
    queryKey: ['users', params],
    queryFn: () => usersApi.getAll(params).then((res) => res.data),
  });
}

export function useUser(id: string) {
  return useQuery({
    queryKey: ['users', id],
    queryFn: () => usersApi.getById(id).then((res) => res.data),
    enabled: !!id,
  });
}

export function useApproveUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => usersApi.approve(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      toast.success('Đã phê duyệt user');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Có lỗi xảy ra');
    },
  });
}

export function useDeactivateUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => usersApi.deactivate(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      toast.success('Đã vô hiệu hóa user');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Có lỗi xảy ra');
    },
  });
}

export function useDeleteUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => usersApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      toast.success('Đã xóa user');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Có lỗi xảy ra');
    },
  });
}

export function useUserRoles(userId: string) {
  return useQuery({
    queryKey: ['users', userId, 'roles'],
    queryFn: () => usersApi.getRoles(userId).then((res) => res.data),
    enabled: !!userId,
  });
}

export function useAddUserRole(userId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: AddUserRoleRequest) => usersApi.addRole(userId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users', userId, 'roles'] });
      queryClient.invalidateQueries({ queryKey: ['users'] });
      toast.success('Đã thêm role');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Có lỗi xảy ra');
    },
  });
}

export function useDeleteUserRole(userId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (roleId: string) => usersApi.deleteRole(userId, roleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users', userId, 'roles'] });
      queryClient.invalidateQueries({ queryKey: ['users'] });
      toast.success('Đã xóa role');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Có lỗi xảy ra');
    },
  });
}
