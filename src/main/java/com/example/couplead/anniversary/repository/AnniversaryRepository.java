package com.example.couplead.anniversary.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.couplead.anniversary.domain.Anniversary;
import com.example.couplead.couple.domain.Couple;

public interface AnniversaryRepository extends JpaRepository<Anniversary, Long> {
    List<Anniversary> findByCoupleOrderByAnniversaryDateAsc(Couple couple);

    Optional<Anniversary> findByIdAndCouple(
        Long id,
        Couple couple
    );
}
