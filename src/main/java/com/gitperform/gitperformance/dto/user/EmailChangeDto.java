package com.gitperform.gitperformance.dto.user;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class EmailChangeDto {
    private String currentPassword;
    private String newEmail;
}
