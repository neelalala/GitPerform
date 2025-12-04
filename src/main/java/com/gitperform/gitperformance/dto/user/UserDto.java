package com.gitperform.gitperformance.dto.user;

import com.gitperform.gitperformance.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String displayName;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
    }
}