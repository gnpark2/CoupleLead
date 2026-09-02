package com.example.couplead.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.couplead.domain.entity.DeviceInstallation;

import java.util.List;
import java.util.Optional;

public interface DeviceInstallationRepository
    extends JpaRepository<
        DeviceInstallation,
        Long
    > {

    Optional<DeviceInstallation>
        findByFid(
            String fid
        );

    List<DeviceInstallation>
        findAllByUserId(
            Long userId
        );

    void deleteByFid(
        String fid
    );
}