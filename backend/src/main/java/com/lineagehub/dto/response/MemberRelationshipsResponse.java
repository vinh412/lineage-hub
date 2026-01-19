package com.lineagehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRelationshipsResponse {
    private List<MemberSummaryResponse> parents;
    private List<MemberSummaryResponse> spouses;
    private List<MemberSummaryResponse> children;
}
