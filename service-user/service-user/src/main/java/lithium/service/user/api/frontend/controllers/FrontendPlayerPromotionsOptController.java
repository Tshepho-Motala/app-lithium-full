package lithium.service.user.api.frontend.controllers;

import javax.servlet.http.HttpServletRequest;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.limit.client.exceptions.Status403PlayerRestrictionDeniedException;
import lithium.service.limit.client.exceptions.Status409PlayerRestrictionConflictException;
import lithium.service.user.client.enums.UserLinkTypes;
import lithium.service.user.client.objects.PubSubAccountChange;
import lithium.service.user.client.objects.PubSubEventOrigin;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.PubSubMarketingPreferences;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.PromotionsOptService;
import lithium.service.user.services.PubSubUserService;
import lithium.service.user.services.UserLinkService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/profile/opt") //FIXME: should be /frontend/profile/opt since this is only used by frontend
public class FrontendPlayerPromotionsOptController {
	@Autowired PromotionsOptService promotionsOptService;
	@Autowired TokenStore tokenStore;
  @Autowired PubSubUserService pubSubUserService;
  @Autowired UserLinkService userLinkService;

	@PostMapping("/{method}/{optOut}")
	public Response<Boolean> opt(
		@PathVariable("method") String method,
		@PathVariable("optOut") Boolean optOut,
		HttpServletRequest request, LithiumTokenUtil tokenUtil
	) throws Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException {

    StringBuilder logStr = null;

    try {
      logStr = new StringBuilder("Market Optout (by %s) :")
          .append(tokenUtil.guid());

      User user = promotionsOptService.getUserById(tokenUtil.id());
      if (user == null) throw new Exception(logStr + "  :  user not found.");
      logStr.append("method: ").append(method)
          .append(", optout: ").append(optOut)
          .append(", userGuid: ").append(user.getGuid());

      String ipAddress = request.getRemoteAddr();
      if (request.getHeader("X-Forwarded-For") != null) {
        ipAddress = request.getHeader("X-Forwarded-For");
       logStr.append(", ip: ").append(ipAddress);
      }

      log.info(logStr.toString());

      return opt(user, method, optOut, ipAddress, tokenUtil);
    }
    catch(Status403PlayerRestrictionDeniedException | Status409PlayerRestrictionConflictException e) {
      logStr.append(" :: ").append(e.getMessage());
      log.error(logStr.toString(), e);
      throw e;
    }
    catch(Exception e) {
     logStr.append(" :: ").append(e.getMessage());
      log.error(logStr.toString(), e);
      return Response.<Boolean>builder().status(Status.INTERNAL_SERVER_ERROR).build();
    }
	}

