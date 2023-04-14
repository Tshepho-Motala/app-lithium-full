package lithium.service.casino.provider.sportsbook.api.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.service.casino.exceptions.Status409DuplicateSubmissionException;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.api.exceptions.Status422DataValidationError;
import lithium.service.casino.provider.sportsbook.api.schema.settledebit.SettleDebitRequest;
import lithium.service.casino.provider.sportsbook.api.schema.settledebit.SettleDebitResponse;
import lithium.service.casino.provider.sportsbook.context.SettleDebitContext;
import lithium.service.casino.provider.sportsbook.services.OpenBetsOperatorMigrationService;
import lithium.service.casino.provider.sportsbook.services.PubSubBetService;
import lithium.service.casino.provider.sportsbook.services.SettleDebitService;
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
public class SettleDebitController {
    @Autowired @Setter
    SettleDebitService service;

    @Autowired
    GuidConverterService guidConverterService;

    @Autowired
    PubSubBetService pubSubBetService;

    @Autowired
    OpenBetsOperatorMigrationService openBetsOperatorMigrationService;

    @PostMapping("/settle/debit")
    public SettleDebitResponse settleDebit(
            @RequestParam(defaultValue = "en_US") String locale,
            @RequestBody SettleDebitRequest request)
            throws Status470HashInvalidException, Status512ProviderNotConfiguredException,
            Status422DataValidationError, Status500UnhandledCasinoClientException,
            Status471InsufficientFundsException, Status500InternalServerErrorException {

        SettleDebitContext context = SettleDebitContext.builder()
                .request(request)
                .response(new SettleDebitResponse())
                .convertedGuid(guidConverterService.convertFromSportbookToLithium(request.getGuid()))
                .build();

        try {
            // Remove check after VB migration of open bets
            openBetsOperatorMigrationService.validateRequestForSettlement(context.getConvertedGuid());

            try {
                service.settleDebit(context);
            } catch (Status409DuplicateSubmissionException de) {
                log.info("settledebit duplicate " + de + " " + context);
            }

            context.getResponse().setBalance(CurrencyAmount.fromAmount(context.getSettlementDebit().getBalanceAfter()));
            context.getResponse().setBalanceCurrencyCode(context.getCurrency().getCode());
            context.getResponse().setTransactionId(context.getSettlementDebit().getAccountingTransactionId());

            log.info("settledebit " + context);
            if(pubSubBetService.isChannelActivated(context.getDomain().getName())) {
                pubSubBetService.buildAndSendPubSubSettlementDebitMessage(context);
            }
            return context.getResponse();
        } catch (ErrorCodeException ec) {
            log.warn("settledebit " + ec + " " + context);
            throw ec;
        } catch (Exception e) {
            log.error("settledebit " + ExceptionMessageUtil.allMessages(e) + " " + context, e);
            throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e), e);
        }
    }}
