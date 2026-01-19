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
public class CreateParentChildRequest {
    
    @NotNull(message = "Parent ID không được để trống")
    private UUID parentId;
    
    @NotNull(message = "Child ID không được để trống")
    private UUID childId;
}
