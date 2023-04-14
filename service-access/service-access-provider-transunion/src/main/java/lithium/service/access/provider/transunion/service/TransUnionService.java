package lithium.service.access.provider.transunion.service;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.provider.transunion.KycTransUnionModuleInfo;
import lithium.service.access.provider.transunion.config.TransUnionProviderConfig;
import lithium.service.access.provider.transunion.exeptions.Status512ProviderNotConfiguredException;
import lithium.service.access.provider.transunion.exeptions.UserIndividualsNotSetupException;
import lithium.service.access.provider.transunion.shema.response.fault.FaultResponse;
import lithium.service.access.provider.transunion.shema.response.passwordupdate.ExecuteChangePasswordResponse;
import lithium.service.access.provider.transunion.shema.response.passwordupdate.error.ErrorDetails;
import lithium.service.access.provider.transunion.shema.response.success.CallValidateOtherChecks;
import lithium.service.access.provider.transunion.shema.response.success.TransUnionResponse;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.user.client.objects.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Service;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static lithium.service.access.client.objects.EAuthorizationOutcome.ACCEPT;
import static lithium.service.access.client.objects.EAuthorizationOutcome.NOT_FILLED;
import static lithium.service.access.client.objects.EAuthorizationOutcome.REJECT;

@Service
@Slf4j
@AllArgsConstructor
public class TransUnionService {

    protected LithiumServiceClientFactory services;
    private SOAPParseService soapParseService;
    private TransUnionRequestBuilderService requestBuilderService;
    private KycTransUnionModuleInfo moduleInfo;
    private ChangeLogService changeLogService;
    @Autowired
    private VerificationResultService verificationResultService;

    @Autowired
    public TransUnionService(KycTransUnionModuleInfo kycTransUnionModuleInfo, LithiumServiceClientFactory services, SOAPParseService soapParseService, TransUnionRequestBuilderService requestBuilderService, ChangeLogService changeLogService) {
        this.moduleInfo = kycTransUnionModuleInfo;
        this.services = services;
        this.requestBuilderService = requestBuilderService;
        this.soapParseService = soapParseService;
        this.changeLogService = changeLogService;
    }

    public ProviderAuthorizationResult doVerify(User user, ProviderAuthorizationResult providerAuthorizationResult) throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
        providerAuthorizationResult.setAuthorisationOutcome(REJECT);
        // Create SOAP Connection


        FaultResponse faultResponse = null;
        TransUnionResponse transUnionResponse = null;

        TransUnionProviderConfig config = getConfig(moduleInfo.getModuleName(), user.getDomain().getName());
        ArrayList<RawAuthorizationData> rawAuthorizationDataList = new ArrayList<>();

