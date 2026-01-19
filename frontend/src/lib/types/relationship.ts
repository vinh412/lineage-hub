export type RelationshipType = 'PARENT_CHILD' | 'SPOUSE';

export interface Relationship {
  id: string;
  fromMemberId: string;
  fromMemberName: string;
  toMemberId: string;
  toMemberName: string;
  relationshipType: RelationshipType;
  createdAt: string;
}

export interface CreateParentChildRequest {
  parentId: string;
  childId: string;
}

export interface CreateSpouseRequest {
  member1Id: string;
  member2Id: string;
}

export interface MemberRelationships {
  memberId: string;
  memberName: string;
  parents: RelatedMember[];
  spouses: RelatedMember[];
  children: RelatedMember[];
}

export interface RelatedMember {
  relationshipId: string;
  memberId: string;
  memberName: string;
  gender: string;
  canEdit: boolean;
}
