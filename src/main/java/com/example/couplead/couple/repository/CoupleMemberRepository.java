package com.example.couplead.couple.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.user.domain.User;

public interface CoupleMemberRepository extends JpaRepository<CoupleMember, Long> {
    Optional<CoupleMember> findByUser(User user);
    boolean existsByUser(User user);
    List<CoupleMember> findByCouple(Couple couple);
}
