package lithium.service.casino.provider.sportsbook.api.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status438ReservationPendingException;
import lithium.service.casino.provider.sportsbook.api.schema.betcancelreserve.BetCancelReserveRequest;
import lithium.service.casino.provider.sportsbook.api.schema.betcancelreserve.BetCancelReserveResponse;
import lithium.service.casino.provider.sportsbook.context.BetCancelReserveContext;
import lithium.service.casino.provider.sportsbook.services.BetCancelReserveService;
import lithium.service.casino.provider.sportsbook.shared.service.GuidConverterService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BetCancelReserveController {
    @Autowired @Setter
    BetCancelReserveService service;

    @Autowired
    GuidConverterService guidConverterService;

    @PostMapping("/bet/cancelreserve")
    public BetCancelReserveResponse betCancelReserve(
            @RequestParam(defaultValue = "en_US") String locale,
            @RequestBody BetCancelReserveRequest request)
            throws Status470HashInvalidException, Status512ProviderNotConfiguredException,
            Status500UnhandledCasinoClientException, Status422DataValidationError,
            Status500InternalServerErrorException, Status550ServiceDomainClientException,
            Status438ReservationPendingException {

        BetCancelReserveContext context = BetCancelReserveContext.builder()
                .locale(locale)
                .request(request)
                .response(new BetCancelReserveResponse())
                .convertedGuid(guidConverterService.convertFromSportbookToLithium(request.getGuid()))
                .build();
        try {
            log.info("betcancelreserve pre " + context);
            try {
                service.betCancelReserve(context);
            } catch (Status409DuplicateSubmissionException de) {
                log.warn("betcancelreserve duplicate " + de + " " + context);
            }
            context.getResponse().setBalance(CurrencyAmount.fromAmount(context.getReservationCancel().getBalanceAfter()));
            context.getResponse().setBalanceCurrencyCode(context.getCurrency().getCode());
            context.getResponse().setTransactionId(context.getReservationCancel().getAccountingTransactionId());
            log.info("betcancelreserve post " + context);
            return context.getResponse();
        } catch (Status444ReferencedEntityNotFound ec) {
            log.warn("betcancelreserve " + ec + " " + context);
            return service.getDefaultCancelReserveResponse(context);
        } catch (ErrorCodeException ec) {
            log.warn("betcancelreserve " + ec + " " + context);
            throw ec;
        } catch (Exception e) {
            log.error("betcancelreserve " + ExceptionMessageUtil.allMessages(e) + " " + context, e);
            throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e), e);
        }
    }
}
