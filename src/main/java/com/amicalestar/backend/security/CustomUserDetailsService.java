package com.amicalestar.backend.security;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.repositories.AdherentRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdherentRepository repository;

    public CustomUserDetailsService(AdherentRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Adherent user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }
}