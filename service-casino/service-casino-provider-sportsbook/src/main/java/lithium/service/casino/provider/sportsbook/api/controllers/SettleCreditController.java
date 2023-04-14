package lithium.service.casino.provider.sportsbook.api.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status444ReferencedEntityNotFound;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.schema.settlecredit.SettleCreditRequest;
import lithium.service.casino.provider.sportsbook.api.schema.settlecredit.SettleCreditResponse;
import lithium.service.casino.provider.sportsbook.context.SettleCreditContext;
import lithium.service.casino.provider.sportsbook.services.OpenBetsOperatorMigrationService;
import lithium.service.casino.provider.sportsbook.services.PubSubBetService;
import lithium.service.casino.provider.sportsbook.services.SettleCreditService;
import lithium.math.CurrencyAmount;
import lithium.service.casino.provider.sportsbook.shared.service.GuidConverterService;
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
public class SettleCreditController {
    @Autowired @Setter
    SettleCreditService service;

    @Autowired
    GuidConverterService guidConverterService;

    @Autowired
    PubSubBetService pubSubBetService;

    @Autowired
    OpenBetsOperatorMigrationService openBetsOperatorMigrationService;

    @PostMapping("/settle/credit")
    public SettleCreditResponse settleCredit(
        @RequestParam(defaultValue = "en_US") String locale,
        @RequestBody SettleCreditRequest request
    ) throws
        Status422DataValidationError,
        Status444ReferencedEntityNotFound,
        Status470HashInvalidException,
        Status500InternalServerErrorException,
        Status500UnhandledCasinoClientException,
        Status512ProviderNotConfiguredException
    {

        SettleCreditContext context = SettleCreditContext.builder()
                .request(request)
                .response(new SettleCreditResponse())
                .convertedGuid(guidConverterService.convertFromSportbookToLithium(request.getGuid()))
                .build();

        try {
            // Remove check after VB migration of open bets
            openBetsOperatorMigrationService.validateRequestForSettlement(context.getConvertedGuid());

            try {
                service.settleCredit(context);
            } catch (Status409DuplicateSubmissionException de) {
                log.warn("settlecredit duplicate " + de + " " + context);
            }

            context.getResponse().setBalance(CurrencyAmount.fromAmount(context.getSettlementCredit().getBalanceAfter()));
            context.getResponse().setBalanceCurrencyCode(context.getCurrency().getCode());
            context.getResponse().setTransactionId(context.getSettlementCredit().getAccountingTransactionId());

            log.info("settlecredit " + context);
            if (pubSubBetService.isChannelActivated(context.getDomain().getName())) {
                pubSubBetService.buildAndSendPubSubCreditSettlementMessage(context);
            }
            return context.getResponse();
        } catch (ErrorCodeException ec) {
            log.warn("settlecredit " + ec + " " + context);
            throw ec;
        } catch (Exception e) {
            log.error("settlecredit " + ExceptionMessageUtil.allMessages(e) + " " + context, e);
            throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e), e);
        }
    }
}
