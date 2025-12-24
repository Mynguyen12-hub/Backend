package com.nguyenthimynguyen.example10.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private Set<String> roles; // Role names like "ROLE_ADMIN", "ROLE_USER", etc.
}
