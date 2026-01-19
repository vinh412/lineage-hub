package com.lineagehub.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSpouseRequest {
    
    @NotNull(message = "Member 1 ID không được để trống")
    private UUID member1Id;
    
    @NotNull(message = "Member 2 ID không được để trống")
    private UUID member2Id;
}