        try {
            RawAuthorizationData.RawAuthorizationDataBuilder builder = RawAuthorizationData.builder();

            SOAPConnection soapConnection = getConnection();
            // Send SOAP Message to SOAP Server
            String url = config.getBaseUrl();

            SOAPMessage msg = requestBuilderService.createVerifyRequest(user, config);
            log.debug("Soap Message for identity check for User " + user.getGuid() + "created. message: " + messageToString(msg));

            Integer timeoutConnection = Integer.valueOf(config.getTimeoutConnection());
            Integer timeoutRead = Integer.valueOf(config.getTimeoutRead());

            URL endpoint = new URL(null,
                    url,
                    new URLStreamHandler() {
                        @Override
                        protected URLConnection openConnection(URL url) throws IOException {
                            URL target = new URL(url.toString());
                            URLConnection connection = target.openConnection();
                            connection.setConnectTimeout(timeoutConnection);
                            connection.setReadTimeout(timeoutRead);
                            return (connection);
                        }
                    });

            SOAPMessage soapResponse = soapConnection.call(msg, endpoint);

            String clonedMessage = messageToString(soapResponse);

            builder.rawRequestToProvider(messageToString(msg));
            builder.rawResponseFromProvider(clonedMessage);
            rawAuthorizationDataList.add(builder.build());
            providerAuthorizationResult.setRawDataList(rawAuthorizationDataList);

            try {
                faultResponse = soapParseService.unmarshallFault(clonedMessage);
                if (faultResponse != null && faultResponse.getFaultReason() != null) {
                    log.warn("Get Fault response from TransUnion Api" + faultResponse.getFaultReason().getText() + "For user:" + user.getGuid());
                    try {
                        changeLogService.registerChangesWithDomain("user.verification", "disable", user.getId(), User.SYSTEM_GUID, faultResponse.getFaultReason().getText(), "Sent from TransUnion KyC service", null, Category.ACCESS,
                                SubCategory.KYC, 0, user.getDomain().getName());
                    } catch (Exception e) {
                        log.error("can't register Account verification changes" + e.getMessage() + "For user:" + user.getGuid());
                    }
                    providerAuthorizationResult.setErrorMessage(faultResponse.getFaultReason().getText());
                    return providerAuthorizationResult;
                }
            } catch (SOAPException | UnmarshallingFailureException e) {
                log.debug("Fault Part not found in transunion response");
            }

            transUnionResponse = soapParseService.unmarshallSuccess(soapResponse, TransUnionResponse.class);
            log.debug("TransUnionResponse Unmarshalled in " + transUnionResponse.toString());

        } catch (UserIndividualsNotSetupException e) {
            log.warn("Can't start verify process, user individuals are not filled correctly:" + e.getMessage() + "For user:" + user.getGuid());
            providerAuthorizationResult.setErrorMessage(e.getMessage());
            providerAuthorizationResult.setAuthorisationOutcome(NOT_FILLED);
            try {
                changeLogService.registerChangesWithDomain("user.verification", "disable", user.getId(), User.SYSTEM_GUID, e.getMessage(), "Sent from TransUnion KyC service", null, Category.ACCESS,
                        SubCategory.KYC, 0, user.getDomain().getName());
            } catch (Exception exception) {
                log.error("can't register Account verification changes" + e.getMessage() + "For user:" + user.getGuid());
            }
            return providerAuthorizationResult;
        } catch (SOAPException | XmlMappingException | IOException e) {
            String errorMessage = "can't process user verification, User " + user.getGuid() + " , processing fail cause by: " + e.getMessage();
            log.warn(errorMessage);
            providerAuthorizationResult.setErrorMessage(errorMessage);
            try {
                changeLogService.registerChangesWithDomain("user.verification", "disable", user.getId(), User.SYSTEM_GUID, e.getMessage(), "Sent from TransUnion KyC service", null, Category.ACCESS,
                        SubCategory.KYC, 0, user.getDomain().getName());
            } catch (Exception exception) {
                log.error("can't register Account verification changes" + e.getMessage() + "For user:" + user.getGuid());
            }
            return providerAuthorizationResult;
        }
        CallValidateOtherChecks callValidateOtherChecks = null;

        Optional<CallValidateOtherChecks> otherChecksOptional = Optional.ofNullable(transUnionResponse.getSearchResultBody().getProductResponses().getCallValidate5().getCallValidate5Response().getResponse().getResult().getCallValidateDisplays().getCallValidateOtherChecks());


        if (otherChecksOptional.isPresent()) {
            callValidateOtherChecks = otherChecksOptional.get();
        }

