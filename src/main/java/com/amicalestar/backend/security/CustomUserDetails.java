package com.amicalestar.backend.security;

import com.amicalestar.backend.entities.Adherent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private Adherent adherent;

    public CustomUserDetails(Adherent adherent) {
        this.adherent = adherent;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(adherent.getRoleName())
        );
    }

    public String getPassword() {
        return adherent.getPassword();
    }

    @Override
    public String getUsername() {
        return adherent.getEmail(); // login with email
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return adherent.getActif();
    }
}