package lithium.service.kyc.provider.paystack.controllers.admin;

import lithium.service.Response;
import lithium.service.kyc.provider.exceptions.Status400BadRequestException;
import lithium.service.kyc.provider.paystack.data.objects.BvnResolveResponse;
import lithium.service.kyc.provider.paystack.data.objects.BvnVerificationRequest;
import lithium.service.kyc.provider.paystack.services.BackOfficeBvnService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/backoffice/kyc")
public class BackOfficeBvnController {

    @Autowired
    private BackOfficeBvnService backOfficeBvnService;

    @PostMapping("/verify-bvn")
    public Response<BvnResolveResponse> adminVerifyBvn(@RequestBody BvnVerificationRequest bvnRequest,
                                                       HttpServletRequest request,
                                                       LithiumTokenUtil tokenUtil) throws Exception {
        log.debug("{}", bvnRequest);
        if (bvnRequest == null || bvnRequest.getBvn() == null || bvnRequest.getUserGuid() == null) {
            String message = "The request contained a document that could not be parsed due to a syntax error. Please do not resubmit before changing the content";
            throw new Status400BadRequestException(message);
        }
        return backOfficeBvnService.checkBvn(bvnRequest.getBvn(),bvnRequest.getUserGuid(), request.getRemoteAddr(), tokenUtil);
    }

}
