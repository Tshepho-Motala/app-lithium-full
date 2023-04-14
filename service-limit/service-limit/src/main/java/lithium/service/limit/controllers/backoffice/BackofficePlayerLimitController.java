package lithium.service.limit.controllers.backoffice;

import java.security.Principal;

import lithium.service.Response;
import lithium.service.limit.client.exceptions.Status477DomainTimeSlotLimitDisabledException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.objects.DomainLimit;
import lithium.service.limit.client.objects.LossLimitsVisibility;
import lithium.service.limit.client.objects.PlayerLimit;
import lithium.service.limit.data.entities.PlayerTimeSlotLimit;
import lithium.service.limit.services.PlayerLimitService;
import lithium.service.limit.services.PlayerTimeSlotLimitService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/player-limit/v1/{domainName}")
public class BackofficePlayerLimitController {
    @Autowired
    private PlayerLimitService playerLimitService;
    @Autowired
    private PlayerTimeSlotLimitService playerTimeSlotLimitService;
    @Autowired
    private ModelMapper mapper;

    @GetMapping("/net-loss-to-house")
    public Response<Long> netLossToHouse(@RequestParam("domainName") String domainName,
                                         @RequestParam("playerGuid") String playerGuid,
                                         @RequestParam("currency") String currency,
                                         @RequestParam("granularity") Integer granularity) {
        Long netLoss = playerLimitService.netLossToHouse(domainName, playerGuid, currency, granularity);
        if (netLoss == null) return Response.<Long>builder().status(Response.Status.INTERNAL_SERVER_ERROR).build();
        return Response.<Long>builder().status(Response.Status.OK).data(netLoss).build();
    }

    @GetMapping("/find-domain-limit")
    public Response<lithium.service.limit.data.entities.DomainLimit> findDomainLimit(
            @PathVariable("domainName") String domainName,
            @RequestParam("granularity") Integer granularity,
            @RequestParam("type") Integer type, Principal principal) {
        return Response.<lithium.service.limit.data.entities.DomainLimit>builder()
                .data(playerLimitService.findDomainLimit(domainName, granularity, type))
                .status(Response.Status.OK).build();
    }

    @GetMapping("/set-domain-limit")
    public Response<DomainLimit> setDomainLimit(@PathVariable("domainName") String domainName,
                                                @RequestParam("granularity") Integer granularity,
                                                @RequestParam("amount") Long amount,
                                                @RequestParam("type") Integer type,
                                                Principal principal)
            throws Exception {
        lithium.service.limit.data.entities.DomainLimit domainLimit = playerLimitService.saveDomainLimit(domainName, granularity,
                amount, type, principal);
        if (domainLimit == null) {
            return Response.<DomainLimit>builder().status(Response.Status.NOT_FOUND).build();
        }
        return Response.<DomainLimit>builder().data(mapper.map(domainLimit, DomainLimit.class)).status(Response.Status.OK).build();
    }

    @DeleteMapping("/remove-domain-limit")
    public Response<Boolean> removeDomainLimit(@PathVariable("domainName") String domainName,
                                               @RequestParam("granularity") Integer granularity,
                                               @RequestParam("type") Integer type,
                                               Principal principal)
            throws Exception {
        playerLimitService.removeDomainLimit(domainName, granularity, type, principal);
        return Response.<Boolean>builder().data(true).status(Response.Status.OK).build();
    }

    @GetMapping("/find-player-limit")
    public Response<lithium.service.limit.data.entities.PlayerLimit> findPlayerLimit(
            @RequestParam("playerGuid") String playerGuid,
            @PathVariable("domainName") String domainName,
            @RequestParam("granularity") Integer granularity,
            @RequestParam("type") Integer type, Principal principal) {
        return Response.<lithium.service.limit.data.entities.PlayerLimit>builder().data(playerLimitService.findPlayerLimit(playerGuid, granularity, type))
                .status(Response.Status.OK).build();
    }

    @PostMapping("/find-player-limit")
    public Response<lithium.service.limit.data.entities.PlayerLimit> findPlayerLimitPost(
            @PathVariable("domainName") String domainName,
            @RequestParam("playerGuid") String playerGuid,
            @RequestParam("granularity") Integer granularity,
            @RequestParam("type") Integer type,
            Principal principal
    ) {
        return Response.<lithium.service.limit.data.entities.PlayerLimit>builder().data(playerLimitService.findPlayerLimit(playerGuid, granularity, type))
                .status(Response.Status.OK).build();
    }

