export type Gender = 'MALE' | 'FEMALE' | 'OTHER';

export interface Member {
  id: string;
  fullName: string;
  gender: Gender;
  birthDate: string | null;
  deathDate: string | null;
  address: string | null;
  phone: string | null;
  email: string | null;
  avatarUrl: string | null;
  isBloodRelative: boolean;
  branchName: string | null;
  generation: number;
  isDeceased: boolean;
  canEdit: boolean;
}

export interface MemberDetail extends Member {
  notes: string | null;
  relationships: {
    parents: MemberSummary[];
    spouses: MemberSummary[];
    children: MemberSummary[];
  };
  createdAt: string;
  updatedAt: string;
  createdBy: {
    id: string;
    fullName: string;
  };
}

export interface MemberSummary {
  id: string;
  fullName: string;
  gender: Gender;
  isBloodRelative: boolean;
  relationshipId?: string;
  canEdit?: boolean;
}

export interface CreateMemberRequest {
  fullName: string;
  gender: Gender;
  birthDate?: string | null;
  deathDate?: string | null;
  address?: string;
  phone?: string;
  email?: string;
  notes?: string;
  isBloodRelative: boolean;
  branchName?: string | null;
  parentIds?: string[];
  spouseIds?: string[];
}

export interface UpdateMemberRequest {
  fullName: string;
  gender: Gender;
  birthDate?: string | null;
  deathDate?: string | null;
  address?: string;
  phone?: string;
  email?: string;
  notes?: string;
  isBloodRelative: boolean;
  branchName?: string | null;
}
