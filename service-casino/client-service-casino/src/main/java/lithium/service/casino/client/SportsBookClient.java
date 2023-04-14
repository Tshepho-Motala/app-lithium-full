package lithium.service.casino.client;

import lithium.service.Response;
import lithium.service.casino.client.objects.BonusRestrictionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="service-casino-provider-sportsbook")
public interface SportsBookClient {
    @RequestMapping(value = "/system/bonus/{domainName}/toggle-bonus-restriction", method = RequestMethod.POST)
    Response<?> toggleBonusRestriction(@RequestBody BonusRestrictionRequest bonusRestriction, @PathVariable("domainName") String domainName);
}
