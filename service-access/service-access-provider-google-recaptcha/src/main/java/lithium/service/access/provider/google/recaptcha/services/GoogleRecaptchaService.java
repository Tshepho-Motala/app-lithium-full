package lithium.service.access.provider.google.recaptcha.services;

import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.rest.EnableRestTemplate;
import lithium.service.Response;
import lithium.service.access.client.objects.EAuthorizationOutcome;
import lithium.service.access.client.objects.ExternalAuthorizationRequest;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.provider.google.recaptcha.config.ProviderConfigProperties;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.translate.client.objects.RegistrationError;
import lithium.service.user.client.objects.ReCaptchaApiResponse;
import lithium.service.access.provider.google.recaptcha.config.RecaptchaProviderConfig;
import lithium.util.JsonStringify;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@EnableRestTemplate
public class GoogleRecaptchaService {

    final MessageSource messageSource;
    final LithiumServiceClientFactory lithiumServiceClientFactory;
    final RestService restService;

    @Autowired
    public GoogleRecaptchaService(MessageSource messageSource,
                                  LithiumServiceClientFactory lithiumServiceClientFactory,
                                  RestService restService) {
        this.messageSource = messageSource;
        this.lithiumServiceClientFactory = lithiumServiceClientFactory;
        this.restService = restService;
    }

