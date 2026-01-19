import apiClient from './client';
import type { TreeData } from '@/lib/types';

export interface GetTreeParams {
  rootMemberId?: string;
  depth?: number;
}

export const treeApi = {
  getTree: (params?: GetTreeParams) =>
    apiClient.get<TreeData>('/tree', { params }),

  getPath: (fromId: string, toId: string) =>
    apiClient.get('/tree/path', { params: { fromId, toId } }),
};