    @GetMapping("/set-player-limit")
    public Response<PlayerLimit> setPlayerLimit(@RequestParam("playerGuid") String playerGuid,
                                                @RequestParam("playerId") Long playerId,
                                                @PathVariable("domainName") String domainName,
                                                @RequestParam("granularity") Integer granularity,
                                                @RequestParam("amount") Long amount,
                                                @RequestParam("type") Integer type,
                                                LithiumTokenUtil tokenUtil) throws Exception {
        lithium.service.limit.data.entities.PlayerLimit playerLimit = playerLimitService.savePlayerLimit(playerGuid, playerId,
                granularity, amount, type, domainName, tokenUtil, false, tokenUtil.guid());
        if (playerLimit == null) {
            return Response.<PlayerLimit>builder().status(Response.Status.NOT_FOUND).build();
        }
        return Response.<PlayerLimit>builder().data(mapper.map(playerLimit, PlayerLimit.class)).status(Response.Status.OK).build();
    }
    @DeleteMapping("/remove-player-limit")
    public Response<Boolean> removePlayerLimit(@RequestParam("playerGuid") String playerGuid,
                                               @RequestParam("playerId") Long playerId,
                                               @PathVariable("domainName") String domainName,
                                               @RequestParam("granularity") Integer granularity,
                                               @RequestParam("type") Integer type,
                                               LithiumTokenUtil tokenUtil) throws Exception {
        playerLimitService.removePlayerLimit(playerGuid, playerId, granularity, type, tokenUtil);
        return Response.<Boolean>builder().data(true).status(Response.Status.OK).build();
    }

    @GetMapping("/get-player-time-slot-limit")
    public Response<PlayerTimeSlotLimit> findPlayerTimeSlotLimit(
            @PathVariable("domainName") String domainName,
            @RequestParam("playerGuid") String playerGuid) throws Status477DomainTimeSlotLimitDisabledException {

//      Here we don't have to check if domain is enabled or not as this will cause no harm to the system.
        PlayerTimeSlotLimit timeSlotLimit = playerTimeSlotLimitService.findPlayerLimit(playerGuid);
        return Response
                .<PlayerTimeSlotLimit>builder()
                .data(timeSlotLimit)
                .status(Response.Status.OK)
                .build();
    }

    @GetMapping("/set-player-time-slot-limit")
    public Response<PlayerTimeSlotLimit> setPlayerTimeSlotLimit(
            @RequestParam("playerGuid") String playerGuid,
            @RequestParam("playerId") Long playerId,
            @PathVariable("domainName") String domainName,
            @RequestParam("timeFromUtc") Long timeFromUtc,
            @RequestParam("timeToUtc") Long timeToUtc,
                   LithiumTokenUtil tokenUtil) throws Status477DomainTimeSlotLimitDisabledException, Status478TimeSlotLimitException {

        playerTimeSlotLimitService.throwIfTimeSlotLimitIsDisabledForDomain(domainName);
        PlayerTimeSlotLimit timeSlotLimit = playerTimeSlotLimitService.createPlayerLimit(playerGuid, playerId, timeFromUtc, timeToUtc, tokenUtil);
        if (timeSlotLimit == null) {
            return Response
                    .<PlayerTimeSlotLimit>builder()
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }
        return Response
                .<PlayerTimeSlotLimit>builder()
                .data(mapper.map(timeSlotLimit, PlayerTimeSlotLimit.class))
                .status(Response.Status.OK).build();
    }

    @DeleteMapping("/remove-player-time-slot-limit")
    public Response<Boolean> removePlayerTimeSlotLimit(
            @PathVariable("domainName") String domainName,
            @RequestParam("playerGuid") String playerGuid,
            @RequestParam("playerId") Long playerId,
            LithiumTokenUtil tokenUtil) throws Status477DomainTimeSlotLimitDisabledException {

        playerTimeSlotLimitService.throwIfTimeSlotLimitIsDisabledForDomain(domainName);
        playerTimeSlotLimitService.removePlayerLimit(playerGuid, playerId, tokenUtil);
        return Response.<Boolean>builder().data(true).status(Response.Status.OK).build();
    }

    @GetMapping("/check-player-time-slot-limit")
    public Response checkPlayerTimeSlotLimit(
            @PathVariable("domainName") String domainName,
            @RequestParam("playerGuid") String playerGuid) throws Status478TimeSlotLimitException {
        playerTimeSlotLimitService.checkLimits(playerGuid, domainName, null);

        return Response
                .builder()
                .data(false)
                .status(Response.Status.OK)
                .build();
    }

    @GetMapping("/get-loss-limit-visibility")
    public Response<lithium.service.limit.client.objects.User> getLossLimitVisibility(
        @RequestParam("playerGuid") String playerGuid
    ) {
        return Response.<lithium.service.limit.client.objects.User>builder().data(playerLimitService.getLossLimitVisibility(playerGuid)).status(Response.Status.OK).build();
    }

    @PostMapping("/set-loss-limit-visibility")
    public Response<lithium.service.limit.client.objects.User> setLossLimitVisibility(
        @RequestParam("playerGuid") String playerGuid,
        @RequestParam("visibility") LossLimitsVisibility visibility
    ) {
        return Response.<lithium.service.limit.client.objects.User>builder().data(playerLimitService.setLossLimitVisibility(playerGuid, visibility)).status(Response.Status.OK).build();
    }
}
