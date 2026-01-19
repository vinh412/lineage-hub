package com.lineagehub.dto.response;

import com.lineagehub.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private UUID id;
    private String fullName;
    private Gender gender;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private Integer birthYear;
    private Integer deathYear;
    private Boolean isBloodRelative;
    private String branchName;
    private String address;
    private String phone;
    private String email;
    private String avatarUrl;
    private Integer generation;
    private Boolean isDeceased;
    private Boolean canEdit;  // Whether current user can edit this member
    private Instant createdAt;
    private Instant updatedAt;
    private UserSummaryResponse createdBy;
}
