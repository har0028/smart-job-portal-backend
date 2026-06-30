package com.smartjobportal.util;

import com.smartjobportal.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails details) {
            return details;
        }
        throw new IllegalStateException("No authenticated user found in security context");
    }

    public Long getCurrentUserId() {
        return getCurrentUserDetails().getUserId();
    }

    public String getCurrentUserEmail() {
        return getCurrentUserDetails().getUsername();
    }

    public String getCurrentUserRole() {
        return getCurrentUserDetails().getAuthorities()
                .iterator().next().getAuthority();
    }
}
