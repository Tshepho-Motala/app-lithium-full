package lithium.service.limit.controllers.backoffice;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status100InvalidInputDataException;
import lithium.service.limit.client.exceptions.Status476DomainBalanceLimitDisabledException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.data.objects.PlayerBalanceLimit;
import lithium.service.limit.services.BalanceLimitService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/backoffice/balance-limit/{domainName}")
public class BackofficeBalanceLimitController {
    @Autowired
    private BalanceLimitService balanceLimitService;

    @GetMapping("/player")
    public Response<PlayerBalanceLimit> playerBalanceLimits(@PathVariable("domainName") String domainName,
                                                            @RequestParam("playerGuid") String playerGuid) {
        log.trace("Admin request to retrieve all balance limits for " + playerGuid + ".");
        try {
            DomainValidationUtil.validate(domainName, domainName);
            PlayerBalanceLimit balanceLimit = balanceLimitService.checkAndFindPlayerBalanceLimit(playerGuid);
            balanceLimit.setPendingLimitDelay(balanceLimitService.getPendingBalanceLimitUpdateDelay(domainName));
            return Response.<PlayerBalanceLimit>builder().data(balanceLimit).status(Response.Status.OK).build();
        } catch (Status476DomainBalanceLimitDisabledException e) {
            return Response.<PlayerBalanceLimit>builder().data(PlayerBalanceLimit.builder().disabled(true).build()).status(Response.Status.OK).build();
        } catch (Status550ServiceDomainClientException e) {
            return Response.<PlayerBalanceLimit>builder().status(Response.Status.CUSTOM.fromId(e.getCode())).message(e.getMessage()).build();
        } catch (Status500InternalServerErrorException | Status500LimitInternalSystemClientException e) {
            return Response.<PlayerBalanceLimit>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }

    @DeleteMapping("/pending")
    public Response<PlayerBalanceLimit> removePending(@PathVariable("domainName") String domainName,
                                                      @RequestParam(name = "playerGuid") String playerGuid, LithiumTokenUtil tokenUtil) throws Status500InternalServerErrorException {
        log.trace("Admin request to remove pending balance limit for " + playerGuid + ".");
        DomainValidationUtil.validate(domainName, playerGuid.split("/")[0]);
        balanceLimitService.removePending(playerGuid, tokenUtil.guid(), tokenUtil);
        return playerBalanceLimits(domainName, playerGuid);
    }

    @PostMapping("/save")
    public Response<PlayerBalanceLimit> save(@PathVariable("domainName") String domainName,
                                             @RequestParam(name = "playerGuid") String playerGuid, @RequestParam(name = "amount") BigDecimal amount, LithiumTokenUtil util) {
        try {
            DomainValidationUtil.validate(domainName, domainName);

            PlayerBalanceLimit savedBalanceLimit = balanceLimitService.save(playerGuid, amount, util.guid(), util);
            savedBalanceLimit.setPendingLimitDelay(balanceLimitService.getPendingBalanceLimitUpdateDelay(domainName));
            return Response.<PlayerBalanceLimit>builder().data(savedBalanceLimit).status(Response.Status.OK).build();
        } catch (Status100InvalidInputDataException | Status550ServiceDomainClientException | Status476DomainBalanceLimitDisabledException e) {
            return Response.<PlayerBalanceLimit>builder().status(Response.Status.CUSTOM.fromId(e.getCode())).message(e.getMessage()).build();
        } catch (Status500InternalServerErrorException | Status500LimitInternalSystemClientException | Status510AccountingProviderUnavailableException e) {
            return Response.<PlayerBalanceLimit>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }

}
