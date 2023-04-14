package lithium.service.limit.controllers.system;

import lithium.service.Response;
import lithium.service.limit.client.PlayerLimitsClient;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status477DomainTimeSlotLimitDisabledException;
import lithium.service.limit.client.objects.LossLimitsVisibility;
import lithium.service.limit.client.objects.PlayerLimitV2Dto;
import lithium.service.limit.data.entities.PlayerLimit;
import lithium.service.limit.data.entities.PlayerTimeSlotLimit;
import lithium.service.limit.services.PlayerLimitService;
import lithium.service.limit.services.PlayerTimeSlotLimitService;
import lithium.service.limit.services.SystemPlayerLimitService;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/system/player-limit")
public class SystemPlayerLimitsController implements PlayerLimitsClient {
    @Autowired
    private PlayerLimitService service;
    @Autowired
    private PlayerTimeSlotLimitService timeSlotLimitService;
    @Autowired
    private SystemPlayerLimitService systemPlayerLimitService;
    @Autowired
    private ModelMapper mapper;

    @GetMapping("/{domainName}/set-player-time-slot-limit")
    public Response<lithium.service.limit.client.objects.PlayerTimeSlotLimit> setPlayerTimeSlotLimit(
            @RequestParam("playerGuid") String playerGuid,
            @RequestParam("playerId") Long playerId,
            @PathVariable("domainName") String domainName,
            @RequestParam("timeFromUtc") Long timeFromUtc,
            @RequestParam("timeToUtc") Long timeToUtc) throws Status477DomainTimeSlotLimitDisabledException, Status478TimeSlotLimitException {

        LithiumTokenUtil tokenUtil = timeSlotLimitService.tokenUtil();
        timeSlotLimitService.throwIfTimeSlotLimitIsDisabledForDomain(domainName);
        PlayerTimeSlotLimit timeSlotLimit = timeSlotLimitService.createPlayerLimit(playerGuid, playerId, timeFromUtc, timeToUtc, tokenUtil);
        if (timeSlotLimit == null) {
            return Response
                    .<lithium.service.limit.client.objects.PlayerTimeSlotLimit>builder()
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }
        return Response
                .<lithium.service.limit.client.objects.PlayerTimeSlotLimit>builder()
                .data(mapper.map(timeSlotLimit, lithium.service.limit.client.objects.PlayerTimeSlotLimit.class))
                .status(Response.Status.OK).build();
    }

    @GetMapping("/{domainName}/check-limits")
    public void checkLimits(
            @RequestParam("domainName") String domainName,
            @RequestParam("playerGuid") String playerGuid,
            @RequestParam("currency") String currency,
            @RequestParam(name = "betAmountCents", required = false) Long betAmountCents,
            Locale locale
    ) throws
            Status484WeeklyLossLimitReachedException,
            Status485WeeklyWinLimitReachedException,
            Status493MonthlyLossLimitReachedException,
            Status492DailyLossLimitReachedException,
            Status495MonthlyWinLimitReachedException,
            Status494DailyWinLimitReachedException, Status478TimeSlotLimitException {
        service.checkLimits(playerGuid, domainName, currency, betAmountCents, locale);
    }

    @PostMapping("/{domainName}/find-player-limit")
    public Response<PlayerLimit> findPlayerLimitPost(
            @PathVariable("domainName") String domainName,
            @RequestParam("playerGuid") String playerGuid,
            @RequestParam("granularity") Integer granularity,
            @RequestParam("type") Integer type,
            Principal principal
    ) {
        return Response.<lithium.service.limit.data.entities.PlayerLimit>builder().data(service.findPlayerLimit(playerGuid, granularity, type))
                .status(Response.Status.OK).build();
    }

    @PostMapping("/{domainName}/v2/find-limit-with-net-loss")
    public PlayerLimitV2Dto findPlayerLimitV2WithNetLoss(
        @PathVariable("domainName") String domainName,
        @RequestParam("playerGuid") String playerGuid,
        @RequestParam("granularity") Integer granularity,
        @RequestParam("type") Integer type
    ) {
        return service.findPlayerLimitV2WithNetLoss(domainName, playerGuid, granularity, type);
    }

    @GetMapping("/{domainName}/is-blocked-by-time-slot")
    public Response<Boolean> isBlockedByTimeSlot(@RequestParam("playerGuid") String playerGuid, @PathVariable("domainName") String domainName) {
        Boolean isBlocked = timeSlotLimitService.isBlockedByLimit(playerGuid, domainName, null);
        return Response.<Boolean>builder().data(isBlocked).status(Response.Status.OK).build();
    }

    @PostMapping("/set-player-age-limit")
    public Response<List<lithium.service.limit.client.objects.PlayerLimit>> setPlayerLimit(@RequestBody User user) {

        List<lithium.service.limit.client.objects.PlayerLimit> playerLimit = systemPlayerLimitService.setUserLimit(user);
        if (playerLimit == null) {
            return Response.<List<lithium.service.limit.client.objects.PlayerLimit>>builder().status(Response.Status.NOT_FOUND).build();
        }
        return Response.<List<lithium.service.limit.client.objects.PlayerLimit>>builder()
                .data(playerLimit.stream().map(pl -> mapper.map(pl, lithium.service.limit.client.objects.PlayerLimit.class)).collect(Collectors.toList()))
                .status(Response.Status.OK).build();
    }

    @PostMapping("/get-loss-limit-visibility")
    public lithium.service.limit.client.objects.User getLossLimitVisibility(
        @RequestParam("playerGuid") String playerGuid
    ) {
        return systemPlayerLimitService.getLossLimitVisibility(playerGuid);
    }
    @PostMapping("/set-loss-limit-visibility")
    public lithium.service.limit.client.objects.User setLossLimitVisibility(
        @RequestParam("playerGuid") String playerGuid,
        @RequestParam("visibility") LossLimitsVisibility visibility
    ) {
        return systemPlayerLimitService.setLossLimitVisibility(playerGuid, visibility);
    }
}
