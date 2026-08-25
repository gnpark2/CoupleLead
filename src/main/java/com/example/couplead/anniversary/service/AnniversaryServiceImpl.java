package com.example.couplead.anniversary.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couplead.anniversary.domain.Anniversary;
import com.example.couplead.anniversary.domain.AnniversaryType;
import com.example.couplead.anniversary.domain.HomeAnniversarySelection;
import com.example.couplead.anniversary.dto.request.CreateAnniversaryRequest;
import com.example.couplead.anniversary.dto.response.AnniversaryResponse;
import com.example.couplead.anniversary.repository.AnniversaryRepository;
import com.example.couplead.anniversary.repository.HomeAnniversarySelectionRepository;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.anniversary.dto.request.UpdateAnniversaryRequest;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.event.producer.WidgetRefreshProducer;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;
import com.example.couplead.widget.domain.WidgetPreference;
import com.example.couplead.widget.repository.WidgetPreferenceRepository;
import com.example.couplead.widget.service.WidgetCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AnniversaryServiceImpl implements AnniversaryService {
    private final UserRepository userRepository;
    private final CoupleMemberRepository coupleMemberRepository;
    private final AnniversaryRepository anniversaryRepository;
    private final WidgetRefreshProducer widgetRefreshProducer;
    private final HomeAnniversarySelectionRepository homeAnniversarySelectionRepository;
    private final WidgetCacheService widgetCacheService;
    private final WidgetPreferenceRepository widgetPreferenceRepository;

    @Override
    public AnniversaryResponse create(
            Long userId,
            CreateAnniversaryRequest request) {
        Couple couple = getCouple(userId);

        validateCustomType(
                request.type(),
                request.customTypeName());

        String customTypeName = request.type() == AnniversaryType.CUSTOM
                ? request.customTypeName().trim()
                : null;

        Anniversary anniversary = Anniversary.builder()
                .couple(couple)
                .title(request.title())
                .anniversaryDate(
                        request.anniversaryDate())
                .type(request.type())
                .repeatType(
                        request.repeatType())
                .customTypeName(
                        customTypeName)
                .build();

        Anniversary saved = anniversaryRepository.save(anniversary);

        widgetRefreshProducer.publish(couple.getId(), "ANNIVERSARY_CREATED");

        return AnniversaryResponse.from(saved);
    }

    @Override
    public AnniversaryResponse update(
            Long userId,
            Long anniversaryId,
            UpdateAnniversaryRequest request) {
        Couple couple = getCouple(userId);

        validateCustomType(
                request.type(),
                request.customTypeName());

        String customTypeName = request.type() == AnniversaryType.CUSTOM
                ? request.customTypeName().trim()
                : null;

        Anniversary anniversary = anniversaryRepository
                .findByIdAndCouple(anniversaryId, couple)
                .orElseThrow();

        anniversary.update(
                request.title(),
                request.anniversaryDate(),
                request.type(),
                request.repeatType(),
                customTypeName);

        widgetRefreshProducer.publish(
                couple.getId(),
                "ANNIVERSARY_UPDATED");

        return AnniversaryResponse.from(
                anniversary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnniversaryResponse> getAll(
            Long userId) {
        Couple couple = getCouple(userId);

        return anniversaryRepository
                .findByCoupleOrderByAnniversaryDateAsc(couple)
                .stream()
                .map(AnniversaryResponse::from)
                .toList();
    }

    @Transactional
    public void delete(
            Long userId,
            Long anniversaryId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow();

        CoupleMember member = coupleMemberRepository
                .findByUser(user)
                .orElseThrow();

        Couple couple = member.getCouple();

        Anniversary anniversary = anniversaryRepository
                .findByIdAndCouple(
                        anniversaryId,
                        couple)
                .orElseThrow();

        /*
         * 1. Home 표시 기념일에서 제거
         */
        homeAnniversarySelectionRepository
                .deleteAllByAnniversary(
                        anniversary);

        /*
         * 2. WidgetPreference에서
         * 해당 기념일 참조 제거
         */
        widgetPreferenceRepository
                .clearSelectedAnniversary(
                        anniversary);

        /*
         * 3. 실제 기념일 삭제
         */
        anniversaryRepository.delete(
                anniversary);

        /*
         * 4. 위젯 캐시 제거
         */
        widgetCacheService
                .invalidateByCouple(
                        couple.getId());

        /*
         * 5. 삭제 이벤트 발행
         */
        widgetRefreshProducer.publish(
                couple.getId(),
                "ANNIVERSARY_DELETED");
    }

    private void validateCustomType(
            AnniversaryType type,
            String customTypeName) {
        if (type == AnniversaryType.CUSTOM) {
            if (customTypeName == null ||
                    customTypeName.isBlank()) {

                throw new IllegalArgumentException(
                        "직접 지정 종류를 입력해주세요.");
            }
        }
    }

    private Couple getCouple(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        CoupleMember member = coupleMemberRepository.findByUser(user).orElseThrow();

        return member.getCouple();
    }

    @Transactional(readOnly = true)
    public List<AnniversaryResponse> getHomeAnniversaries(
            Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow();

        return homeAnniversarySelectionRepository
                .findAllByUserOrderByDisplayOrderAsc(user)
                .stream()
                .map(HomeAnniversarySelection::getAnniversary)
                .map(AnniversaryResponse::from)
                .toList();
    }

    @Transactional
    public List<AnniversaryResponse> updateHomeAnniversaries(
            Long userId,
            List<Long> anniversaryIds) {
        User user = userRepository
                .findById(userId)
                .orElseThrow();

        CoupleMember member = coupleMemberRepository
                .findByUser(user)
                .orElseThrow();

        Couple couple = member.getCouple();

        /*
         * 중복 ID 제거
         */
        List<Long> distinctIds = anniversaryIds
                .stream()
                .distinct()
                .toList();

        List<Anniversary> anniversaries = anniversaryRepository
                .findAllByIdInAndCouple(
                        distinctIds,
                        couple);

        /*
         * 요청한 개수와 실제 조회 개수가 다르다면
         * 다른 Couple 기념일이 섞였거나
         * 존재하지 않는 ID
         */
        if (anniversaries.size() != distinctIds.size()) {
            throw new IllegalArgumentException(
                    "유효하지 않은 기념일이 포함되어 있습니다.");
        }

        /*
         * 기존 Home 선택 제거
         */
        homeAnniversarySelectionRepository
                .deleteAllByUser(user);

        /*
         * 요청 ID 순서 유지
         */
        Map<Long, Anniversary> anniversaryMap = anniversaries
                .stream()
                .collect(
                        java.util.stream.Collectors
                                .toMap(
                                        Anniversary::getId,
                                        anniversary -> anniversary));

        for (int i = 0; i < distinctIds.size(); i++) {

            Long anniversaryId = distinctIds.get(i);

            Anniversary anniversary = anniversaryMap.get(
                    anniversaryId);

            HomeAnniversarySelection selection = HomeAnniversarySelection
                    .builder()
                    .user(user)
                    .anniversary(
                            anniversary)
                    .displayOrder(i)
                    .build();

            homeAnniversarySelectionRepository
                    .save(selection);
        }

        return distinctIds
                .stream()
                .map(anniversaryMap::get)
                .map(AnniversaryResponse::from)
                .toList();
    }
}
