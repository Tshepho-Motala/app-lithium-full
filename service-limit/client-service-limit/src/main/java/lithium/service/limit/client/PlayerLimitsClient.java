package lithium.service.limit.client;

import feign.Headers;
import lithium.service.Response;
import lithium.service.limit.client.exceptions.Status477DomainTimeSlotLimitDisabledException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.objects.PlayerLimit;
import lithium.service.limit.client.objects.PlayerTimeSlotLimit;
import lithium.service.user.client.objects.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="service-limit")
public interface PlayerLimitsClient {

    @RequestMapping(method = RequestMethod.GET, path = "/system/player-limit/{domainName}/set-player-time-slot-limit")
    Response<PlayerTimeSlotLimit> setPlayerTimeSlotLimit(@RequestParam("playerGuid") String playerGuid,
                                                         @RequestParam("playerId") Long playerId,
                                                         @PathVariable("domainName") String domainName,
                                                         @RequestParam("timeFromUtc") Long timeFromUtc,
                                                         @RequestParam("timeToUtc") Long timeToUtc) throws Status477DomainTimeSlotLimitDisabledException, Status478TimeSlotLimitException;

    @RequestMapping(method= RequestMethod.POST, path="/system/player-limit/set-player-age-limit")
    @Headers("Content-Type: application/json")
    Response<List<PlayerLimit>> setPlayerLimit(@RequestBody User user);

    @RequestMapping(method= RequestMethod.GET, path="/system/player-limit/{domainName}/is-blocked-by-time-slot")
    Response<Boolean> isBlockedByTimeSlot(@RequestParam("playerGuid") String playerGuid, @PathVariable("domainName") String domainName);
}
