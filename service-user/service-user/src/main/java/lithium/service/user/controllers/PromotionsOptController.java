package lithium.service.user.controllers;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.client.objects.PubSubEventOrigin;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.PubSubMarketingPreferences;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.PromotionsOptService;
import lithium.service.user.services.PubSubUserService;
import lithium.service.user.services.oauthClient.OauthApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

@RestController
@RequestMapping("/promotions/opt/{hash}")
@Slf4j
public class PromotionsOptController {
	@Autowired PromotionsOptService promotionsOptService;
  @Autowired PubSubUserService pubSubUserService;
  @Autowired OauthApiInternalClientService oauthApiInternalClientService;
	
	@Retryable(backoff=@Backoff(500), maxAttempts=10)
	@PostMapping
	public Response<Boolean> opt(@RequestHeader("Authorization") String authorization,
      @PathVariable String hash, HttpServletRequest request, LithiumTokenUtil util)
      throws Status401UnAuthorisedException {
		try {
			String decodedHash = new String(Base64.getDecoder().decode(hash.getBytes()), "UTF-8");
			log.debug("Original hash " + hash + " Decoded hash " + decodedHash);
			String hashValues[] = decodedHash.split("&");
			Long userId = Long.parseLong(promotionsOptService.getValue(hashValues[0]));
			String guid = promotionsOptService.getValue(hashValues[1]);
			String fullName = promotionsOptService.getValue(hashValues[2]);
			String emailAddress = promotionsOptService.getValue(hashValues[3]);
			String cell = promotionsOptService.getValue(hashValues[4]);
			Boolean optOut = Boolean.parseBoolean(promotionsOptService.getValue(hashValues[5]));
			String method = promotionsOptService.getValue(hashValues[6]);
			log.debug("UserId " + userId + " Guid " + guid + " Name " + fullName + " Email " + emailAddress + " Cell " + cell + " OptOut " + optOut);
			User user = promotionsOptService.getUserById(userId);
			if (user == null) throw new Exception("No user with id " + userId + " found!");
			String userGuid = user.getDomain().getName()+"/"+user.getUsername();
			if ((!userGuid.equalsIgnoreCase(guid)) ||
					(user.getEmail() != null && !user.getEmail().equalsIgnoreCase(emailAddress)) ||
					(user.getCellphoneNumber() != null && !user.getCellphoneNumber().equalsIgnoreCase(cell))) {
				throw new Exception("Values mismatch!");
			}
			String ipAddress = request.getRemoteAddr();
			if (request.getHeader("X-Forwarded-For") != null) {
				ipAddress = request.getHeader("X-Forwarded-For");
			}

      if (util == null) {
        try {
          oauthApiInternalClientService.validateClientAuth(authorization);
        } catch (Exception ex) {
          log.debug("Invalidated client auth : " + ex.getMessage());
          throw new Status401UnAuthorisedException("Invalid client auth promotions/opt");
        }

        String clientId = oauthApiInternalClientService.getClientId(authorization);
        log.info("Marketing Optout (by " + clientId + ") :: " + "method: " + method + ", optOut:" + optOut
            + ", optOut for userGuid:" + userGuid + ", ip: " + ipAddress);
      } else {
        log.info("Marketing Optout (by " + util.guid() + ") :: " + "method: " + method + ", optOut:" + optOut
            + ", optOut for userGuid:" + userGuid + ", ip: " + ipAddress);
      }

      PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder pubSubMarketingPreferences = PubSubMarketingPreferences.builder();
      switch (method) {
				case PromotionsOptService.PROMOTION_METHOD_EMAIL:
				  user = promotionsOptService.optEmail(user, optOut, ipAddress, util);
				  pubSubMarketingPreferences.emailOptOut(optOut);
          pubSubMarketingPreferences.postOptOut(user.getPostOptOut());
          pubSubMarketingPreferences.smsOptOut(user.getSmsOptOut());
          pubSubMarketingPreferences.pushOptOut(user.getPushOptOut());
          pubSubMarketingPreferences.leaderBoardOptOut(user.getLeaderboardOptOut());
          pubSubMarketingPreferences.callOptOut(user.getCallOptOut());
          break;
				case PromotionsOptService.PROMOTION_METHOD_POST:
				  user = promotionsOptService.optPost(user, optOut, ipAddress, util);
          pubSubMarketingPreferences.postOptOut(optOut);
          pubSubMarketingPreferences.emailOptOut(user.getEmailOptOut());
          pubSubMarketingPreferences.smsOptOut(user.getSmsOptOut());
          pubSubMarketingPreferences.pushOptOut(user.getPushOptOut());
          pubSubMarketingPreferences.leaderBoardOptOut(user.getLeaderboardOptOut());
          pubSubMarketingPreferences.callOptOut(user.getCallOptOut());
          break;
				case PromotionsOptService.PROMOTION_METHOD_SMS:
				  user = promotionsOptService.optSMS(user, optOut, ipAddress, util);
          pubSubMarketingPreferences.smsOptOut(optOut);
          pubSubMarketingPreferences.postOptOut(user.getPostOptOut());
          pubSubMarketingPreferences.emailOptOut(user.getEmailOptOut());
          pubSubMarketingPreferences.pushOptOut(user.getPushOptOut());
          pubSubMarketingPreferences.leaderBoardOptOut(user.getLeaderboardOptOut());
          pubSubMarketingPreferences.callOptOut(user.getCallOptOut());
          break;
				case PromotionsOptService.PROMOTION_METHOD_CALL:
				  user = promotionsOptService.optCall(user, optOut, ipAddress, util);
          pubSubMarketingPreferences.callOptOut(optOut);
          pubSubMarketingPreferences.postOptOut(user.getPostOptOut());
          pubSubMarketingPreferences.emailOptOut(user.getEmailOptOut());
          pubSubMarketingPreferences.pushOptOut(user.getPushOptOut());
          pubSubMarketingPreferences.leaderBoardOptOut(user.getLeaderboardOptOut());
          pubSubMarketingPreferences.smsOptOut(user.getSmsOptOut());
          break;
				case PromotionsOptService.PROMOTION_METHOD_PUSH:
				  user = promotionsOptService.optPush(user, optOut, ipAddress, util);
          pubSubMarketingPreferences.pushOptOut(optOut);
          pubSubMarketingPreferences.callOptOut(user.getCallOptOut());
          pubSubMarketingPreferences.emailOptOut(user.getEmailOptOut());
          pubSubMarketingPreferences.postOptOut(user.getPostOptOut());
          pubSubMarketingPreferences.leaderBoardOptOut(user.getLeaderboardOptOut());
          pubSubMarketingPreferences.smsOptOut(user.getSmsOptOut());
          break;
				case PromotionsOptService.PROMOTION_METHOD_LEADERBOARD:
				  user = promotionsOptService.optLeaderboard(user, optOut, ipAddress, util);
          pubSubMarketingPreferences.leaderBoardOptOut(optOut);
          pubSubMarketingPreferences.callOptOut(user.getCallOptOut());
          pubSubMarketingPreferences.emailOptOut(user.getEmailOptOut());
          pubSubMarketingPreferences.pushOptOut(user.getPushOptOut());
          pubSubMarketingPreferences.postOptOut(user.getPostOptOut());
          pubSubMarketingPreferences.smsOptOut(user.getSmsOptOut());
          break;
			}

      try {
        pubSubUserService.buildAndSendPubSubAccountChangeOpt(user, PubSubEventOrigin.USER, pubSubMarketingPreferences, util, PubSubEventType.MARKETING_PREFERENCES);
      } catch (Exception ex) {
			  log.error("PromotionsOptOut has failed for : " + userGuid + " :: " + ex.getMessage(), ex);
      }
			return Response.<Boolean>builder().data(optOut).status(Status.OK).build();
		} catch (Status401UnAuthorisedException ex) {
      return Response.<Boolean>builder().data(null).status(Status.UNAUTHORIZED).message(ex.getMessage()).build();
		} catch (Exception e) {
      log.error(e.getMessage(), e);
      return Response.<Boolean>builder().data(null).status(Status.INTERNAL_SERVER_ERROR).build();
    }
	}
}
