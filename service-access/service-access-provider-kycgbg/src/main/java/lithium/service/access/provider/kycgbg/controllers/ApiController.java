package lithium.service.access.provider.kycgbg.controllers;

import com.id3global.id3gws._2013._04.CheckCredentialsResponseElement;
import lithium.service.Response;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.provider.kycgbg.adapter.KycAdapter;
import lithium.service.access.provider.kycgbg.config.APIAuthentication;
import lithium.service.access.provider.kycgbg.config.GbgResponseData;
import lithium.service.access.provider.kycgbg.config.ResponseObj;
import lithium.service.access.provider.kycgbg.services.VerificationResultService;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class ApiController extends BaseController {
    public static final String BAND_TEXT_ALERT = "ALERT";
    public static final String BAND_TEXT_REFER = "REFER";
    public static final String BAND_TEXT_PASS = "PASS";
    public static final String BAND_TEXT_PASS_AV = "PASS AV";
    public static final String BAND_TEXT_FAIL = "FAIL";
    public static final String BAND_TEXT_REVIEW = "REVIEW";
    public static final String REQUEST_AUTHENTICATE_SP_PATH = "/Soap11_Auth";
    public static final String CHECK_CREDENTIALS_PATH = "/Soap11_NoAuth";

    @Autowired
    VerificationResultService verificationResultService;

    @RequestMapping("/checkAuth")
    public Response<ResponseObj> checkAuth(
            @PathVariable("providerUrl") String providerUrl,
            @PathVariable("domainName") String domainName
    ) {
        boolean valid = false;
        APIAuthentication apiAuth = getAPIAuthentication(providerUrl, domainName);
        String username = apiAuth.getBrandConfiguration().getUsername();
        String password = apiAuth.getBrandConfiguration().getPassword();
        log.debug("Reached checkAuth (Credentials Below)");
        String url = apiAuth.getBrandConfiguration().getBaseUrl() + CHECK_CREDENTIALS_PATH;
        log.debug(url);
        RawAuthorizationData rawAuthorizationData = new RawAuthorizationData();
        try {
            CheckCredentialsResponseElement responseElement = kycService.checkCredentials(
                    adapter(providerUrl, domainName),
                    username,
                    password,
                    url,
                    rawAuthorizationData
            );
            log.debug("CheckCredentialsResponseElement : " + responseElement);
            if (responseElement.getCheckCredentialsResult().getValue().getAccountID() != null) valid = true;
        } catch (Exception e) {
            log.warn("Problem in processing credentials check. Pretending like everything is ok for domain: " + domainName, e);
        } finally {
            ResponseObj responseObj = ResponseObj.builder()
                    .success(valid)
                    .rawAuthorizationData(rawAuthorizationData)
                    .build();
            return Response.<ResponseObj>builder().status(Response.Status.OK).data(responseObj).build();
        }
    }

    @RequestMapping("/requestAuthenticateSP")
    public Response<List<ResponseObj>> requestAuthenticateSP(
            @PathVariable("providerUrl") String providerUrl,
            @PathVariable("domainName") String domainName,
            @RequestParam String playerguid
    ) {
        APIAuthentication apiAuth = getAPIAuthentication(providerUrl, domainName);
        String profileID = apiAuth.getBrandConfiguration().getProfileId();
        String pepSanctionsID = apiAuth.getBrandConfiguration().getPepSancID();
        String username = apiAuth.getBrandConfiguration().getUsername();
        String password = apiAuth.getBrandConfiguration().getPassword();
        String url = apiAuth.getBrandConfiguration().getBaseUrl() + REQUEST_AUTHENTICATE_SP_PATH;
        //String playerguid = domainName + "/" + principal.getName();
        User user = kycService.getUser(playerguid);
        RawAuthorizationData rawAuthorizationData = new RawAuthorizationData();
        RawAuthorizationData rawAuthorizationDataAlert = new RawAuthorizationData();
        List<ResponseObj> responseObjList = new ArrayList<>();

        if (user == null) {
            return null;
        }

        KycAdapter adapter = adapter(providerUrl, domainName);
        // TODO: 2019/10/29 We need to cater not just for Canada in future, so we need to think about a way to manage differnt countries
        GbgResponseData responseData = kycService.authenticateSP(adapter, user, profileID, username, password, url, rawAuthorizationData);
        ResponseObj resultObj = ResponseObj.builder()
                .type("GBG CHECK")
                .bandtext(responseData.getBandText())
                .scorePoints(responseData.getScorePoints())
                .rawAuthorizationData(rawAuthorizationData)
                .build();
        responseObjList.add(resultObj);
        boolean success = responseData.getBandText().equalsIgnoreCase(ApiController.BAND_TEXT_PASS) || responseData.getBandText().equalsIgnoreCase(ApiController.BAND_TEXT_PASS_AV);
        try {
            verificationResultService.sendVerificationAttempt(responseData, user, success, responseData.getBandText());
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Cant store GBG verification result for user:" + user.getGuid() + " Exception:" + Arrays.toString(e.getStackTrace()));
        }
        if (resultObj.getBandtext().equalsIgnoreCase(BAND_TEXT_ALERT) && !apiAuth.getBrandConfiguration().getPepSancID().isEmpty()) {
            GbgResponseData pepResponseData = kycService.authenticateSP(adapter, user, pepSanctionsID, username, password, url, rawAuthorizationDataAlert);
            ResponseObj resultObjAlert = ResponseObj.builder()
                    .type("PEP&SANCTIONS")
                    .bandtext(pepResponseData.getBandText())
                    .scorePoints(pepResponseData.getScorePoints())
                    .rawAuthorizationData(rawAuthorizationDataAlert)
                    .build();
            responseObjList.add(resultObjAlert);
            success = pepResponseData.getBandText().equalsIgnoreCase(ApiController.BAND_TEXT_PASS) || pepResponseData.getBandText().equalsIgnoreCase(ApiController.BAND_TEXT_PASS_AV);
            try {
                verificationResultService.sendVerificationAttempt(pepResponseData, user, success, "PEP&SANCTION request:"+ responseData.getBandText());
            } catch (LithiumServiceClientFactoryException e) {
                log.error("Cant store GBG verification result for user:" + user.getGuid() + " Exception:" + Arrays.toString(e.getStackTrace()));
            }
        }
        return Response.<List<ResponseObj>>builder().data(responseObjList).build();
    }

// Should sensitive data be exposed on a rest endpoint ..
//	@RequestMapping("/getBrandConfig")
//	public BrandsConfigurationBrand getBrandConfig(@PathVariable("providerUrl") String providerUrl, @PathVariable("domainName") String domainName) {
//		log.info("Reached getBrandConfig");
//		return kycService.getBrandConfiguration(providerUrl, domainName);
//	}

}
