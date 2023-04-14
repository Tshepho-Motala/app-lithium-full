package lithium.service.casino.provider.sportsbook.controllers.system;

import lithium.service.Response;
import lithium.service.casino.client.objects.BonusRestrictionRequest;
import lithium.service.casino.provider.sportsbook.response.BonusRestrictionResponse;
import lithium.service.casino.provider.sportsbook.services.BonusRestrictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/bonus/{domainName}")
public class SystemSportsbookBonusRestrictionController {

    @Autowired
    private BonusRestrictionService bonusRestrictionService;

    @RequestMapping(value = "/toggle-bonus-restriction", method = RequestMethod.POST)
    public Response<?> toggleBonusRestriction(@RequestBody BonusRestrictionRequest bonusRestriction, @PathVariable String domainName) {
        BonusRestrictionResponse response = bonusRestrictionService.toggle(bonusRestriction, domainName);

        return Response.builder()
                .status(Response.Status.fromId(response.getErrorCode()))
                .message(response.getErrorMessage())
                .build();
    }
}