    public ProviderAuthorizationResult checkAuthorization(ExternalAuthorizationRequest request) {
        ProviderAuthorizationResult providerAuthorizationResult = ProviderAuthorizationResult.builder().authorisationOutcome(EAuthorizationOutcome.ACCEPT).build();

        Map<String, String> data = new HashMap<>();
        ArrayList<RawAuthorizationData> rawAuthorizationDataList = new ArrayList<>();
        Map<String, String> additionalData = request.getPlayerBasic().getAdditionalData();
        String clientRecaptchaResponse = (!ObjectUtils.isEmpty(additionalData))
                ? additionalData.get("reCaptcha")
                : null;
        RecaptchaProviderConfig providerConfig;

        if (ObjectUtils.isEmpty(clientRecaptchaResponse)) {
            data.put("message", RegistrationError.INVALID_PARAMETER
                    .getResponseMessageLocal(messageSource, request.getDomainName(), new Object[]{"Recaptcha = " + clientRecaptchaResponse}));
            providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
            providerAuthorizationResult.setRawDataList(rawAuthorizationDataList);
            providerAuthorizationResult.setData(data);
            return providerAuthorizationResult;
        }

        try {
            providerConfig = getConfig(request.getDomainName());
        } catch (Status512ProviderNotConfiguredException e) {
            providerAuthorizationResult.setErrorMessage(e.getMessage());
            providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
            providerAuthorizationResult.setRawDataList(rawAuthorizationDataList);
            providerAuthorizationResult.setData(data);
            return providerAuthorizationResult;
        }

        if (ObjectUtils.isEmpty(providerConfig)) {
            data.put("message", RegistrationError.PROVIDER_NOT_CONFIGURED.getResponseMessageLocal(messageSource, request.getDomainName()));
            providerAuthorizationResult.setRawDataList(rawAuthorizationDataList);
            providerAuthorizationResult.setData(data);
            providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
            return providerAuthorizationResult;
        }

        try {
            final String baseUrl = providerConfig.getRecaptchaServiceUrl() + "?secret=" + providerConfig.getSecretKey()
                    + "&response=" + clientRecaptchaResponse;
            RestTemplate restTemplate = restService.restTemplate(
                    providerConfig.getConnectTimeout(),
                    providerConfig.getConnectionRequestTimeout(),
                    providerConfig.getSocketTimeout());
            ResponseEntity<ReCaptchaApiResponse> responseHttpEntity = restTemplate.getForEntity(baseUrl, ReCaptchaApiResponse.class);
            rawAuthorizationDataList.add(RawAuthorizationData.builder().rawRequestToProvider(baseUrl).rawResponseFromProvider(JsonStringify.objectToString(responseHttpEntity.getBody())).build());

            if (responseHttpEntity.getBody().isSuccess()) {
                if (providerConfig.getScore() >= 0 && providerConfig.getScore() <= 10) {
                    data.put("reCaptchaResult", String.valueOf(responseHttpEntity.getBody().getScore() >= (providerConfig.getScore() / 10)));
                    providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.ACCEPT);
                } else {
                    data.put("reCaptchaResult", "false");
                    providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
                    data.put("message", RegistrationError.INVALID_PARAMETER
                            .getResponseMessageLocal(messageSource, request.getDomainName(), new Object[]{"Invalid Score = " + clientRecaptchaResponse}));
                }
            } else {
                data.put("reCaptchaResult", "false");
                data.put("message", responseHttpEntity.getBody().getErrorCodes().get(0));
                providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
            }
        } catch (RestClientException e) {
            data.put("message", "Could not communicate with Google Recaptcha service");
            providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.TIMEOUT);
        } catch (Exception e) {
            log.error("Exception when executing google v3 ReCaptcha = " + providerConfig.getRecaptchaServiceUrl() + " (" + request.getDomainName() + ")", e);
            data.put("ReCaptcha Pass", "false");
            data.put("message", "Exception when executing google v3 ReCaptcha");
            providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
        }
        providerAuthorizationResult.setRawDataList(rawAuthorizationDataList);
        providerAuthorizationResult.setData(data);
        return providerAuthorizationResult;
    }

    private RecaptchaProviderConfig getConfig(String domainName) throws Status512ProviderNotConfiguredException {
        ProviderClient cl = getProviderService();
        Response<Iterable<ProviderProperty>> pp =
                cl.propertiesByProviderUrlAndDomainName("service-access-provider-google-recaptcha", domainName);

        if (!pp.isSuccessful() || pp.getData() == null) {
            throw new Status512ProviderNotConfiguredException(domainName);
        }

        RecaptchaProviderConfig config = new RecaptchaProviderConfig();
        for (ProviderProperty p : pp.getData()) {
            if (p.getName().equalsIgnoreCase("secret_key")) {
                config.setSecretKey(p.getValue());
            }
            if (p.getName().equalsIgnoreCase("site_key")) {
                config.setSiteKey(p.getValue());
            }
            if (p.getName().equalsIgnoreCase("recaptcha_service_url")) {
                config.setRecaptchaServiceUrl(p.getValue());
            }
            if (p.getName().equalsIgnoreCase("score")) {
                config.setScore(interpretScore(p.getValue(), domainName));
            }
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.CONNECTION_REQUEST_TIMEOUT.getValue()) && !StringUtil.isEmpty(p.getValue()) && StringUtil.isNumeric(p.getValue())) {
                config.setConnectionRequestTimeout(Integer.parseInt(p.getValue()));
            }
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.CONNECT_TIMEOUT.getValue()) && !StringUtil.isEmpty(p.getValue()) && StringUtil.isNumeric(p.getValue())) {
                config.setConnectTimeout(Integer.parseInt(p.getValue()));
            }
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.SOCKET_TIMEOUT.getValue()) && !StringUtil.isEmpty(p.getValue()) && StringUtil.isNumeric(p.getValue())) {
                config.setSocketTimeout(Integer.parseInt(p.getValue()));
            }
        }

        if (ObjectUtils.isEmpty(config.getRecaptchaServiceUrl())) {
            throw new Status512ProviderNotConfiguredException(domainName + " on ReCaptcha Url");
        }

        if (ObjectUtils.isEmpty(config.getSecretKey())) {
            throw new Status512ProviderNotConfiguredException(domainName + " on secret key");
        }

        if (ObjectUtils.isEmpty(config.getSiteKey())) {
            throw new Status512ProviderNotConfiguredException(domainName + " on site key");
        }

        return config;
    }

    private ProviderClient getProviderService() {
        ProviderClient cl = null;
        try {
            cl = lithiumServiceClientFactory.target(ProviderClient.class, "service-domain", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting provider properties", e);
        }
        return cl;
    }

    private double interpretScore(String scoreProvided, String domainName) throws Status512ProviderNotConfiguredException {
        double scoreAsInt = Double.parseDouble(scoreProvided) / 10;
        if (scoreAsInt > 1.0) {
            throw new Status512ProviderNotConfiguredException(domainName + " on score");
        }
        return scoreAsInt;
    }

}
