package com.example.couplead.user.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.couplead.user.service.ProfileImageStorageService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProfileImageReplacedListener {

    private final ProfileImageStorageService profileImageStorageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(
            ProfileImageReplacedEvent event) {
        profileImageStorageService.delete(
                event.oldProfileImage());
    }
}