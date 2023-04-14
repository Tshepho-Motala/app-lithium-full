package lithium.service.kyc.provider.paystack.controllers;

import lithium.service.Response;
import lithium.service.kyc.provider.exceptions.Status400BadRequestException;
import lithium.service.kyc.provider.exceptions.Status406InvalidVerificationNumberException;
import lithium.service.kyc.provider.exceptions.Status424KycVerificationUnsuccessfulException;
import lithium.service.kyc.provider.exceptions.Status425IllegalUserStateException;
import lithium.service.kyc.provider.exceptions.Status426PlayerUnderAgeException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.exceptions.Status520KycProviderEndpointException;
import lithium.service.kyc.provider.paystack.data.objects.BvnVerificationRequest;
import lithium.service.kyc.provider.paystack.services.PaystackService;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/frontend")
public class BvnVerificationController  {

    @Autowired
    private PaystackService paystackService;

    @PostMapping("/kyc/verify-bvn")
    public Response<VerificationStatus> verifyBvn(@RequestBody BvnVerificationRequest bvnRequest, LithiumTokenUtil tokenUtil) throws Status406InvalidVerificationNumberException, Status520KycProviderEndpointException, Status425IllegalUserStateException, UserClientServiceFactoryException, Status426PlayerUnderAgeException, Status424KycVerificationUnsuccessfulException, Status512ProviderNotConfiguredException, Status400BadRequestException {
        log.debug("{}", bvnRequest);
        if (bvnRequest == null || bvnRequest.getBvn() == null) {
            throw new Status406InvalidVerificationNumberException("Invalid Bvn Number");
        }
        log.debug("Token {}", tokenUtil);
        VerificationStatus verificationStatus = paystackService.verifyBvn(bvnRequest.getBvn(), tokenUtil.guid());
        return Response.<VerificationStatus>builder().data(verificationStatus).status(Response.Status.OK_SUCCESS).build();
    }

}
