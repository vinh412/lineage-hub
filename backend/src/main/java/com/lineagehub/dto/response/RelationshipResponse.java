package com.lineagehub.dto.response;

import com.lineagehub.entity.enums.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipResponse {
    private UUID id;
    private UUID fromMemberId;
    private String fromMemberName;
    private UUID toMemberId;
    private String toMemberName;
    private RelationshipType relationshipType;
    private Boolean canEdit;
    private Instant createdAt;
    private UserSummaryResponse createdBy;
}
