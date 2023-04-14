package lithium.service.limit.api.controllers;

import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status403AccessDeniedException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status403PlayerRestrictionDeniedException;
import lithium.service.limit.client.exceptions.Status409PlayerRestrictionConflictException;
import lithium.service.limit.client.exceptions.Status422PlayerRestrictionExclusionException;
import lithium.service.limit.client.exceptions.Status477DomainTimeSlotLimitDisabledException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.client.objects.PlayerLimit;
import lithium.service.limit.client.objects.PlayerRestrictionRequestFE;
import lithium.service.limit.client.objects.PlayerTimeSlotLimitRequest;
import lithium.service.limit.client.objects.PlayerTimeSlotLimitResponse;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.UserRestrictionSet;
import lithium.service.limit.enums.SystemRestriction;
import lithium.service.limit.services.PlayerLimitService;
import lithium.service.limit.services.PlayerTimeSlotLimitService;
import lithium.service.limit.services.RestrictionService;
import lithium.service.limit.services.UserRestrictionService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@RestController
@RequestMapping("/frontend/player-limit/v1")
public class FrontendPlayerLimitController {
    @Autowired
    private PlayerLimitService service;

    @Autowired
    private UserApiInternalClientService userApiInternalClientService;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PlayerTimeSlotLimitService playerTimeSlotLimitService;

    @Autowired
    private UserRestrictionService userRestrictionService;

    @Autowired
    private RestrictionService restrictionService;

    @GetMapping("/find-player-limit")
    public Response<lithium.service.limit.data.entities.PlayerLimit> findPlayerLimit(
            @RequestParam("granularity") Integer granularity,
            @RequestParam("type") Integer type,
            LithiumTokenUtil tokenUtil) {
        return Response.<lithium.service.limit.data.entities.PlayerLimit>builder().data(service.findPlayerLimit(tokenUtil.guid(), granularity, type))
                .status(Response.Status.OK).build();
    }

    @PostMapping("/find-player-limit")
    public Response<lithium.service.limit.data.entities.PlayerLimit> findPlayerLimitPost(
            @RequestParam("granularity") Integer granularity,
            @RequestParam("type") Integer type,
            LithiumTokenUtil tokenUtil
    ) {
        return Response.<lithium.service.limit.data.entities.PlayerLimit>builder().data(service.findPlayerLimit(tokenUtil.guid(), granularity, type))
                .status(Response.Status.OK).build();
    }

    @GetMapping("/set-player-limit")
    public Response<PlayerLimit> setPlayerLimit(@RequestParam("granularity") Integer granularity,
                                                @RequestParam("amount") Long amount,
                                                @RequestParam("type") Integer type,
                                                LithiumTokenUtil tokenUtil)
            throws Exception, UserNotFoundException, UserClientServiceFactoryException {
        User user = userApiInternalClientService.getUserByGuid(tokenUtil.guid());
        lithium.service.limit.data.entities.PlayerLimit playerLimit = service.savePlayerLimit(tokenUtil.guid(), user.getId(),
                granularity, amount, type, tokenUtil.domainName(), tokenUtil, false, tokenUtil.guid());
        if (playerLimit == null) {
            return Response.<PlayerLimit>builder().status(Response.Status.NOT_FOUND).build();
        }
        return Response.<lithium.service.limit.client.objects.PlayerLimit>builder().data(mapper.map(playerLimit, PlayerLimit.class)).status(Response.Status.OK).build();
    }

    /**
     * @deprecated LSPLAT-6853 Currently being kept for backwards compatibility but should be removed in future
     */
    @Deprecated(since="3.07", forRemoval=true)
    @GetMapping("/get-player-time-slot-limit")
    public PlayerTimeSlotLimitResponse findPlayerTimeSlotLimit(LithiumTokenUtil tokenUtil)
            throws Status477DomainTimeSlotLimitDisabledException {
        // Check to see if we can action this controller
        playerTimeSlotLimitService.throwIfTimeSlotLimitIsDisabledForDomain(tokenUtil.domainName());

        // Fetch the player limit
        lithium.service.limit.data.entities.PlayerTimeSlotLimit timeSlotLimit = playerTimeSlotLimitService.findPlayerLimit(tokenUtil.guid());

        // Convert the player limit to a valid response
        return playerTimeSlotLimitService.convertLimitToResponse(timeSlotLimit);
    }

