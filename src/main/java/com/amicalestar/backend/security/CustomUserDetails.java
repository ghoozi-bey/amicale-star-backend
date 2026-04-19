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

        // 🔥 IMPORTANT : utiliser EXACTEMENT le role DB
        return List.of(new SimpleGrantedAuthority("ROLE_" + adherent.getTypeAdherent().name()));
    }

    @Override
    public String getPassword() {
        return adherent.getPassword();
    }

    @Override
    public String getUsername() {
        return adherent.getMatricule();
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