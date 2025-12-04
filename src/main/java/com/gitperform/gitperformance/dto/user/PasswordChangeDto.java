package com.gitperform.gitperformance.dto.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PasswordChangeDto {
    private String currentPassword;
    private String newPassword;
}
