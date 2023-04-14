package lithium.service.user.controllers;


import javax.servlet.http.HttpServletRequest;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.leaderboard.client.LeaderboardClient;
import lithium.service.pushmsg.client.PushMsgClient;
import lithium.service.user.client.objects.PubSubEventOrigin;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.PubSubMarketingPreferences;
import lithium.service.user.data.entities.OptRequest;
import lithium.service.user.data.entities.User;
import lithium.exceptions.Status404UserNotFoundException;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import lithium.service.user.services.PromotionsOptService;
import lithium.service.user.services.PubSubUserService;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/backoffice/{domainName}/profile/opt/")
public class PromotionsOptSubscriptionController {
    @Autowired
    PromotionsOptService promotionsOptService;
    @Autowired
    ChangeLogService changeLogService;
    @Autowired
    UserService userService;
    @Autowired
    LithiumServiceClientFactory services;
    @Autowired
    MessageSource messageSource;
    @Autowired
    PubSubUserService pubSubUserService;

    @PostMapping
    public ResponseEntity editOptIn(
            @PathVariable("domainName") String domainName,
            @RequestBody OptRequest optRequest,
            HttpServletRequest request,
            LithiumTokenUtil util
    ) throws Status404UserNotFoundException, Status500InternalServerErrorException {

        if (util == null) throw  new Status500InternalServerErrorException("Invalid Token");
        String ipAddress = request.getRemoteAddr();
        if (request.getHeader("X-Forwarded-For") != null) {
          ipAddress = request.getHeader("X-Forwarded-For");
        }
        User user = userService.findFromGuid(optRequest.getGuid());
        if (user == null) throw new Status404UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_NOT_FOUND", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "User not found or invalid user guid.", LocaleContextHolder.getLocale()));

        log.info("Marketing Optout (by " + util.guid() + ") :: optRequest.method:" + optRequest.getMethod() + ", optRequest.guid:" + optRequest.getGuid() +
            ", optRequest.description:" + optRequest.getDescription() + ", optRequest.optout:" + optRequest.isOptOut() + ", ip:" + ipAddress + "");

        ChangeLogFieldChange c = null;
      PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder builder = PubSubMarketingPreferences.builder();
      try {
        switch (optRequest.getMethod()) {
          case PromotionsOptService.PROMOTION_METHOD_EMAIL:
            user = promotionsOptService.optEmail(user, optRequest.isOptOut(), ipAddress, util);
            builder.emailOptOut(optRequest.isOptOut());
            builder.postOptOut(user.getPostOptOut());
            builder.smsOptOut(user.getSmsOptOut());
            builder.callOptOut(user.getCallOptOut());
            builder.pushOptOut(user.getPushOptOut());
            builder.leaderBoardOptOut(user.getLeaderboardOptOut());
            break;
          case PromotionsOptService.PROMOTION_METHOD_POST:
            user = promotionsOptService.optPost(user, optRequest.isOptOut(), ipAddress, util);
            builder.emailOptOut(user.getEmailOptOut());
            builder.postOptOut(optRequest.isOptOut());
            builder.smsOptOut(user.getSmsOptOut());
            builder.callOptOut(user.getCallOptOut());
            builder.pushOptOut(user.getPushOptOut());
            builder.leaderBoardOptOut(user.getLeaderboardOptOut());
            break;
          case PromotionsOptService.PROMOTION_METHOD_SMS:
            user = promotionsOptService.optSMS(user, optRequest.isOptOut(), ipAddress, util);
            builder.emailOptOut(user.getEmailOptOut());
            builder.postOptOut(user.getPostOptOut());
            builder.smsOptOut(optRequest.isOptOut());
            builder.callOptOut(user.getCallOptOut());
            builder.pushOptOut(user.getPushOptOut());
            builder.leaderBoardOptOut(user.getLeaderboardOptOut());
            break;
          case PromotionsOptService.PROMOTION_METHOD_CALL:
            user = promotionsOptService.optCall(user, optRequest.isOptOut(), ipAddress, util);
            builder.emailOptOut(user.getEmailOptOut());
            builder.postOptOut(user.getPostOptOut());
            builder.smsOptOut(user.getSmsOptOut());
            builder.callOptOut(optRequest.isOptOut());
            builder.pushOptOut(user.getPushOptOut());
            builder.leaderBoardOptOut(user.getLeaderboardOptOut());
            break;
          case PromotionsOptService.PROMOTION_METHOD_PUSH:
            user = promotionsOptService.optPush(user, optRequest.isOptOut(), ipAddress, util);
            toggleSvcPushMsgOptOut(user);
            builder.emailOptOut(user.getEmailOptOut());
            builder.postOptOut(user.getPostOptOut());
            builder.smsOptOut(user.getSmsOptOut());
            builder.callOptOut(user.getCallOptOut());
            builder.pushOptOut(optRequest.isOptOut());
            builder.leaderBoardOptOut(user.getLeaderboardOptOut());
            break;
          case PromotionsOptService.PROMOTION_METHOD_LEADERBOARD:
            user = promotionsOptService.optLeaderboard(user, optRequest.isOptOut(), ipAddress, util);
            toggleSvcLeaderboardOptOut(user);
            builder.emailOptOut(user.getEmailOptOut());
            builder.postOptOut(user.getPostOptOut());
            builder.smsOptOut(user.getSmsOptOut());
            builder.callOptOut(user.getCallOptOut());
            builder.pushOptOut(user.getPushOptOut());
            builder.leaderBoardOptOut(optRequest.isOptOut());
            break;
        }
        user = userService.save(user);
      } catch (Exception ex) {
        log.error("editOptIn failed for : " + user.getGuid() + " :: " + ex.getMessage(), ex);
      }
      try {
        pubSubUserService.buildAndSendPubSubAccountChangeOpt(user, PubSubEventOrigin.BACK_OFFICE, builder, util, PubSubEventType.MARKETING_PREFERENCES);
      } catch (Exception ex) {
        log.error("editOptIn failed for : " + user.getGuid() + " :: " + ex.getMessage(), ex);
      }
        return ResponseEntity.ok().build();

    }

    private void toggleSvcPushMsgOptOut(User user) {
        try {
            PushMsgClient pushMsgClient = services.target(PushMsgClient.class, true);
            pushMsgClient.toggleOptOut(user.domainName(), user.guid());
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Could not toggle optout on svc-pushmsg", e);
        }
    }

    private void toggleSvcLeaderboardOptOut(User user) {
        try {
            LeaderboardClient leaderboardClient = services.target(LeaderboardClient.class, true);
            leaderboardClient.optout(user.guid());
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Could not toggle optout on svc-pushmsg", e);
        }
    }
}
