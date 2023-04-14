package lithium.service.cashier.processor.paypal.api.controllers;

import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import lithium.service.cashier.client.frontend.ProcessorAccountResponseStatus;
import lithium.service.cashier.client.internal.AccountProcessorRequest;
import lithium.service.cashier.client.internal.ProcessorAccountClient;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.processor.paypal.data.AgreementTokenResponse;
import lithium.service.cashier.processor.paypal.services.BillingAgreementService;
import lithium.service.cashier.processor.paypal.services.DepositService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.isNull;

@RestController
@Slf4j
public class BillingAgreementController implements ProcessorAccountClient {

    @Autowired
    private BillingAgreementService billingAgreementService;

    @Autowired
    MessageSource messageSource;

    @Override
    @RequestMapping(path="/internal/processor-account/add", method= RequestMethod.POST)
    public ProcessorAccountResponse addProcessorAccount(@RequestBody AccountProcessorRequest request) {
        String baToken = request.getMetadata().get("baToken");
        String payerId = request.getMetadata().get("payerId");
        String orderId = request.getMetadata().get("orderId");
        String guid = request.getUser().getRealGuid();
        if (StringUtil.isEmpty(baToken) || StringUtil.isEmpty(payerId) || StringUtil.isEmpty(orderId)) {
            log.error("Missing billing agreement data ("+guid+") (baToken: "+ baToken + " payerId: "+ payerId + " orderId:"+ orderId+")");
            return ProcessorAccountResponse.builder()
                    .status(ProcessorAccountResponseStatus.FAILED)
                    .errorCode("500")
                    .errorMessage("Missing billing agreement data")
                    .generalError(GeneralError.FAILED_TO_ADD_BILLING_AGREEMENT.getResponseMessageLocal(messageSource, request.getUser().getDomain(), request.getUser().getLanguage()))
                    .build();
        }
        return billingAgreementService.createBillingAgreement(guid, request, baToken, payerId, orderId);
    }

    @RequestMapping(path="/public/billing-agreement/token", method= RequestMethod.POST)
    public AgreementTokenResponse getBillingAgreementToken(LithiumTokenUtil tokenUtil) {
        String guid = tokenUtil.guid();
        String domainName = tokenUtil.getJwtUser().getDomainName();
        try {
            String agreementTokenId = billingAgreementService.getAgreementTokenId(guid, domainName);
            return new AgreementTokenResponse(agreementTokenId, null);
        } catch (Exception e) {
            log.error("Getting billing agreement token failed ("+guid+") due "+ e.getMessage(), e);
            return new AgreementTokenResponse(null, GeneralError.FAILED_TO_ADD_BILLING_AGREEMENT.getResponseMessageLocal(messageSource, domainName));
        }
    }

}
