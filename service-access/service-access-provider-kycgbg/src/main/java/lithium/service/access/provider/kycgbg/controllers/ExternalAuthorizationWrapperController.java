package lithium.service.access.provider.kycgbg.controllers;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.service.Response;
import lithium.service.access.client.ExternalAuthorizationClient;
import lithium.service.access.client.exceptions.Status513InvalidDomainConfigurationException;
import lithium.service.access.client.objects.EAuthorizationOutcome;
import lithium.service.access.client.objects.ExternalAuthorizationRequest;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.provider.kycgbg.config.ResponseObj;
import lithium.service.access.provider.kycgbg.services.ApiService;
import lithium.service.access.provider.kycgbg.services.VerificationResultService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static lithium.service.Response.Status.OK;

@Slf4j
@RestController
@RequestMapping("/system")
public class ExternalAuthorizationWrapperController implements ExternalAuthorizationClient {
    @Autowired
    ApiController apiController;
    @Autowired
    LithiumServiceClientFactory services;
    @Autowired
    ChangeLogService changeLogService;
    @Autowired
    ApiService kycService;


    @Override
    @RequestMapping(path = "/checkAuthorization")
    public Response<ProviderAuthorizationResult> checkAuthorization(
            @RequestBody ExternalAuthorizationRequest externalAuthorizationRequest) throws Status513InvalidDomainConfigurationException {
        final Response<ResponseObj> responseObjResponse = apiController.checkAuth(
                "service-access-provider-kycgbg",
                externalAuthorizationRequest.getDomainName()
        );
        ArrayList<RawAuthorizationData> rawDataList = new ArrayList<>();
        rawDataList.add(responseObjResponse.getData().getRawAuthorizationData());

        ProviderAuthorizationResult providerAuthorizationResult = new ProviderAuthorizationResult();
        providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.REJECT); // Default init just to be safe
        if (responseObjResponse.isSuccessful()) {
            if (responseObjResponse.getData().isSuccess()) { // allow
                final Response<List<ResponseObj>> autenticateResponse = apiController.requestAuthenticateSP(
                        "service-access-provider-kycgbg",
                        externalAuthorizationRequest.getDomainName(),
                        externalAuthorizationRequest.getUserGuid());

                if (autenticateResponse.isSuccessful()) {
                    User user = kycService.getUser(externalAuthorizationRequest.getUserGuid());
                    for (ResponseObj respObj : autenticateResponse.getData()) {
                        String comment = "User " + externalAuthorizationRequest.getUserGuid() + " Verification call using the GBG service is successful. The result of the check: \"" + respObj.getBandtext() + "\" Number of points scored: " + respObj.getScorePoints();
                        rawDataList.add(respObj.getRawAuthorizationData());
                        if (respObj.getBandtext().equalsIgnoreCase(ApiController.BAND_TEXT_PASS) || respObj.getBandtext().equalsIgnoreCase(ApiController.BAND_TEXT_PASS_AV)) {
                            providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.ACCEPT);
                            try {
                                changeLogService.registerChangesWithDomain("user.verification", "enable", user.getId(), "GBG_KYC_Service", comment, "Sent from GBG KyC service", null, Category.ACCOUNT,
                                        SubCategory.KYC, 0, user.getDomain().getName());
                            } catch (Exception e) {
                                log.error("can't register Account verification changes" + e.getMessage());
                            }
                        } else if (respObj.getBandtext().equalsIgnoreCase(ApiController.BAND_TEXT_REFER)) {
                            providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.REVIEW);
                            try {
                                changeLogService.registerChangesWithDomain("user.verification", "edit", user.getId(), "GBG_KYC_Service", comment, "Sent from GBG KyC service", null, Category.ACCOUNT,
                                        SubCategory.KYC, 0, user.getDomain().getName());
                            } catch (Exception e) {
                                log.error("can't register Account verification changes" + e.getMessage());
                            }
                        } else {
                            providerAuthorizationResult.setAuthorisationOutcome(EAuthorizationOutcome.REJECT);
                            providerAuthorizationResult.setErrorMessage(respObj.getBandtext());
                            try {
                                changeLogService.registerChangesWithDomain("user.verification", "disable", user.getId(), "GBG_KYC_Service", comment, "Sent from GBG KyC service", null, Category.ACCOUNT,
                                        SubCategory.KYC, 0, user.getDomain().getName());
                            } catch (Exception e) {
                                log.error("can't register Account verification changes" + e.getMessage());
                            }
                        }
                    }
                }
            } else { // deny
                providerAuthorizationResult.setErrorMessage("Authentication failure for KYC provider username or password");
            }
        }
        providerAuthorizationResult.setRawDataList(rawDataList);
        log.debug("AuthorizationResult : " + providerAuthorizationResult);
        return Response.<ProviderAuthorizationResult>builder().data(providerAuthorizationResult).status(OK).build();
    }
}

