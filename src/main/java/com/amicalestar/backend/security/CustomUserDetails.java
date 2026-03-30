package com.amicalestar.backend.security;

import com.amicalestar.backend.entities.Adherent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Adherent adherent;

    public CustomUserDetails(Adherent adherent) {
        this.adherent = adherent;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // 🔥 FIX PRINCIPAL
        String role = "ROLE_" + adherent.getTypeAdherent().name();

        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return adherent.getPassword(); // BCrypt
    }

    @Override
    public String getUsername() {
        return adherent.getEmail();
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
        return adherent.getActif() == null || adherent.getActif();
    }
}