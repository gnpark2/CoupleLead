package com.example.couplead.widget.service;

import com.example.couplead.anniversary.domain.Anniversary;
import com.example.couplead.anniversary.repository.AnniversaryRepository;
import com.example.couplead.chat.domain.Message;
import com.example.couplead.chat.repository.MessageRepository;
import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.couple.repository.CoupleRepository;
import com.example.couplead.presence.service.PresenceService;
import com.example.couplead.typing.service.TypingService;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;
import com.example.couplead.weather.dto.response.WeatherResponse;
import com.example.couplead.weather.service.WeatherService;
import com.example.couplead.widget.domain.WidgetPreference;
import com.example.couplead.widget.dto.response.CoupleWidgetResponse;
import com.example.couplead.widget.repository.WidgetPreferenceRepository;
import com.example.couplead.widget.dto.response.WidgetPersonResponse;
import com.example.couplead.anniversary.util.AnniversaryDateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WidgetCacheService {

    private static final String PREFIX = "widget:user:";

    private final StringRedisTemplate redisTemplate;
    private final CoupleRepository coupleRepository;
    private final CoupleMemberRepository coupleMemberRepository;
    private final MessageRepository messageRepository;
    private final PresenceService presenceService;
    private final WeatherService weatherService;
    private final UserRepository userRepository;
    private final AnniversaryRepository anniversaryRepository;
    private final WidgetPreferenceRepository widgetPreferenceRepository;
    private final TypingService typingService;

    @Transactional(readOnly = true)
    public CoupleWidgetResponse getCache(Long coupleId, Long myUserId) {

        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        String key = PREFIX + myUserId;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            updateCache(coupleId, myUserId);
        }

        Map<String, String> data = hash.entries(key);

        Long partnerId = parseLong(data.get("partnerId"));
        boolean partnerTyping = partnerId != null && typingService.isTyping(partnerId);
        long unreadCount = messageRepository.countByCoupleIdAndSenderIdNotAndReadAtIsNull(coupleId, myUserId);

        log.info(
                "Widget unreadCount: userId={}, coupleId={}, count={}",
                myUserId,
                coupleId,
                unreadCount);

        WidgetPersonResponse meResponse = new WidgetPersonResponse(
                parseLong(
                        data.get("myId")),
                data.get("myNickname"),
                data.get("myCity"),
                data.get("myTimezone"),
                data.get("myLocalTime"),
                parseDouble(
                        data.get(
                                "myTemperature")),
                data.get(
                        "myWeatherCondition"),
                data.get(
                        "myWeatherIcon"));

        WidgetPersonResponse partnerResponse = new WidgetPersonResponse(
                partnerId,
                data.get(
                        "partnerNickname"),
                data.get(
                        "partnerCity"),
                data.get(
                        "partnerTimezone"),
                data.get(
                        "partnerLocalTime"),
                parseDouble(
                        data.get(
                                "partnerTemperature")),
                data.get(
                        "partnerWeatherCondition"),
                data.get(
                        "partnerWeatherIcon"));

        return new CoupleWidgetResponse(
                coupleId,
                partnerId,
                parseInteger(
                        data.get(
                                "daysTogether")),
                parseLong(
                        data.get(
                                "anniversaryId")),
                data.get(
                        "anniversaryTitle"),
                data.get(
                        "anniversaryDate"),
                parseInteger(
                        data.get(
                                "anniversaryDDay")),
                data.get(
                        "partnerNickname"),
                data.get(
                        "partnerProfileImage"),
                Boolean.parseBoolean(
                        data.getOrDefault(
                                "partnerOnline",
                                "false")),
                partnerTyping,
                data.get(
                        "partnerLastSeen"),
                unreadCount,
                data.get(
                        "partnerCity"),
                data.get(
                        "partnerTimezone"),
                data.get(
                        "partnerLocalTime"),
                parseDouble(
                        data.get(
                                "temperature")),
                data.get(
                        "weatherCondition"),
                data.get(
                        "weatherIcon"),
                data.get(
                        "lastMessageAt"),
                meResponse,
                partnerResponse);
    }

    @Transactional
    public void updateCache(Long coupleId, Long myUserId) {
        Couple couple = coupleRepository
                .findById(coupleId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.COUPLE_NOT_FOUND));

        User me = userRepository
                .findById(myUserId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.USER_NOT_FOUND));
        User partner = getPartner(couple, myUserId);

        // Long unreadCount = messageRepository
        // .countByCoupleIdAndSenderIdNotAndReadAtIsNull(coupleId, myUserId);

        WidgetPreference preference = widgetPreferenceRepository
                .findByUser(me)
                .orElse(null);

        Anniversary anniversary = null;

        if (preference != null) {
            anniversary = preference.getSelectedAnniversary();
        }

        long daysTogether = calculateDaysTogether(couple);

        AnniversaryWidgetData anniversaryData = calculateAnniversary(anniversary);

        WeatherResponse myWeather = weatherService.getCurrentWeather(
                me.getLatitude(),
                me.getLongitude());

        WeatherResponse partnerWeather = weatherService.getCurrentWeather(
                partner.getLatitude(),
                partner.getLongitude());

        Message lastMessage = messageRepository
                .findTopByCoupleIdOrderBySentAtDesc(coupleId)
                .orElse(null);

        String lastMessageAt = lastMessage == null
                ? ""
                : lastMessage.getSentAt().toString();

        Map<String, String> cache = new HashMap<>();

        cache.put("myId", me.getId().toString());
        cache.put("myNickname", value(me.getNickname()));
        cache.put("myCity", value(me.getCity()));
        cache.put("myTimezone", value(me.getTimezone()));
        cache.put("myLocalTime", value(getLocalTime(me.getTimezone())));
        cache.put("myTemperature", myWeather == null || myWeather.temperature() == null
                ? ""
                : myWeather.temperature().toString());
        cache.put("myWeatherCondition", myWeather == null
                ? ""
                : value(myWeather.condition()));
        cache.put("myWeatherIcon", myWeather == null
                ? ""
                : value(myWeather.icon()));
        cache.put("daysTogether", String.valueOf(daysTogether));
        cache.put("anniversaryId", anniversaryData.id());
        cache.put("anniversaryTitle", anniversaryData.title());
        cache.put("anniversaryDate", anniversaryData.date());
        cache.put("anniversaryDDay", anniversaryData.dDay());
        cache.put("partnerId", partner.getId().toString());
        cache.put("partnerNickname", value(partner.getNickname()));
        cache.put("partnerProfileImage", partner.getProfileImage() != null ? partner.getProfileImage() : "");
        cache.put("partnerOnline", String.valueOf(presenceService.isOnline(partner.getId())));
        // cache.put("partnerTyping", String.valueOf(partnerTying));
        cache.put("partnerLastSeen", value(presenceService.getLastSeen(partner.getId())));
        // cache.put("unreadCount", String.valueOf(unreadCount));
        cache.put("partnerCity", value(partner.getCity()));
        cache.put("partnerTimezone", value(partner.getTimezone()));
        cache.put("partnerLocalTime", value(getLocalTime(partner.getTimezone())));
        cache.put("partnerTemperature", partnerWeather == null || partnerWeather.temperature() == null
                ? ""
                : partnerWeather.temperature().toString());
        cache.put("partnerWeatherCondition", partnerWeather == null
                ? ""
                : value(partnerWeather.condition()));
        cache.put("partnerWeatherIcon", partnerWeather == null
                ? ""
                : value(partnerWeather.icon()));
        cache.put("lastMessageAt", value(lastMessageAt));
        cache.put("updatedAt", LocalDateTime.now().toString());

        redisTemplate.opsForHash().putAll(PREFIX + myUserId, cache);
    }

    @Transactional
    public void selectAnniversary(
            Long userId,
            Long anniversaryId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.USER_NOT_FOUND));

        CoupleMember member = coupleMemberRepository
                .findByUser(user)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.COUPLE_NOT_FOUND));

        Couple couple = member.getCouple();

        Anniversary anniversary = anniversaryRepository.findByIdAndCouple(
                anniversaryId,
                couple).orElseThrow();

        WidgetPreference preference = widgetPreferenceRepository
                .findByUser(user)
                .orElseGet(() -> WidgetPreference.builder()
                        .user(user)
                        .build());

        preference.selectAnniversary(anniversary);
        widgetPreferenceRepository.save(preference);

        redisTemplate.delete(PREFIX + userId);
    }

    @Transactional(readOnly = true)
    public void invalidateByCouple(Long coupleId) {
        Couple couple = coupleRepository
                .findById(coupleId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.COUPLE_NOT_FOUND));

        coupleMemberRepository.findByCoupleWithUser(couple)
                .stream()
                .map(CoupleMember::getUser)
                .map(User::getId)
                .forEach(userId -> redisTemplate.delete(PREFIX + userId));
    }

    public void invalidateByUser(Long userId) {
        redisTemplate.delete(PREFIX + userId);
    }

    private User getPartner(Couple couple, Long myUserId) {

        return coupleMemberRepository
                .findByCoupleWithUser(couple)
                .stream()
                .map(CoupleMember::getUser)
                .filter(user -> !user.getId().equals(myUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("상대방을 찾을 수 없습니다."));
    }

    private long calculateDaysTogether(Couple couple) {
        if (couple.getConnectedAt() == null) {
            return 0;
        }

        return ChronoUnit.DAYS.between(
                couple.getConnectedAt(),
                LocalDate.now());
    }

    private AnniversaryWidgetData calculateAnniversary(
            Anniversary anniversary) {
        if (anniversary == null) {
            return new AnniversaryWidgetData(
                    "",
                    "",
                    "",
                    "");
        }

        LocalDate date = anniversary.getAnniversaryDate();

        long dDay = AnniversaryDateUtils.calculateDDay(
                date,
                anniversary.getRepeatType());

        return new AnniversaryWidgetData(
                anniversary
                        .getId()
                        .toString(),
                value(
                        anniversary.getTitle()),
                date.toString(),
                String.valueOf(
                        dDay));
    }

    private String getLocalTime(String timezone) {

        if (timezone == null || timezone.isBlank()) {
            return "";
        }

        return LocalTime.now(ZoneId.of(timezone))
                .withSecond(0)
                .withNano(0)
                .toString();
    }

    private Integer parseInteger(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return Integer.parseInt(value);
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Long.parseLong(value);
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Double.parseDouble(value);
    }

    private String value(String value) {

        return value == null ? "" : value;
    }

    private record AnniversaryWidgetData(
            String id,
            String title,
            String date,
            String dDay) {

    }
}
