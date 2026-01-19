import apiClient from './client';
import type {
  Relationship,
  CreateParentChildRequest,
  CreateSpouseRequest,
  MemberRelationships,
} from '@/lib/types';

export const relationshipsApi = {
  addParentChild: (data: CreateParentChildRequest) =>
    apiClient.post<Relationship>('/relationships/parent-child', data),

  addSpouse: (data: CreateSpouseRequest) =>
    apiClient.post<Relationship>('/relationships/spouse', data),

  delete: (id: string) =>
    apiClient.delete(`/relationships/${id}`),

  getMemberRelationships: (memberId: string) =>
    apiClient.get<MemberRelationships>(`/members/${memberId}/relationships`),
};
