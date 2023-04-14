package lithium.service.limit.api.controllers;

import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.service.Response;
import lithium.service.limit.client.exceptions.Status464TimeSlotLimitNotFoundException;
import lithium.service.limit.client.exceptions.Status477DomainTimeSlotLimitDisabledException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.objects.PlayerTimeSlotLimitResponse;
import lithium.service.limit.client.objects.TimeSlotLimitRequest;
import lithium.service.limit.client.objects.TimeSlotLimitResponse;
import lithium.service.limit.services.PlayerTimeSlotLimitService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/frontend/player-limit/v2")
public class FrontendPlayerLimitV2Controller {
    @Autowired
    private UserApiInternalClientService userApiInternalClientService;
    @Autowired
    private PlayerTimeSlotLimitService playerTimeSlotLimitService;
    @Autowired
    private MessageSource messageSource;


    @GetMapping("/get-player-time-slot-limit")
    public TimeSlotLimitResponse findPlayerTimeSlotLimit(LithiumTokenUtil tokenUtil)
            throws Status477DomainTimeSlotLimitDisabledException, Status464TimeSlotLimitNotFoundException {
        // Check to see if we can action this controller
        playerTimeSlotLimitService.throwIfTimeSlotLimitIsDisabledForDomain(tokenUtil.domainName());

        // Fetch the player limit
        lithium.service.limit.data.entities.PlayerTimeSlotLimit timeSlotLimit = playerTimeSlotLimitService.findPlayerLimit(tokenUtil.guid());

        // Convert the player limit to a valid response
        PlayerTimeSlotLimitResponse playerTimeSlotLimitResponse = playerTimeSlotLimitService.convertLimitToResponse(timeSlotLimit);
        if (playerTimeSlotLimitResponse.getFromTimestampUTC() != null && playerTimeSlotLimitResponse.getToTimestampUTC() != null) {
            return TimeSlotLimitResponse.builder()
                    .fromTimeUTC(playerTimeSlotLimitResponse.getFromTimestampUTC())
                    .toTimeUTC(playerTimeSlotLimitResponse.getToTimestampUTC())
                    .build();
        } else {
            return TimeSlotLimitResponse.builder()
                    .fromTimeUTC(null)
                    .toTimeUTC(null)
                    .build();
        }
    }

    @RequestMapping(value = "/set-player-time-slot-limit", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public TimeSlotLimitResponse setPlayerTimeSlotLimit(TimeSlotLimitRequest request, LithiumTokenUtil tokenUtil)
            throws UserNotFoundException, UserClientServiceFactoryException, Status477DomainTimeSlotLimitDisabledException, Status478TimeSlotLimitException, Status426InvalidParameterProvidedException {
        // Check to see if we can action this controller
        playerTimeSlotLimitService.throwIfTimeSlotLimitIsDisabledForDomain(tokenUtil.domainName());

        // Fetch the current user
        User user = userApiInternalClientService.getUserByGuid(tokenUtil.guid());

        // Validate input received
        playerTimeSlotLimitService.validateTimeSlotLimitInputReceived(request, user);

        // Convert the timestamps HH:MM without zone to UTC times
        Long timeFromUtc = playerTimeSlotLimitService.convertFromTimestampToUtc(request.getFromTimeUTC());
        Long timeToUtc = playerTimeSlotLimitService.convertFromTimestampToUtc(request.getToTimeUTC());

        // Save the player limit
        lithium.service.limit.data.entities.PlayerTimeSlotLimit timeSlotLimit = playerTimeSlotLimitService.createPlayerLimit(tokenUtil.guid(), user.getId(), timeFromUtc, timeToUtc, tokenUtil);

        // Convert the player limit to a valid response
        PlayerTimeSlotLimitResponse playerTimeSlotLimitResponse = playerTimeSlotLimitService.convertLimitToResponse(timeSlotLimit);
        return TimeSlotLimitResponse.builder()
                .fromTimeUTC(playerTimeSlotLimitResponse.getFromTimestampUTC())
                .toTimeUTC(playerTimeSlotLimitResponse.getToTimestampUTC())
                .build();
    }

    @DeleteMapping("/remove-player-time-slot-limit")
    public Response<Boolean> removePlayerTimeSlotLimit(LithiumTokenUtil tokenUtil)
            throws UserNotFoundException, UserClientServiceFactoryException, Status477DomainTimeSlotLimitDisabledException {
        // Check to see if we can action this controller
        playerTimeSlotLimitService.throwIfTimeSlotLimitIsDisabledForDomain(tokenUtil.domainName());

        // Fetch the user
        User user = userApiInternalClientService.getUserByGuid(tokenUtil.guid());

        // Remove their limit
        playerTimeSlotLimitService.removePlayerLimit(tokenUtil.guid(), user.getId(), tokenUtil);

        // Default return as true
        return Response.<Boolean>builder()
                .status(Response.Status.OK)
                .data(true)
                .build();
    }
}