  private Response<Boolean> opt(User user, String method, boolean optOut, String ipAddress, LithiumTokenUtil tokenUtil) throws Exception {
    PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder builder = PubSubMarketingPreferences.builder();
    builder.accountId(user.getId());
    User parentUser = promotionsOptService.mutuallyExclusiveParent(user);
    boolean parentUpdate  = method.toUpperCase().contains("PARENT");

    PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder parentBuilder = PubSubMarketingPreferences.builder();
    if (!ObjectUtils.isEmpty(parentUser)) {
      parentBuilder.accountId(parentUser.getId());
    }
    switch (method) {
      case PromotionsOptService.PROMOTION_METHOD_EMAIL:
        builder.emailOptOut(optOut);
        builder.postOptOut(user.getPostOptOut());
        builder.smsOptOut(user.getSmsOptOut());
        builder.callOptOut(user.getCallOptOut());
        builder.pushOptOut(user.getPushOptOut());
        builder.leaderBoardOptOut(user.getLeaderboardOptOut());
        builder.promotionsOptOut(user.getPromotionsOptOut());
        user = promotionsOptService.optEmail(user, optOut, ipAddress, tokenUtil); break;
      case PromotionsOptService.PROMOTION_METHOD_POST:
        builder.emailOptOut(user.getEmailOptOut());
        builder.postOptOut(optOut);
        builder.smsOptOut(user.getSmsOptOut());
        builder.callOptOut(user.getCallOptOut());
        builder.pushOptOut(user.getPushOptOut());
        builder.leaderBoardOptOut(user.getLeaderboardOptOut());
        builder.promotionsOptOut(user.getPromotionsOptOut());
        user = promotionsOptService.optPost(user, optOut, ipAddress, tokenUtil); break;
      case PromotionsOptService.PROMOTION_METHOD_SMS:
        builder.emailOptOut(user.getEmailOptOut());
        builder.postOptOut(user.getPostOptOut());
        builder.smsOptOut(optOut);
        builder.callOptOut(user.getCallOptOut());
        builder.pushOptOut(user.getPushOptOut());
        builder.leaderBoardOptOut(user.getLeaderboardOptOut());
        builder.promotionsOptOut(user.getPromotionsOptOut());
        user = promotionsOptService.optSMS(user, optOut, ipAddress, tokenUtil); break;
      case PromotionsOptService.PROMOTION_METHOD_CALL:
        builder.emailOptOut(user.getEmailOptOut());
        builder.postOptOut(user.getPostOptOut());
        builder.smsOptOut(user.getSmsOptOut());
        builder.callOptOut(optOut);
        builder.pushOptOut(user.getPushOptOut());
        builder.leaderBoardOptOut(user.getLeaderboardOptOut());
        builder.promotionsOptOut(user.getPromotionsOptOut());
        user = promotionsOptService.optCall(user, optOut, ipAddress, tokenUtil); break;
      case PromotionsOptService.PROMOTION_METHOD_PUSH:
        builder.emailOptOut(user.getEmailOptOut());
        builder.postOptOut(user.getPostOptOut());
        builder.smsOptOut(user.getSmsOptOut());
        builder.callOptOut(user.getCallOptOut());
        builder.pushOptOut(optOut);
        builder.leaderBoardOptOut(user.getLeaderboardOptOut());
        builder.promotionsOptOut(user.getPromotionsOptOut());
        user = promotionsOptService.optPush(user, optOut, ipAddress, tokenUtil); break;
      case PromotionsOptService.PROMOTION_METHOD_LEADERBOARD:
        builder.emailOptOut(user.getEmailOptOut());
        builder.postOptOut(user.getPostOptOut());
        builder.smsOptOut(user.getSmsOptOut());
        builder.callOptOut(user.getCallOptOut());
        builder.pushOptOut(user.getPushOptOut());
        builder.leaderBoardOptOut(optOut);
        builder.promotionsOptOut(user.getPromotionsOptOut());
        user = promotionsOptService.optLeaderboard(user, optOut, ipAddress, tokenUtil); break;
      case PromotionsOptService.PROMOTION_METHOD_PARENT_EMAIL:
        parentBuilder.emailOptOut(optOut);
        parentBuilder.postOptOut(parentUser.getPostOptOut());
        parentBuilder.smsOptOut(parentUser.getSmsOptOut());
        parentBuilder.callOptOut(parentUser.getCallOptOut());
        parentBuilder.pushOptOut(parentUser.getPushOptOut());
        parentBuilder.leaderBoardOptOut(parentUser.getLeaderboardOptOut());
        if (!ObjectUtils.isEmpty(parentUser) && !ObjectUtils.nullSafeEquals(parentUser.domainName(), user.domainName())) {
          parentUser = promotionsOptService.optEmail(parentUser, optOut, ipAddress, tokenUtil);
        }
        break;
      case PromotionsOptService.PROMOTION_METHOD_PARENT_POST:
        parentBuilder.emailOptOut(parentUser.getEmailOptOut());
        parentBuilder.postOptOut(optOut);
        parentBuilder.smsOptOut(parentUser.getSmsOptOut());
        parentBuilder.callOptOut(parentUser.getCallOptOut());
        parentBuilder.pushOptOut(parentUser.getPushOptOut());
        parentBuilder.leaderBoardOptOut(parentUser.getLeaderboardOptOut());
        if (!ObjectUtils.isEmpty(parentUser) && !ObjectUtils.nullSafeEquals(parentUser.domainName(), user.domainName())) {
          parentUser = promotionsOptService.optPost(parentUser, optOut, ipAddress, tokenUtil);
        }

        break;
      case PromotionsOptService.PROMOTION_METHOD_PARENT_SMS:
        parentBuilder.emailOptOut(parentUser.getEmailOptOut());
        parentBuilder.postOptOut(parentUser.getPostOptOut());
        parentBuilder.smsOptOut(optOut);
        parentBuilder.callOptOut(parentUser.getCallOptOut());
        parentBuilder.pushOptOut(parentUser.getPushOptOut());
        parentBuilder.leaderBoardOptOut(parentUser.getLeaderboardOptOut());
        if (!ObjectUtils.isEmpty(parentUser) && !ObjectUtils.nullSafeEquals(parentUser.domainName(), user.domainName())) {
          parentUser = promotionsOptService.optSMS(parentUser, optOut, ipAddress, tokenUtil);
        }
        break;
      case PromotionsOptService.PROMOTION_METHOD_PARENT_CALL:
        parentBuilder.emailOptOut(parentUser.getEmailOptOut());
        parentBuilder.postOptOut(parentUser.getPostOptOut());
        parentBuilder.smsOptOut(parentUser.getSmsOptOut());
        parentBuilder.callOptOut(optOut);
        parentBuilder.pushOptOut(parentUser.getPushOptOut());
        parentBuilder.leaderBoardOptOut(parentUser.getLeaderboardOptOut());
        if (!ObjectUtils.isEmpty(parentUser) && !ObjectUtils.nullSafeEquals(parentUser.domainName(), user.domainName())) {
          parentUser = promotionsOptService.optCall(parentUser, optOut, ipAddress, tokenUtil);
        }
        break;
      case PromotionsOptService.PROMOTION_METHOD_PARENT_PUSH:
        parentBuilder.emailOptOut(parentUser.getEmailOptOut());
        parentBuilder.postOptOut(parentUser.getPostOptOut());
        parentBuilder.smsOptOut(parentUser.getSmsOptOut());
        parentBuilder.callOptOut(parentUser.getCallOptOut());
        parentBuilder.pushOptOut(optOut);
        parentBuilder.leaderBoardOptOut(parentUser.getLeaderboardOptOut());
        if (!ObjectUtils.isEmpty(parentUser) && !ObjectUtils.nullSafeEquals(parentUser.domainName(), user.domainName())) {
          parentUser = promotionsOptService.optPush(parentUser, optOut, ipAddress, tokenUtil);
        }
        break;
      case PromotionsOptService.PROMOTION_METHOD_PARENT_LEADERBOARD:
        parentBuilder.emailOptOut(parentUser.getEmailOptOut());
        parentBuilder.postOptOut(parentUser.getPostOptOut());
        parentBuilder.smsOptOut(parentUser.getSmsOptOut());
        parentBuilder.callOptOut(parentUser.getCallOptOut());
        parentBuilder.pushOptOut(parentUser.getPushOptOut());
        parentBuilder.leaderBoardOptOut(optOut);
        if (!ObjectUtils.isEmpty(parentUser) && !ObjectUtils.nullSafeEquals(parentUser.domainName(), user.domainName())) {
          parentUser = promotionsOptService.optLeaderboard(parentUser, optOut, ipAddress, tokenUtil);
        }
        break;
      case PromotionsOptService.PROMOTION_METHOD_PROMOTIONS:
        promotionsOptService.optPromotions(user, optOut);
        return Response.<Boolean>builder().data(optOut).status(Status.OK).build();
    }

    try {

      if (parentUpdate && !ObjectUtils.isEmpty(parentUser) && !ObjectUtils.nullSafeEquals(parentUser.domainName(), user.domainName())) {
        pubSubUserService.buildAndSendPubSubAccountChangeOpt(parentUser, PubSubEventOrigin.USER, parentBuilder, tokenUtil,
            PubSubEventType.MARKETING_PREFERENCES);
      } else {
        pubSubUserService.buildAndSendPubSubAccountChangeOpt(user, PubSubEventOrigin.USER, builder, tokenUtil, PubSubEventType.MARKETING_PREFERENCES);
      }
    } catch (Exception ex) {
      log.error("ProfileOptOut failed for : " + user.getGuid() + " :: " + ex.getMessage(), ex);
    }
    return Response.<Boolean>builder().data(optOut).status(Status.OK).build();
  }
}
