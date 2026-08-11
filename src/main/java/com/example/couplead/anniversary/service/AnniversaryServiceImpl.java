package com.example.couplead.anniversary.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couplead.anniversary.domain.Anniversary;
import com.example.couplead.anniversary.dto.request.CreateAnniversaryRequest;
import com.example.couplead.anniversary.dto.response.AnniversaryResponse;
import com.example.couplead.anniversary.repository.AnniversaryRepository;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.anniversary.dto.request.UpdateAnniversaryRequest;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.event.producer.WidgetRefreshProducer;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AnniversaryServiceImpl implements AnniversaryService {
    private final UserRepository userRepository;
    private final CoupleMemberRepository coupleMemberRepository;
    private final AnniversaryRepository anniversaryRepository;
    private final WidgetRefreshProducer widgetRefreshProducer;

    @Override
    public AnniversaryResponse create(
        Long userId,
        CreateAnniversaryRequest request
    ) {
        Couple couple = getCouple(userId);

        Anniversary anniversary = Anniversary.builder()
            .couple(couple)
            .title(request.title())
            .anniversaryDate(request.anniversaryDate())
            .type(request.type())
            .repeatType(request.repeatType())
            .build();

        Anniversary saved = anniversaryRepository.save(anniversary);

        widgetRefreshProducer.publish(couple.getId(), "ANNIVERSARY_CREATED");

        return AnniversaryResponse.from(saved);
    }

    @Override
    public AnniversaryResponse update(
        Long userId,
        Long anniversaryId,
        UpdateAnniversaryRequest request
    ) {
        Couple couple = getCouple(userId);

        Anniversary anniversary =
            anniversaryRepository
                .findByIdAndCouple(anniversaryId, couple)
                .orElseThrow();

        anniversary.update(
            request.title(),
            request.anniversaryDate(),
            request.type(),
            request.repeatType()
        );

        widgetRefreshProducer.publish(
            couple.getId(),
            "ANNIVERSARY_UPDATED"
        );

        return AnniversaryResponse.from(
            anniversary
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnniversaryResponse> getAll(
        Long userId
    ) {
        Couple couple = getCouple(userId);

        return anniversaryRepository
            .findByCoupleOrderByAnniversaryDateAsc(couple)
            .stream()
            .map(AnniversaryResponse::from)
            .toList();
    }

    @Override
    public void delete(
        Long userId,
        Long anniversaryId
    ) {
        Couple couple = getCouple(userId);

        Anniversary anniversary = anniversaryRepository.findByIdAndCouple(
            anniversaryId,
            couple
        ).orElseThrow();

        anniversaryRepository.delete(anniversary);

        widgetRefreshProducer.publish(couple.getId(), "ANNIVERSARY_DELETED");
    }

    private Couple getCouple(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        CoupleMember member = coupleMemberRepository.findByUser(user).orElseThrow();

        return member.getCouple();
    }


}
