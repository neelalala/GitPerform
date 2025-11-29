package com.gitperform.gitperformance.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // constructors, getters, setters
}
