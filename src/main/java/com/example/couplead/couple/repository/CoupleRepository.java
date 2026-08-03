package com.example.couplead.couple.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.couplead.couple.domain.Couple;

public interface CoupleRepository extends JpaRepository<Couple, Long> {
    
}
