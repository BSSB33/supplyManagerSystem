package com.elte.supplymanagersystem.security;

import com.elte.supplymanagersystem.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Authenticated User for Security Management
 */
@RequestScope
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {
    private User user;
}
