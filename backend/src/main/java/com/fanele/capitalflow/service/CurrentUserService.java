package com.fanele.capitalflow.service;

import com.fanele.capitalflow.entity.UserEntity;
import com.fanele.capitalflow.exception.ApiException;
import com.fanele.capitalflow.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public UserEntity requireCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ApiException("Authentication required", HttpStatus.UNAUTHORIZED);
        }
        return principal.getUser();
    }
}
