package com.example.couplead.anniversary.repository;

import com.example.couplead.anniversary.domain.Anniversary;
import com.example.couplead.anniversary.domain.HomeAnniversarySelection;
import com.example.couplead.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeAnniversarySelectionRepository
        extends JpaRepository<HomeAnniversarySelection, Long> {

    List<HomeAnniversarySelection> findAllByUserOrderByDisplayOrderAsc(
            User user);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
                delete from HomeAnniversarySelection h
                where h.user = :user
            """)
    void deleteAllByUser(
            @Param("user") User user);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
                delete from HomeAnniversarySelection h
                where h.anniversary = :anniversary
            """)
    void deleteAllByAnniversary(
            @Param("anniversary") Anniversary anniversary);
}