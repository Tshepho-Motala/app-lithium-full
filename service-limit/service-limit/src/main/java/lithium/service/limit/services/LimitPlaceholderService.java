package lithium.service.limit.services;


import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.limit.data.entities.PlayerCoolOff;
import lithium.service.limit.data.entities.PlayerExclusionV2;
import lithium.service.limit.data.repositories.PlayerCoolOffRepository;
import lithium.service.limit.data.repositories.PlayerExclusionV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static lithium.service.client.objects.placeholders.PlaceholderBuilder.LIMIT_PLAYER_COOLOFF_CREATED_DATE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.LIMIT_PLAYER_COOLOFF_EXPIRED_DATE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.LIMIT_PLAYER_COOLOFF_PERIOD_IN_DAYS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.LIMIT_PLAYER_EXCLUSION_CREATED_DATE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.LIMIT_PLAYER_EXCLUSION_DURATION_DAYS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.LIMIT_PLAYER_EXCLUSION_EXPIRED_DATE;


@Service
@Slf4j
public class LimitPlaceholderService {

    @Autowired
    private PlayerCoolOffRepository coolOffRepository;
    @Autowired
    private PlayerExclusionV2Repository exclusionV2Repository;


    public Set<Placeholder> findPlaceholdersByGuid(String recipientGuid) {
        Set<Placeholder> placeholders = new HashSet<>();

        placeholders.addAll(getCoolOffPlaceholders(recipientGuid));
        placeholders.addAll(getExclusionPlaceholders(recipientGuid));

        return placeholders;
    }

    private Collection<? extends Placeholder> getCoolOffPlaceholders(String recipientGuid) {
        PlayerCoolOff playerCoolOff = coolOffRepository.findByPlayerGuid(recipientGuid);
        return resolveCoolOffPlaceholders(playerCoolOff);
    }

    private Collection<? extends Placeholder> getExclusionPlaceholders(String recipientGuid) {
        PlayerExclusionV2 exclusion = exclusionV2Repository.findByPlayerGuid(recipientGuid);
        return new HashSet<>(resolveExclusionPlaceholders(exclusion));
    }

    public Set<Placeholder> resolveCoolOffPlaceholders(PlayerCoolOff playerCoolOff) {
        Set<Placeholder> placeholders = new HashSet<>();
        if (playerCoolOff == null) return placeholders;
        placeholders.add(LIMIT_PLAYER_COOLOFF_CREATED_DATE.from(playerCoolOff.getCreatedDateDisplay()));
        placeholders.add(LIMIT_PLAYER_COOLOFF_EXPIRED_DATE.from(playerCoolOff.getExpiryDateDisplay()));
        placeholders.add(LIMIT_PLAYER_COOLOFF_PERIOD_IN_DAYS.from(playerCoolOff.getPeriodInDays()));
        return placeholders;
    }

    public Set<Placeholder> resolveExclusionPlaceholders(PlayerExclusionV2 exclusion) {
        Set<Placeholder> placeholders = new HashSet<>();
        if (exclusion != null) {
            placeholders.add(LIMIT_PLAYER_EXCLUSION_CREATED_DATE.from(exclusion.getCreatedDateDisplay()));
            placeholders.add(LIMIT_PLAYER_EXCLUSION_EXPIRED_DATE.from(exclusion.getExpiryDateDisplay()));
            if (exclusion.getCreatedDate() != null && exclusion.getExpiryDate() != null) {
                Days days = calculateDays(exclusion.getCreatedDate(), exclusion.getExpiryDate());
                placeholders.add(LIMIT_PLAYER_EXCLUSION_DURATION_DAYS.from(days.getDays()));
            }
        }
        return placeholders;
    }

    private Days calculateDays(Date createdDate, Date expiredDate) {
        DateTime dtCreated = new DateTime(createdDate).withTimeAtStartOfDay();
        DateTime dtExpiry = new DateTime(expiredDate).withTimeAtStartOfDay();
        return Days.daysBetween(dtCreated, dtExpiry);
    }


}
