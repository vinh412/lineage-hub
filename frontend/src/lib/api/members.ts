import apiClient from './client';
import type {
  Member,
  MemberDetail,
  CreateMemberRequest,
  UpdateMemberRequest,
  PaginatedResponse,
  SubtreeData,
} from '@/lib/types';

export interface GetMembersParams {
  page?: number;
  size?: number;
  generation?: number;
  gender?: string;
  search?: string;
  isDeceased?: boolean;
  isBloodRelative?: boolean;
  rootMemberId?: string;
}

export const membersApi = {
  getAll: (params?: GetMembersParams) =>
    apiClient.get<PaginatedResponse<Member>>('/members', { params }),

  getById: (id: string) =>
    apiClient.get<MemberDetail>(`/members/${id}`),

  create: (data: CreateMemberRequest) =>
    apiClient.post<Member>('/members', data),

  update: (id: string, data: UpdateMemberRequest) =>
    apiClient.put<Member>(`/members/${id}`, data),

  delete: (id: string, force?: boolean) =>
    apiClient.delete(`/members/${id}`, { params: { force } }),

  uploadAvatar: (id: string, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient.post<{ avatarUrl: string }>(`/members/${id}/avatar`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },

  getSubtree: (id: string, params?: { maxDepth?: number; includeSpouses?: boolean }) =>
    apiClient.get<SubtreeData>(`/members/${id}/subtree`, { params }),
};
