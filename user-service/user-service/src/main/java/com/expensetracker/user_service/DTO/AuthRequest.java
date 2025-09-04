package com.expensetracker.user_service.DTO;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
