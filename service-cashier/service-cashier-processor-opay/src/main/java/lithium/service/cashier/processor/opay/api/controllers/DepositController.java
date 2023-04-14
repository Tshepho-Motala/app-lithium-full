package lithium.service.cashier.processor.opay.api.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.processor.opay.api.v2.schema.BalanceResponse;
import lithium.service.cashier.processor.opay.api.v2.schema.BaseResponse;
import lithium.service.cashier.processor.opay.api.v2.schema.DepositRequest;
import lithium.service.cashier.processor.opay.api.v2.schema.DepositResponse;
import lithium.service.cashier.processor.opay.api.v2.schema.DepositStatusResponse;
import lithium.service.cashier.processor.opay.api.v2.schema.OpayDepositStatusRequest;
import lithium.service.cashier.processor.opay.api.v2.schema.ValidateRequest;
import lithium.service.cashier.processor.opay.context.BalanceRequestContext;
import lithium.service.cashier.processor.opay.context.DepositRequestContext;
import lithium.service.cashier.processor.opay.context.DepositStatusRequestContext;
import lithium.service.cashier.processor.opay.exceptions.Status900InvalidSignatureException;
import lithium.service.cashier.processor.opay.exceptions.Status901InvalidOrMissingParameters;
import lithium.service.cashier.processor.opay.exceptions.Status902UserNotFoundException;
import lithium.service.cashier.processor.opay.exceptions.Status903ReferenceExistsException;
import lithium.service.cashier.processor.opay.exceptions.Status906DepositNotAllowed;
import lithium.service.cashier.processor.opay.exceptions.Status907UserSelfExcluded;
import lithium.service.cashier.processor.opay.exceptions.Status999GeneralFailureException;
import lithium.service.cashier.processor.opay.services.DepositService;
import lithium.math.CurrencyAmount;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@RestController
public class DepositController {

    @Autowired
    DepositService depositService;
    @Autowired
    LocaleContextProcessor localeContextProcessor;

    private static final String NG_PHONE_CODE = "234";
    private static final int NG_PHONE_NUMBER_LENGTH = 10;

