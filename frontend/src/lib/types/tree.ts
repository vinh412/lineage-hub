export interface TreeNode {
  id: string;
  fullName: string;
  gender: string;
  birthYear: number | null;
  deathYear: number | null;
  avatarUrl: string | null;
  generation: number;
  isBloodRelative: boolean;
  branchName: string | null;
  isDeceased: boolean;
  canEdit: boolean;
}

export interface TreeEdge {
  id: string;
  source: string;
  target: string;
  type: 'PARENT_CHILD' | 'SPOUSE';
}

export interface TreeData {
  nodes: TreeNode[];
  edges: TreeEdge[];
  metadata: {
    totalNodes: number;
    totalEdges: number;
    maxGeneration: number;
  };
}

export interface SubtreeData {
  rootMember: {
    id: string;
    fullName: string;
    gender: string;
  };
  members: Array<{
    id: string;
    fullName: string;
    depth: number;
  }>;
  totalMembers: number;
  maxDepth: number;
}
