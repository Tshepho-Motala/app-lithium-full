package lithium.service.limit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lithium.exceptions.Status414UserNotFoundException;
import lithium.service.client.objects.Granularity;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.PlayTimeLimitFE;
import lithium.service.limit.client.objects.PlayerLimitFE;
import lithium.service.limit.client.objects.PlayerLimitSummaryFE;
import lithium.service.limit.client.objects.PlayerTimeSlotLimitResponse;
import lithium.service.limit.data.entities.PlayerTimeSlotLimit;
import lithium.service.user.client.objects.PlayTimeLimitPubSubDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class PlayerLimitsSummaryService {

    @Autowired
    private DepositLimitService depositLimitService;

    @Autowired
    private BalanceLimitService balanceLimitService;

    @Autowired
    private PlayerTimeSlotLimitService playerTimeSlotLimitService;

    @Autowired LimitInternalSystemService limitInternalSystemService;

    public PlayerLimitSummaryFE findPlayerLimitSummary(String guid, Locale locale)
        throws Status500LimitInternalSystemClientException {
        PlayerLimitSummaryFE playerLimitSummaryFE = new PlayerLimitSummaryFE();
        List<PlayerLimitFE> depositLimits = depositLimitService.findAllFE(guid, locale);
        if (!ObjectUtils.isEmpty(depositLimits)) {
            playerLimitSummaryFE.setDepositLimits(depositLimits);
        }
        List<PlayerLimitFE> playerBalanceLimits = balanceLimitService.findAllFE(guid);
        if (!ObjectUtils.isEmpty(playerBalanceLimits)) {
            playerLimitSummaryFE.setBalanceLimits(playerBalanceLimits);
        }

        PlayerTimeSlotLimit playerTimeSlotLimit = playerTimeSlotLimitService.findPlayerLimit(guid);
        if (!ObjectUtils.isEmpty(playerTimeSlotLimit)) {
            PlayerTimeSlotLimitResponse playerTimeSlotLimitResponse = playerTimeSlotLimitService.convertLimitToResponse(
                playerTimeSlotLimit);
            playerLimitSummaryFE.setTimeSlotLimit(playerTimeSlotLimitResponse);
        }
        List<PlayTimeLimitPubSubDTO> playerTimeLimitResponse = new ArrayList<>();
        try {
            playerTimeLimitResponse = limitInternalSystemService.getPlayTimeTimeForUserLimits(guid);
        } catch (Status414UserNotFoundException e) {
            log.error("can not get a current PlayTime Limit" + e.getMessage(), e);
        }
        if (!playerTimeLimitResponse.isEmpty()) {
            playerLimitSummaryFE.setPlayTimeLimits(
                convertToPlayerLimitFEs(playerTimeLimitResponse));
        }

        return playerLimitSummaryFE;
    }

    private List<PlayTimeLimitFE> convertToPlayerLimitFEs(List<PlayTimeLimitPubSubDTO> playTimeLimitPubSubDTOS){
        List<PlayTimeLimitFE> playTimeLimitFEs = new ArrayList<>();
        if (!playTimeLimitPubSubDTOS.isEmpty()){
            playTimeLimitPubSubDTOS.forEach(playTimeLimitPubSubDTO -> playTimeLimitFEs.add(convertToPlayTimeLimitFE(playTimeLimitPubSubDTO)));
        }
        return playTimeLimitFEs;
    }

    private PlayTimeLimitFE convertToPlayTimeLimitFE(PlayTimeLimitPubSubDTO playTimeLimitPubSubDTO){
        return PlayTimeLimitFE.builder()
                .playTimeLimit(playTimeLimitPubSubDTO.getPlayTimeLimit())
                .playTimeLimitSeconds(playTimeLimitPubSubDTO.getPlayTimeLimitSeconds())
                .granularity(Objects.requireNonNull(Granularity.fromGranularity(playTimeLimitPubSubDTO.getGranularity())).type())
                .playTimeLimitRemainingSeconds(playTimeLimitPubSubDTO.getPlayTimeLimitRemainingSeconds())
                .type(Objects.requireNonNull(playTimeLimitPubSubDTO.getType()))
                .build();
    }
}