        boolean success = false;
        String comment;
        if (callValidateOtherChecks != null && callValidateOtherChecks.getIdentityResult() != null && callValidateOtherChecks.getIdentityResult().equalsIgnoreCase("Pass")) {
            providerAuthorizationResult.setAuthorisationOutcome(ACCEPT);
            success = true;
            comment = "User " + user.getGuid() + " Verification using the Transunion service is successful. Number of points scored: " + callValidateOtherChecks.getIdentityScore();
            try {
                changeLogService.registerChangesWithDomain("user.verification", "enable", user.getId(), User.SYSTEM_GUID, comment, "Sent from TransUnion KyC service", null, Category.ACCESS,
                        SubCategory.KYC, 0, user.getDomain().getName());
            } catch (Exception e) {
                log.error("can't register Account verification changes" + e.getMessage() + "For user:" + user.getGuid());
            }
        } else {
            comment = "User " + user.getGuid() + " failed Verification";
            if (callValidateOtherChecks != null) {
                comment = comment + " Number of points scored: " + callValidateOtherChecks.getIdentityScore();
            }
            try {
                changeLogService.registerChangesWithDomain("user.verification", "disable", user.getId(), User.SYSTEM_GUID, comment, "Sent from TransUnion KyC service", null, Category.ACCESS,
                        SubCategory.KYC, 0, user.getDomain().getName());
            } catch (Exception e) {
                log.error("can't register Account verification changes" + e.getMessage() + "For user:" + user.getGuid());
            }
            providerAuthorizationResult.setAuthorisationOutcome(REJECT);
        }
        try {
            verificationResultService.sendVerificationResult(transUnionResponse, user, success, comment);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Cant store Transunion verification result for user:" + user.getGuid() + " Exception:" + Arrays.toString(e.getStackTrace()));
        }
        return providerAuthorizationResult;
    }

    public static String messageToString(SOAPMessage soap) throws SOAPException, IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        soap.writeTo(stream);
        String message = stream.toString("utf-8");
        return message;
    }

    public TransUnionProviderConfig getConfig(String providerName, String domainName) throws Status500InternalServerErrorException, Status512ProviderNotConfiguredException {
        ProviderClient providerService = getProviderService();
        Response<Iterable<ProviderProperty>> providerProperties =
                providerService.propertiesByProviderUrlAndDomainName(providerName, domainName);

        if (!providerProperties.isSuccessful() || providerProperties.getData() == null) {
            log.error("TransUnion not properly configured for this domain {}", domainName);
            throw new Status512ProviderNotConfiguredException("TransUnion");
        }

        TransUnionProviderConfig config = new TransUnionProviderConfig();

        for (ProviderProperty providerProperty : providerProperties.getData()) {
            if (providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.USERNAME.getName()))
                config.setUserName(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.PASSWORD.getName()))
                config.setPassword(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.COMPANY.getName()))
                config.setCompany(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.APPLICATION.getName()))
                config.setApplication(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.TIMEOUT_READ.getName()))
                config.setTimeoutRead(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.TIMEOUT_CONNECTION.getName()))
                config.setTimeoutConnection(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.BASE_URL.getName()))
                config.setBaseUrl(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.PASSWORD_AUTO_UPDATE.getName()))
                config.setPasswordAutoUpdate(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.PASSWORD_UPDATE_DELAY.getName()))
                config.setPasswordUpdateDelay(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.PASSWORD_LAST_UPDATE_DATE.getName()))
                config.setLastUpdateDate(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(KycTransUnionModuleInfo.ConfigProperties.PASSWORD_UPDATE_URL.getName()))
                config.setPasswordUpdateUrl(providerProperty.getValue());
        }

        if (config.getUserName() == null || config.getCompany() == null
                || config.getPassword() == null || config.getApplication() == null
                || config.getTimeoutConnection() == null || config.getTimeoutRead() == null
                || config.getBaseUrl() == null || config.getPasswordAutoUpdate() == null
                || config.getPasswordUpdateDelay() == null || config.getPasswordUpdateUrl() == null) {
            log.error("TransUnion not properly configured for this domain {}", domainName);
            throw new Status512ProviderNotConfiguredException("TransUnion");
        }

        return config;
    }

    private SOAPConnection getConnection() throws SOAPException {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        return soapConnectionFactory.createConnection();
    }

    private ProviderClient getProviderService() throws Status500InternalServerErrorException {
        ProviderClient cl = null;
        try {
            cl = services.target(ProviderClient.class, "service-domain", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting provider properties: " + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            throw new Status500InternalServerErrorException("Can't get service-domain provider client");
        }
        return cl;
    }

    public String updatePassword(String author, Domain domain) throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException, IOException {

        TransUnionProviderConfig config = getConfig(moduleInfo.getModuleName(), domain.getName());
        String newPassword = getNewPassword(config.getPassword());
        SOAPMessage msg = null;
        try {
            msg = requestBuilderService.createPasswordChangeRequest(config, newPassword);

            SOAPConnection soapConnection = getConnection();

            log.debug("Soap Message for change password job created. message: " + messageToString(msg));

            Integer timeoutConnection = Integer.valueOf(config.getTimeoutConnection());
            Integer timeoutRead = Integer.valueOf(config.getTimeoutRead());

            String url = config.getPasswordUpdateUrl();

            URL endpoint = new URL(null,
                    url,
                    new URLStreamHandler() {
                        @Override
                        protected URLConnection openConnection(URL url) throws IOException {
                            URL target = new URL(url.toString());
                            URLConnection connection = target.openConnection();
                            connection.setConnectTimeout(timeoutConnection);
                            connection.setReadTimeout(timeoutRead);
                            return (connection);
                        }
                    });

            SOAPMessage soapResponse = soapConnection.call(msg, endpoint);

            FaultResponse faultResponse = null;
            String clonedMessage = messageToString(soapResponse);

            try {
                faultResponse = soapParseService.unmarshallFault(clonedMessage);
                if (faultResponse != null && faultResponse.getFaultReason() != null) {
                    log.error("Get Fault response from TransUnion change password Api. reason:" + faultResponse.getFaultReason().getText());
                    return null;
                }
            } catch (SOAPException | UnmarshallingFailureException e) {
                log.debug("Fault Part not found in transunion response");
            }

            ExecuteChangePasswordResponse executeChangePasswordResponse = null;
            executeChangePasswordResponse = soapParseService.unmarshallSuccess(soapResponse, ExecuteChangePasswordResponse.class);
            log.debug("ExecuteChangePasswordResponse Unmarshalled in " + executeChangePasswordResponse.toString());
            ErrorDetails errorDetails = executeChangePasswordResponse.getChangePassResultBody().getError().getErrorDetails();
            if (errorDetails == null) {
                log.info("Password for TransUnion successfully updated");
                changeLogService.registerChangesWithDomain("domain.provider", "edit", domain.getId(), author, "TransUnion password auto update job changed password to: " + newPassword, null, null, Category.SUPPORT, SubCategory.PROVIDER, 0, domain.getName());
                return newPassword;
            }
            if (errorDetails.getErrorDetail().getErrorMessage().equalsIgnoreCase("The new password has already been used")) {
                log.debug("Tried to use already used password for updating, restarting TransUnion password update");
                return updatePassword(author, domain);
            }
            log.error("Get Error wile using TransUnion change password Api. reason:" + errorDetails.getErrorDetail().getErrorCode());
            throw new Status500InternalServerErrorException("Get Error wile using TransUnion change password Api. reason:" + errorDetails.getErrorDetail().getErrorCode());

        } catch (SOAPException e) {
            log.error("Get Exception wile using TransUnion change password Api. reason:" + e.getMessage());
            throw new Status500InternalServerErrorException("Get Exception wile using TransUnion change password Api.", e.getCause());
        }
    }

    private String getNewPassword(String password) {
        String newPassword = generatePassword();
        boolean passwordEquals = newPassword.equals(password);
        while (passwordEquals) {
            newPassword = generatePassword();
            passwordEquals = newPassword.equals(password);
        }
        return newPassword;
    }

    private String generatePassword() {
        String charactersUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String charactersLowerCase = "abcdefghijklmnopqrstuvwxyz";
        String charactersNumbers = "0123456789";
        String randomUpperCase = RandomStringUtils.random(3, charactersUpperCase);
        String randomLowerCase = RandomStringUtils.random(3, charactersLowerCase);
        String randomNumbers = RandomStringUtils.random(3, charactersNumbers);
        return randomLowerCase + randomUpperCase + randomNumbers;
    }
}
