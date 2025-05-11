package com.example.cashwalk.security;

import com.example.cashwalk.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 사용자 인증 정보를 담는 클래스 (Spring Security가 내부적으로 사용하는 객체)
 */
@Getter
public class CustomUserDetails implements UserDetails {
    private final User user;
    private final Long userId;
    private final String email;
    private final String password;
    private final String role;

    public CustomUserDetails(User user) {
        this.user = user;
        this.userId = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole(); // ex: ADMIN or USER
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ✅ "ADMIN" → "ROLE_ADMIN" 형식으로 보정
        String fullRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return Collections.singletonList(new SimpleGrantedAuthority(fullRole));
    }


    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
