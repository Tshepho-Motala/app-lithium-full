package lithium.service.cashier.processor.nuvei.cc.services;

import com.safecharge.biz.SafechargeRequestExecutor;
import com.safecharge.exception.SafechargeException;
import com.safecharge.model.CashierPaymentMethodDetails;
import com.safecharge.model.MerchantInfo;
import com.safecharge.request.GetUserUPOsRequest;
import com.safecharge.request.SafechargeBaseRequest;
import com.safecharge.response.GetUserUPOsResponse;
import com.safecharge.response.SafechargeResponse;
import com.safecharge.response.ThreeDResponse;
import com.safecharge.util.APIConstants;
import com.safecharge.util.Constants;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class NuveiCCUserPaymentMethodService {
    private final CashierInternalClientService cashierInternalClientService;

    private static final String METHOD_CODE = "nuvei-cc";
    private static final String PROCESSOR_ACCOUNT_TYPE_CARD = "cc_card";
    private static final String PROCESSOR_ACCOUNT_TYPE_PAYPAL = "apmgw_expresscheckout";

    @Autowired
    public NuveiCCUserPaymentMethodService(CashierInternalClientService cashierInternalClientService) {
        this.cashierInternalClientService = cashierInternalClientService;
    }

    public List<ProcessorAccount> retrieveFromNuvei(String domainName, String userTokenId)
            throws Status500InternalServerErrorException {
        log.trace("Received request to retrieve user payment options from nuvei | domainName: {}, userTokenId: {} ",
                domainName, userTokenId);

        // This might put some strain on scp-nuvei-cc and service-cashier.
        // If dry run/performance testing proves it, we might need to consider caching these properties for
        // expected load of repeated and concurrent calls.
        Map<String, String> properties;
        try {
            properties = cashierInternalClientService.propertiesOfFirstEnabledProcessorByMethodCode(domainName,
                    true, METHOD_CODE);
        } catch (Exception e) {
            String errorMessage = "Unable to retrieve " + METHOD_CODE + " processor properties from service-cashier";
            log.error(errorMessage + " | domainName: {}, userTokenId: {} | {}", domainName, userTokenId,
                    e.getMessage(), e);
            throw new Status500InternalServerErrorException(errorMessage);
        }
        log.trace("Received " + METHOD_CODE + " processor properties for " + domainName + " from service-cashier | {}",
                properties);

        String merchantId = properties.get("upos_mid");
        String siteId = properties.get("upos_sid");
        String merchantKey = properties.get("upos_mkey");
        String hashAlgorithm = properties.get("upos_hash_algorithm");
        boolean test = Boolean.parseBoolean(properties.get("test")); // Has default

        validateProperties(merchantId, siteId, merchantKey, hashAlgorithm);

        MerchantInfo merchantInfo = getMerchantInfo(merchantId, siteId, merchantKey, hashAlgorithm, test);

        SafechargeBaseRequest request = GetUserUPOsRequest.builder()
                .addUserTokenId(userTokenId)
                .addMerchantInfo(merchantInfo)
                .build();
        try {
            SafechargeResponse response = SafechargeRequestExecutor.getInstance().execute(request);
            GetUserUPOsResponse userUpos = (GetUserUPOsResponse) response;

            return userUpos.getPaymentMethods()
                    .stream()
                    .map(this::buildUserCard)
                    .toList();
        } catch (SafechargeException e) {
            String errorMessage = "Unable to retrieve user payment options from Nuvei";
            log.error(errorMessage + " | domainName: {}, userTokenId: {} |  {}", domainName, userTokenId,
                    e.getMessage(), e);
            throw new Status500InternalServerErrorException(errorMessage);
        }
    }

    private ProcessorAccount buildUserCard(CashierPaymentMethodDetails upo) {
        boolean active = upo.getUpoStatus().contentEquals("enabled");
        PaymentMethodStatusType status = (active)
                ? PaymentMethodStatusType.ACTIVE
                : PaymentMethodStatusType.DISABLED;

        boolean depositSuccess = Boolean.parseBoolean(upo.getDepositSuccess());
        boolean withdrawSuccess = Boolean.parseBoolean(upo.getWithdrawSuccess());

        ProcessorAccountType type = switch (upo.getPaymentMethodName()) {
            case PROCESSOR_ACCOUNT_TYPE_CARD -> ProcessorAccountType.CARD;
            case PROCESSOR_ACCOUNT_TYPE_PAYPAL -> ProcessorAccountType.PAYPAL;
            default -> ProcessorAccountType.HISTORIC;
        };

        ProcessorAccount processorAccount = ProcessorAccount.builder()
                .reference((upo.getUserPaymentOptionId() != null)
                        ? String.valueOf(upo.getUserPaymentOptionId())
                        : null)
                .methodCode(METHOD_CODE)
                .status(status)
                .hideInDeposit(false)
                .type(type)
                .name(upo.getBillingAddress().getFirstName()
                        + " " + upo.getBillingAddress().getLastName())
                .verified(depositSuccess || withdrawSuccess)
                .build();

        if (type.equals(ProcessorAccountType.CARD)) {
            String bin = upo.getUpoData().get("bin");
            String lastFourDigits = StringUtils.right(upo.getUpoData().get("ccCardNumber"), 4);
            String ccExpMonth = upo.getUpoData().get("ccExpMonth");
            String ccExpYear = upo.getUpoData().get("ccExpYear");
            String expiryDate = (ccExpMonth != null && ccExpYear != null)
                    ? ccExpMonth + "/" + ccExpYear
                    : null;

            processorAccount.setDescriptor(bin + "****" + lastFourDigits);
            processorAccount.setData(new HashMap<>() {{
                put("cardType", upo.getUpoData().get("cardType"));
                put("bin", bin);
                put("last4Digits", lastFourDigits);
                put("expiryDate", expiryDate);
                put("scheme", upo.getUpoData().get("brand"));
                put("country", upo.getUpoData().get("issuerCountry"));
                put("name", upo.getBillingAddress().getFirstName()
                        + " " + upo.getBillingAddress().getLastName());
            }});
        } else {
            processorAccount.setData(upo.getUpoData());
        }

        return processorAccount;
    }

    private void validateProperties(String merchantId, String siteId, String merchantKey, String hashAlgorithm)
            throws Status500InternalServerErrorException {
        StringBuilder errorMessageBuilder = new StringBuilder();
        if (merchantId == null || merchantId.trim().isEmpty()) {
            appendPropertyErrorMessage(errorMessageBuilder, "merchant ID");
        }
        if (siteId == null || siteId.trim().isEmpty()) {
            appendPropertyErrorMessage(errorMessageBuilder, "merchant SID");
        }
        if (merchantKey == null || merchantKey.trim().isEmpty()) {
            appendPropertyErrorMessage(errorMessageBuilder, "merchant key");
        }
        if (hashAlgorithm == null || hashAlgorithm.trim().isEmpty()) {
            appendPropertyErrorMessage(errorMessageBuilder, "merchant hash algorithm");
        }

        if (errorMessageBuilder.length() > 0) {
            String errorMessage = "Unable to retrieve user payment method options from Nuvei | \n" +
                    errorMessageBuilder;
            log.error(errorMessage);
            throw new Status500InternalServerErrorException(errorMessage);
        }
    }

    private void appendPropertyErrorMessage(StringBuilder errorMessageBuilder, String property) {
        String propertyMissingError = "The " + property + " to use for retrieving user payment options has not been"
                + " setup.";
        if (errorMessageBuilder.length() > 0) {
            errorMessageBuilder.append("\n");
        }
        errorMessageBuilder.append(propertyMissingError);
    }

    private MerchantInfo getMerchantInfo(String merchantId, String siteId, String merchantKey, String hashAlgorithm,
            boolean test) throws Status500InternalServerErrorException {
        String host = test
                    ? APIConstants.Environment.INTEGRATION_HOST.getUrl()
                    : APIConstants.Environment.PRODUCTION_HOST.getUrl();
        return new MerchantInfo(merchantKey, merchantId, siteId, host, getHashAlgorithm(hashAlgorithm));
    }

    private Constants.HashAlgorithm getHashAlgorithm(String hashAlgorithm)
            throws Status500InternalServerErrorException {
        if (hashAlgorithm.contentEquals("MD5")) {
            return Constants.HashAlgorithm.MD5;
        } else if (hashAlgorithm.contentEquals("SHA-256")) {
            return Constants.HashAlgorithm.SHA256;
        }
        throw new Status500InternalServerErrorException("The specified hash algorithm is not supported");
    }
}
