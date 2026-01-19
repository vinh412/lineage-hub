import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { membersApi, type GetMembersParams } from '@/lib/api';
import { toast } from 'sonner';
import type { CreateMemberRequest, UpdateMemberRequest } from '@/lib/types';

export function useMembers(params?: GetMembersParams) {
  return useQuery({
    queryKey: ['members', params],
    queryFn: () => membersApi.getAll(params).then((res) => res.data),
  });
}

export function useMember(id: string) {
  return useQuery({
    queryKey: ['members', id],
    queryFn: () => membersApi.getById(id).then((res) => res.data),
    enabled: !!id,
  });
}

export function useCreateMember() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateMemberRequest) => membersApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['members'] });
      toast.success('Đã tạo thành viên mới');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Có lỗi xảy ra');
    },
  });
}

export function useUpdateMember(id: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: UpdateMemberRequest) => membersApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['members'] });
      queryClient.invalidateQueries({ queryKey: ['members', id] });
      toast.success('Đã cập nhật thành viên');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Có lỗi xảy ra');
    },
  });
}

export function useDeleteMember() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, force }: { id: string; force?: boolean }) =>
      membersApi.delete(id, force),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['members'] });
      toast.success('Đã xóa thành viên');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Có lỗi xảy ra');
    },
  });
}

export function useUploadAvatar(id: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (file: File) => membersApi.uploadAvatar(id, file),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['members', id] });
      toast.success('Đã cập nhật ảnh đại diện');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Có lỗi xảy ra');
    },
  });
}

export function useMemberSubtree(id: string, params?: { maxDepth?: number; includeSpouses?: boolean }) {
  return useQuery({
    queryKey: ['members', id, 'subtree', params],
    queryFn: () => membersApi.getSubtree(id, params).then((res) => res.data),
    enabled: !!id,
  });
}
