package lithium.service.raf.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import lithium.service.raf.enums.ReferralConversionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.raf.client.objects.ReferralBasic;
import lithium.service.raf.data.entities.Referral;
import lithium.service.raf.services.ReferralService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/referral")
@Slf4j
public class ReferralController {
	@Autowired ReferralService service;
	
	@PostMapping
	public Response<Referral> add(@RequestBody ReferralBasic referralBasic) {
		Referral referral = null;
		try {
			referral = service.add(referralBasic.getReferrerGuid(), referralBasic.getPlayerGuid());
			return Response.<Referral>builder().data(referral).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Referral>builder().status(INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/add-after-signup")
	public Response<ReferralConversionStatus>addReferralAfterSignUp(@RequestBody ReferralBasic referralBasic){
		try {
			ReferralConversionStatus result = service.addReferralAfterSignUp(referralBasic.getReferrerGuid(), referralBasic.getPlayerGuid());
			return Response.<ReferralConversionStatus>builder().data(result).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<ReferralConversionStatus>builder().status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
//	@PostMapping("/markConverted")
//	public Response<Referral> markConverted(@RequestParam("domainName") String domainName, @RequestParam("userName") String userName) {
//		Referral referral = null;
//		try {
//			referral = service.markConverted(domainName, userName);
//			return Response.<Referral>builder().data(referral).status(OK).build();
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return Response.<Referral>builder().status(INTERNAL_SERVER_ERROR).build();
//		}
//	}
}
