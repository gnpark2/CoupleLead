package com.example.couplead.couple.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.user.domain.User;

public interface CoupleMemberRepository extends JpaRepository<CoupleMember, Long> {
    Optional<CoupleMember> findByUser(User user);

    boolean existsByUser(User user);

    List<CoupleMember> findByCouple(Couple couple);

    @Query("""
            SELECT cm
            FROM CoupleMember cm
            JOIN FETCH cm.user
            WHERE cm.couple = :couple
            """)
    List<CoupleMember> findByCoupleWithUser(
            @Param("couple") Couple couple);

    @Query("""
            SELECT cm
            FROM CoupleMember cm
            JOIN FETCH cm.user
            WHERE cm.couple.id = :coupleId
            """)
    List<CoupleMember> findByCoupleIdWithUser(
            @Param("coupleId") Long coupleId);
}