    @PostMapping(path = "public/deposit/validate", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseResponse validate(
            @RequestParam Map<String, Object> map,
            @RequestParam(value = "locale", required = false) String locale) {
        localeContextProcessor.setLocaleContextHolder(locale);
        ValidateRequest request = new ObjectMapper().convertValue(map, ValidateRequest.class);
        if (isNull(request) || request.getMsisdn().isEmpty() || request.getDateTime().isEmpty() || request.getSignature().isEmpty()) {
            return wrongRequest();
        }

        BalanceRequestContext context = new BalanceRequestContext();
        try {
            String formattedPhoneNumber = formatPhoneNumber(request.getMsisdn());

            context.setMsisdn(request.getMsisdn());
            context.setTimestamp(request.getDateTime());
            context.setSignature(request.getSignature());
            context.setGroupRef(request.getGroupRef());
            context.setFormattedPhoneNumber(formattedPhoneNumber);

            depositService.balance(context);
        } catch (Status999GeneralFailureException e) {
            return requestFailed();
        } catch (Status900InvalidSignatureException | Status907UserSelfExcluded | Status901InvalidOrMissingParameters e) {
            return customErrorResponse(e.getMessage());
        } catch (Status902UserNotFoundException e) {
            return userNotFound();
        } catch (Status906DepositNotAllowed e) {
            return userNotVerified(e.getMessage());
        }

        BalanceResponse response = new BalanceResponse();
        response.setStatus("Ok");

        response.setBalance(CurrencyAmount.fromCents(context.getBalanceInCents()).toAmount());
        response.setMessage("Successful");
        response.setFirstName(context.getFirstName());

        return response;
    }

    @PostMapping(path = "public/deposit/opayin", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseResponse deposit(
            @RequestParam Map<String, Object> map,
            @RequestParam(value = "locale", required = false) String locale) {
        localeContextProcessor.setLocaleContextHolder(locale);
        DepositRequest request = new ObjectMapper().convertValue(map, DepositRequest.class);
        if (isNull(request) || request.getMsisdn().isEmpty() || request.getDateTime().isEmpty() || request.getSignature().isEmpty()) {
            return wrongRequest();
        }

        DepositRequestContext context = new DepositRequestContext();
        try {
            String formattedPhoneNumber = formatPhoneNumber(request.getMsisdn());
            context.setMsisdn(request.getMsisdn());
            context.setTimestamp(request.getDateTime());
            context.setSignature(request.getSignature());
            context.setRequest(request);
            context.setGroupRef(request.getGroupRef());
            context.setPaymentType(request.getPaymentChannel());
            context.setFormattedPhoneNumber(formattedPhoneNumber);

            depositService.deposit(context);
        } catch (Status999GeneralFailureException e) {
            return requestFailed();
        } catch (Status900InvalidSignatureException | Status901InvalidOrMissingParameters e) {
            return customErrorResponse(e.getMessage());
        } catch (Status902UserNotFoundException e) {
            return userNotFound();
        } catch (Status903ReferenceExistsException e) {
            return referenceExists();
        } catch (Status906DepositNotAllowed e) {
            return userNotVerified(e.getMessage());
        }

        DepositResponse response = new DepositResponse();
        response.setStatus("Ok");
        response.setMessage("Success");
        response.setPaymentRef(context.getCashierReferenceNumber().toString());

        return response;
    }

    @PostMapping("public/deposit/status")
    public DepositStatusResponse status(@RequestBody OpayDepositStatusRequest request)
            throws Status901InvalidOrMissingParameters, Status999GeneralFailureException, Status900InvalidSignatureException {

        DepositStatusRequestContext context = new DepositStatusRequestContext();
        context.setTimestamp(request.getDate());
        context.setSignature(request.getSignature());
        context.setNetworkRef(request.getNetworkRef());
        context.setGroupRef(request.getGroupRef());

        depositService.depositStatus(context);

        DepositStatusResponse response = new DepositStatusResponse();
        response.setStatus(context.getDepositStatus().getStatus());
        response.setDate(context.getDepositStatus().getDate());
        return response;

    }


    private static BaseResponse userNotFound() {
        return customErrorResponse("Cannot find msisdn");
    }

    private static BaseResponse userNotVerified(String message) {
        return customErrorResponse(message);
    }

    private static BaseResponse requestFailed() {
        return customErrorResponse("Request failed try again Later");
    }

    private static BaseResponse customErrorResponse(String message) {
        BaseResponse response = new BaseResponse();
        response.setStatus("error");
        response.setMessage(message);
        return response;
    }

    private static BaseResponse wrongRequest() {
        return customErrorResponse("System failed to interpret Request Received");
    }

    private static BaseResponse referenceExists() {
        return customErrorResponse("Transaction with this reference already exists");
    }

    private String formatPhoneNumber(String originalPhoneNumber) throws Status901InvalidOrMissingParameters {
        String inputPhoneNumber = Optional.ofNullable(originalPhoneNumber).map(s -> s.replaceAll("[^\\d]","").trim()).orElse("");
        if (inputPhoneNumber.length() < NG_PHONE_NUMBER_LENGTH) {
            String message = "Incorrect input phone number =[" + originalPhoneNumber + "]";
            log.error(message);
            throw new Status901InvalidOrMissingParameters(message);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(NG_PHONE_CODE);
        sb.append(inputPhoneNumber.substring(inputPhoneNumber.length() - NG_PHONE_NUMBER_LENGTH));
        String formattedPhoneNumber = sb.toString();
        if (!formattedPhoneNumber.equalsIgnoreCase(inputPhoneNumber)) {
            log.info("Opay deposit incoming phone number ["+originalPhoneNumber+"] formatted to ["+formattedPhoneNumber+"]");
        }
        return formattedPhoneNumber;
    }
}
