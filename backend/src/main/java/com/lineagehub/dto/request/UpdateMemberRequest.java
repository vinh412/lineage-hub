package com.lineagehub.dto.request;

import com.lineagehub.entity.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberRequest {
    
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 255, message = "Họ tên không được quá 255 ký tự")
    private String fullName;
    
    @NotNull(message = "Giới tính không được để trống")
    private Gender gender;
    
    private LocalDate birthDate;
    
    private LocalDate deathDate;
    
    @NotNull(message = "is_blood_relative không được để trống")
    private Boolean isBloodRelative;
    
    @Size(max = 255, message = "Tên nhánh không được quá 255 ký tự")
    private String branchName;
    
    private String address;
    
    @Pattern(regexp = "^$|^\\d{10,11}$", message = "Số điện thoại không hợp lệ")
    private String phone;
    
    @Pattern(regexp = "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$", message = "Email không hợp lệ")
    private String email;
    
    private String notes;
    
    private Integer generation;
}
