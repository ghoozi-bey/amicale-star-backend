package com.amicalestar.backend.repositories.Election;

import com.amicalestar.backend.entities.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
}