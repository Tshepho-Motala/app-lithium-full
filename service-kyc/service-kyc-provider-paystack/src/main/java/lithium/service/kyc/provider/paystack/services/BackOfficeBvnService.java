package lithium.service.kyc.provider.paystack.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.ErrorCodeException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.kyc.provider.paystack.data.objects.BvnResolveResponse;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class BackOfficeBvnService {

    private final PaystackService paystackService;
    private final ChangeLogService changeLogService;
    private final ApiService apiService;
    private final VerificationResultService verificationResultService;

    public Response<BvnResolveResponse> checkBvn(String bvn, String userGuid, String ipAddress, LithiumTokenUtil tokenUtil) throws Exception {
        BvnResolveResponse bvnResolveResponse = null;
        String comment = null;
        try {
            bvnResolveResponse = paystackService.bvnResolveBvn(bvn, userGuid);
            saveChangeLog(userGuid, ipAddress, bvnResolveResponse, tokenUtil);
            log.info("{}", bvnResolveResponse);
            return Response.<BvnResolveResponse>builder().data(bvnResolveResponse).status(Response.Status.OK_SUCCESS).build();
        } catch (ErrorCodeException ex) {
            comment = "Error verifying bvn due " + ex.getMessage();
            log.error("Error verifying bvn={}, userGuid={}: {}",bvn, userGuid,  String.join("\\n", ExceptionUtils.getRootCauseStackTrace(ex)));
            return Response.<BvnResolveResponse>builder().message(ex.getMessage()).status(Response.Status.fromId(ex.getCode())).build();
        } finally {
            verificationResultService.sendVerificationAttempt(bvnResolveResponse, userGuid, true, comment);
        }
    }

    private void saveChangeLog(String userGuid, String ipAddress,BvnResolveResponse bvnResolveResponse, LithiumTokenUtil tokenUtil) throws Exception {
        User user=apiService.getUser(userGuid);
        List<ChangeLogFieldChange> clfc = new ArrayList<>();
        String result = apiService.bvnResponseString(bvnResolveResponse);
        changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), tokenUtil.guid(), tokenUtil,
            result, "IP: "+ipAddress, clfc, Category.ACCOUNT, SubCategory.KYC, 1, user.getDomain().getName());
    }

}