    /**
     * @deprecated LSPLAT-6853 Currently being kept for backwards compatibility but should be removed in future
     */
    @Deprecated(since="3.07", forRemoval=true)
    @RequestMapping(
            value = "/set-player-time-slot-limit",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public PlayerTimeSlotLimitResponse setPlayerTimeSlotLimit(PlayerTimeSlotLimitRequest request, LithiumTokenUtil tokenUtil)
            throws UserNotFoundException,
            UserClientServiceFactoryException,
            Status477DomainTimeSlotLimitDisabledException,
            Status478TimeSlotLimitException {
        // Check to see if we can action this controller
        playerTimeSlotLimitService.throwIfTimeSlotLimitIsDisabledForDomain(tokenUtil.domainName());

        // Fetch the current user
        User user = userApiInternalClientService.getUserByGuid(tokenUtil.guid());

        // Convert the timestamps with zone to UTC times
        Long timeFromUtc = playerTimeSlotLimitService.convertFromTimestampToUtcWithZone(request.getFromTimestampWithZone());
        Long timeToUtc = playerTimeSlotLimitService.convertFromTimestampToUtcWithZone(request.getToTimestampWithZone());

        // Save the player limit
        lithium.service.limit.data.entities.PlayerTimeSlotLimit timeSlotLimit = playerTimeSlotLimitService.createPlayerLimit(tokenUtil.guid(), user.getId(), timeFromUtc, timeToUtc, tokenUtil);

        // Convert the player limit to a valid response
        return playerTimeSlotLimitService.convertLimitToResponse(timeSlotLimit);
    }

    @PostMapping("{domainName}/casino-no-more")
    public Access casinoNoMore(@PathVariable("domainName") String domainName,
                               @RequestBody PlayerRestrictionRequestFE request,
                               LithiumTokenUtil tokenUtil)
            throws UserNotFoundException, Status500InternalServerErrorException, Status403AccessDeniedException,
            UserClientServiceFactoryException, Status403PlayerRestrictionDeniedException,
            Status409PlayerRestrictionConflictException, Status422PlayerRestrictionExclusionException, Status550ServiceDomainClientException, Status400BadRequestException {
        DomainValidationUtil.validate(tokenUtil.domainName(), domainName);

        String comment = request.getComment();
        Integer subType = request.getSubType();

        User user = userApiInternalClientService.getUserByGuid(tokenUtil.guid());

        Access access = userRestrictionService.checkAccess(tokenUtil.guid());
        if (access.isCasinoSystemPlaced()) {
            throw new Status403AccessDeniedException(access.getCasinoErrorMessage());
        }
        DomainRestrictionSet set = restrictionService.findByDomainAndName(domainName, SystemRestriction.PLAYER_CASINO_BLOCK.restrictionName());
        UserRestrictionSet userRestrictionSet = userRestrictionService.find(tokenUtil.guid(), set);
        if (userRestrictionSet == null) {
            if(subType != null && (subType == 0 || subType > set.getAltMessageCount())) {
                String message = String.format("Invalid subType value, value should either be `null`, or be between 1 and %d", set.getAltMessageCount());
                log.warn(message);
                throw new Status400BadRequestException(message);
            }
            userRestrictionService.place(tokenUtil.guid(), set, tokenUtil.guid(), comment, user.getId(), subType, tokenUtil);
            return userRestrictionService.checkAccess(tokenUtil.guid());
        }
        userRestrictionService.checkDomainAllowLiftingPlayerCasinoBlock(domainName);
        userRestrictionService.lift(tokenUtil.guid(), userRestrictionSet.getSet(), tokenUtil.guid(), comment, user.getId(), tokenUtil);
        return userRestrictionService.checkAccess(tokenUtil.guid());
    }

    @DeleteMapping("/remove-player-time-slot-limit")
    public Boolean removePlayerTimeSlotLimit(LithiumTokenUtil tokenUtil)
            throws UserNotFoundException, UserClientServiceFactoryException,
            Status477DomainTimeSlotLimitDisabledException {
        // Check to see if we can action this controller
        playerTimeSlotLimitService.throwIfTimeSlotLimitIsDisabledForDomain(tokenUtil.domainName());

        // Fetch the user
        User user = userApiInternalClientService.getUserByGuid(tokenUtil.guid());

        // Remove their limit
        playerTimeSlotLimitService.removePlayerLimit(tokenUtil.guid(), user.getId(), tokenUtil);

        // Default return as true
        return true;
    }

}
