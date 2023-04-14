package lithium.service.document.provider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.modules.ModuleInfo;
import lithium.service.document.provider.api.exceptions.Status411FailAuthInHelloSodaServiceException;
import lithium.service.document.provider.api.exceptions.Status412FailToGetJobDetailsException;
import lithium.service.document.provider.api.exceptions.Status413FailToGetSessionTokenFromHelloSodaServiceException;
import lithium.service.document.provider.api.exceptions.Status414FailFromHelloSodaServiceException;
import lithium.service.document.provider.api.exceptions.Status540ProviderNotConfiguredException;
import lithium.service.document.provider.api.schema.AuthResponse;
import lithium.service.document.provider.api.schema.CommitRequest;
import lithium.service.document.provider.api.schema.Externals;
import lithium.service.document.provider.api.schema.GetSessionTokenRequest;
import lithium.service.document.provider.api.schema.JobData;
import lithium.service.document.provider.api.schema.JobRequest;
import lithium.service.document.provider.api.schema.JobResponse;
import lithium.service.document.provider.api.schema.ReportResponse;
import lithium.service.document.provider.api.schema.SessionCreateRequest;
import lithium.service.document.provider.api.schema.SessionCreateResponse;
import lithium.service.document.provider.api.schema.User;
import lithium.service.document.provider.config.ProviderConfig;
import lithium.service.document.provider.config.ProviderConfigService;
import lithium.service.user.client.objects.Address;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static java.util.Objects.nonNull;
import static lithium.service.document.provider.Utils.toJson;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Service
@Slf4j
@AllArgsConstructor
public class HelloSodaDocumentVerificationServiceImpl implements HelloSodaDocumentService {
    private final ProviderConfigService providerConfigService;
    private final ModuleInfo moduleInfo;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    @Override
    public String attachUserInformationToJob(lithium.service.user.client.objects.User user, String domainName, String notifyUrl,
                                             String sessionId, String jobId, String clientId) throws Status540ProviderNotConfiguredException, Status414FailFromHelloSodaServiceException {
        final ProviderConfig providerConfig = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            add("content-type", "application/json");
            add("Authorization", "Bearer " + providerConfig.getProfileBearer());
        }};

        Address residentialAddress = user.getResidentialAddress();
        User.Home home = null;
        if (nonNull(residentialAddress)) {
            home = User.Home.builder()
                    .city(residentialAddress.getCity())
                    .line1(residentialAddress.getAddressLine1())
                    .country(residentialAddress.getCountry())
                    .postcode(residentialAddress.getPostalCode())
                    .countryCode(residentialAddress.getCountryCode())
                    .build();
        }
        User userData = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthdate(user.getDateOfBirth().toString("YYYY-MM-dd"))
                .home(home)
                .build();
        JobRequest jobRequest = JobRequest.builder()
                .applicationId(clientId)
                .data(userData)
                .consumerId(user.guid())
                .notifyUrl(notifyUrl)
                .externals(new Externals(new Externals.IdCheck(sessionId)))
                .build();

        log.debug("Prepared job request: " + jobRequest);
        ResponseEntity<Object> response =
                restTemplate.exchange(providerConfig.getProfileApiUrl() + "/jobs/" + jobId,
                        PUT, new HttpEntity<>(toJson(jobRequest), headers), Object.class, new HashMap<>());
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Got wrong request from hello soda (" + response.getStatusCode().value() + ") " + response.getBody());
            throw new Status414FailFromHelloSodaServiceException(response.getBody().toString());
        }
        log.debug("Got Job response: " + response.getBody());
        JobResponse body = mapper.convertValue(response.getBody(), JobResponse.class);
        return body.getJobId();
    }

    @Override
    public JobData getJobDetailsById(String jobId, String domainName) throws Status540ProviderNotConfiguredException, Status412FailToGetJobDetailsException {
        ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            add("Authorization", "Bearer " + config.getProfileBearer());
        }};
        String profileApiUrl = config.getProfileApiUrl();
        ResponseEntity<JobData> exchange = restTemplate.exchange(profileApiUrl + "/jobs/" + jobId, GET, new HttpEntity<>(null, headers), JobData.class, new HashMap<>());
        if (exchange.getStatusCodeValue() != 200) {
            log.warn("Fail to get job details: " + exchange.getBody());
            throw new Status412FailToGetJobDetailsException();
        }
        return exchange.getBody();
    }

    @Override
    public ReportResponse getReportByJobId(String jobId, String domainName) throws Status540ProviderNotConfiguredException, Status412FailToGetJobDetailsException {
        ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            add("Authorization", "Bearer " + config.getProfileBearer());
        }};
        String profileApiUrl = config.getProfileApiUrl();

        ResponseEntity<Object> exchange = restTemplate.exchange(profileApiUrl + "/jobs/" + jobId + "/report", GET, new HttpEntity<>(null, headers), Object.class, new HashMap<>());
        log.info("Report: " + exchange.getBody());
        if (exchange.getStatusCodeValue() != 200) {
            log.warn("Fail to get job details: " + exchange.getBody());
            throw new Status412FailToGetJobDetailsException();
        }

        return nonNull(exchange.getBody()) ? mapper.convertValue(exchange.getBody(), ReportResponse.class) : null;
    }

    @Override
    public byte[] getFrontSideImage(String sessionToken, String domainName) throws Status540ProviderNotConfiguredException, Status411FailAuthInHelloSodaServiceException {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            add("Authorization", "Bearer " + getApiToken(domainName));
        }};
        ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
        String iDocufyApiUrl = config.getIDocufyApiUrl();
        return restTemplate.exchange(iDocufyApiUrl + "/sessions/" + sessionToken + "/images/front", GET, new HttpEntity<>(null, headers), byte[].class, new HashMap<>())
                .getBody();
    }

    @Override
    public byte[] getBackSideImage(String sessionToken, String domainName) throws Status540ProviderNotConfiguredException, Status411FailAuthInHelloSodaServiceException {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            add("Authorization", "Bearer " + getApiToken(domainName));
        }};
        ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
        String iDocufyApiUrl = config.getIDocufyApiUrl();

        return restTemplate.exchange(iDocufyApiUrl + "/sessions/" + sessionToken + "/images/back", GET, new HttpEntity<>(null, headers), byte[].class, new HashMap<>())
                .getBody();
    }

    @Override
    public String getSessionToken(String domainName, String jobId, String helloSodaApiToken) throws Status540ProviderNotConfiguredException, Status413FailToGetSessionTokenFromHelloSodaServiceException {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            add("content-type", "application/json");
            add("Authorization", "Bearer " + helloSodaApiToken);
        }};

        ProviderConfig providerConfig = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

        String iDocufyApiUrl = providerConfig.getIDocufyApiUrl();
        String productId = providerConfig.getProductId();
        SessionCreateRequest sessionCreateRequest = new SessionCreateRequest(productId, jobId, "authentication");

        ResponseEntity<SessionCreateResponse> exchange = restTemplate.exchange(iDocufyApiUrl + "/sessions",
                POST, new HttpEntity<>(toJson(sessionCreateRequest), headers), SessionCreateResponse.class, new HashMap<>());

        SessionCreateResponse response = exchange.getBody();
        if (exchange.getStatusCodeValue() != 201 && response.getStatus() != 201 && !response.getSuccess()) {
            log.warn("Can't create hello soda session token: " + exchange.getBody());
            throw new Status413FailToGetSessionTokenFromHelloSodaServiceException();
        }

        return response.getData().getSession().get_id();
    }

    @Override
    public String getApiToken(String domainName) throws Status540ProviderNotConfiguredException, Status411FailAuthInHelloSodaServiceException {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            add("content-type", "application/json");
        }};

        ProviderConfig providerConfig = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
        String iDocufyApiUrl = providerConfig.getIDocufyApiUrl();
        String requestBody = toJson(new GetSessionTokenRequest(providerConfig.getProductId()));

        ResponseEntity<AuthResponse> exchange = restTemplate.exchange(iDocufyApiUrl + "/auth",
                POST, new HttpEntity<>(requestBody, headers), AuthResponse.class, new HashMap<>());

        if (exchange.getStatusCodeValue() != 200 && exchange.getBody().getStatus() != 200 && !exchange.getBody().getSuccess()) {
            log.warn("Failed to authenticate and get api token from Hello soda service: " + exchange.getBody());
            throw new Status411FailAuthInHelloSodaServiceException();
        }

        return exchange.getBody().getData().getToken();
    }

    @Override
    public String commitJob(String domainName, String jobId, String sessionId) throws Status540ProviderNotConfiguredException, Status414FailFromHelloSodaServiceException {
        final ProviderConfig providerConfig = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>() {{
            add("content-type", "application/json");
            add("Authorization", "Bearer " + providerConfig.getProfileBearer());
        }};

        String requestBody = toJson(new CommitRequest(new Externals(new Externals.IdCheck(sessionId)), new CommitRequest.ReportParameters("1m"), true));
        log.debug("Commit job request: " + requestBody);

        ResponseEntity<String> exchange = restTemplate.exchange(providerConfig.getProfileApiUrl() + "/jobs/" + jobId, PUT, new HttpEntity<>(requestBody, headers), String.class, new HashMap<>());
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.error("Got wrong request from hello soda (" + exchange.getStatusCode().value() + ") " + exchange.getBody());
            throw new Status414FailFromHelloSodaServiceException(exchange.getBody());
        }
        log.debug("Commit job response: " + exchange.getBody());
        return exchange.getBody();
    }
}
