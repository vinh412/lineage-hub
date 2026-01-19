package com.lineagehub.dto.response;

import com.lineagehub.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSummaryResponse {
    private UUID id;
    private String fullName;
    private Gender gender;
    private Integer birthYear;
    private Integer deathYear;
    private Boolean isBloodRelative;
    private Boolean isDeceased;
    private String avatarUrl;
}
