package lithium.service.user.services;

import lithium.service.Response;
import lithium.service.user.exceptions.Status400BadRequestException;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.raf.client.RAFClient;
import lithium.service.raf.client.objects.Referral;
import lithium.service.raf.client.objects.ReferralBasic;
import lithium.service.raf.client.objects.ReferralConversion;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserApiToken;
import lombok.extern.slf4j.Slf4j;

import static lithium.service.Response.Status.OK_SUCCESS;

@Service
@Slf4j
public class ReferralService {
	@Autowired LithiumServiceClientFactory factory;
	@Autowired UserApiTokenService userTokenService;
	@Autowired MessageSource messageSource;
	
	private RAFClient getRAFClient() {
		RAFClient client = null;
		try {
			client = factory.target(RAFClient.class, "service-raf", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return client;
	}
	
	public Referral addReferral(String referrerGuid, String playerGuid) {

		return getRAFClient().add(
				ReferralBasic.builder()
						.referrerGuid(lookupReferrerCode(referrerGuid))
						.playerGuid(playerGuid)
						.build())
				.getData();
	}

	public Response<ReferralConversion> addReferralAfterSignUp(String referrerGuid, User user) throws Exception {
		ReferralConversion referralConversion=new ReferralConversion();
		try{
			referrerGuid=lookupValidReferrerCode(referrerGuid);
		} catch (IllegalArgumentException ex){
		  log.debug("IllegalArgumentException: message: " + ex.getMessage() + ", referrerGuid: " + referrerGuid);
			throw new Status400BadRequestException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.INVALID_REFERRER_CODE", new Object[] {new lithium.service.translate.client.objects.Domain(user.domainName()), referrerGuid}, "Invalid referrer code.", LocaleContextHolder.getLocale()), ex.getStackTrace());
		}

		String shortGuid = user.getUserApiToken().getShortGuid();
		if ( shortGuid != null && shortGuid.equalsIgnoreCase(referrerGuid)) {
			throw new Status400BadRequestException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.CANNOT_REDEEM_OWN_CODE", new Object[] {new lithium.service.translate.client.objects.Domain(user.domainName()), referrerGuid}, "Cannot redeem own code {0}.", LocaleContextHolder.getLocale()));
		}

		try{
			referralConversion = getRAFClient().addReferralAfterSignUp(
					ReferralBasic.builder()
							.referrerGuid(lookupReferrerCode(referrerGuid))
							.playerGuid(user.guid())
							.build())
					.getData();
			return Response.<ReferralConversion>builder().message(referralConversion.getMessage()).status(OK_SUCCESS).build();
		}catch (Exception ex){
			log.error("Error adding referral. code {} {}",referrerGuid,ex);
			throw new Status500InternalServerErrorException("Failed to add referral for" + referrerGuid);
		}


	}

	/**
	 * Lookup the shortGuid field in the user API table to resolve the referrer user guid.
	 * If the lookup fails, we assume the code is already the user guid.
	 * @param referrerGuidOrReferrerCode
	 * @return
	 */
	private String lookupReferrerCode(final String referrerGuidOrReferrerCode) {
		UserApiToken token = userTokenService.findByShortGuid(referrerGuidOrReferrerCode);

		if (token == null || token.getShortGuid() == null || token.getShortGuid().trim().isEmpty()) {
			return referrerGuidOrReferrerCode;
		} else {
			return token.getGuid();
		}
	}

	private String lookupValidReferrerCode(final String referrerGuidOrReferrerCode) {
		UserApiToken token = userTokenService.findByShortGuid(referrerGuidOrReferrerCode);
		if(token != null && StringUtils.isNotBlank(token.getShortGuid())){
			return referrerGuidOrReferrerCode;
		}
		token=userTokenService.findByGuid(referrerGuidOrReferrerCode);
		if(token != null){
			return referrerGuidOrReferrerCode;
		}
		log.error("Invalid Referrer Code {}",referrerGuidOrReferrerCode);
		throw new IllegalArgumentException("Invalid Referrer Code " +referrerGuidOrReferrerCode);
	}
}
