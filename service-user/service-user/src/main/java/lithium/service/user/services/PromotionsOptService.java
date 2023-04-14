package lithium.service.user.services;

import java.util.ArrayList;
import java.util.List;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status469InvalidInputException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.PubSubAccountChange;
import lithium.service.user.client.objects.PubSubEventOrigin;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.PubSubMarketingPreferences;
import lithium.service.user.data.entities.User;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromotionsOptService {
	public static final String PROMOTION_METHOD_EMAIL = "email";
	public static final String PROMOTION_METHOD_POST = "post";
	public static final String PROMOTION_METHOD_SMS = "sms";
	public static final String PROMOTION_METHOD_CALL = "call";
	public static final String PROMOTION_METHOD_PUSH = "push";
	public static final String PROMOTION_METHOD_LEADERBOARD = "leaderboard";
  public static final String PROMOTION_METHOD_PROMOTIONS = "promotions";
	
	public static final String PROMOTION_METHOD_PARENT_EMAIL = "parentEmail";
	public static final String PROMOTION_METHOD_PARENT_POST = "parentPost";
	public static final String PROMOTION_METHOD_PARENT_SMS = "parentSms";
	public static final String PROMOTION_METHOD_PARENT_CALL = "parentCall";
	public static final String PROMOTION_METHOD_PARENT_PUSH = "parentPush";
	public static final String PROMOTION_METHOD_PARENT_LEADERBOARD = "parentLeaderboard";

	@Autowired UserService userService;
	@Autowired ChangeLogService changeLogService;
	@Autowired LimitInternalSystemService limitInternalSystemService;
  @Autowired PubSubUserService pubSubUserService;

  public User getUserById(Long id) {
		return userService.findOne(id);
	}
	
	public String getValue(String rawData) {
		return rawData.substring(rawData.indexOf("=") + 1);
	}

	private User opt(User user, boolean optOut, String ipAddress, String field, LithiumTokenUtil util) throws Exception {
		String fromValue = "";
		switch (field) {
			case "emailOptOut": fromValue = (String.valueOf(user.getEmailOptOut())); break;
			case "postOptOut": fromValue = (String.valueOf(user.getPostOptOut())); break;
			case "smsOptOut": fromValue = (String.valueOf(user.getSmsOptOut())); break;
			case "callOptOut": fromValue = (String.valueOf(user.getCallOptOut())); break;
			case "pushOptOut": fromValue = (String.valueOf(user.getPushOptOut())); break;
			case "leaderboardOptOut": fromValue = (String.valueOf(user.getLeaderboardOptOut())); break;
		}
		String toValue = (String.valueOf(optOut));

		if (!fromValue.equalsIgnoreCase(toValue)) {
			List<ChangeLogFieldChange> clfc = new ArrayList<>();
			clfc.add(
				ChangeLogFieldChange.builder()
				.field(field)
				.fromValue(fromValue)
				.toValue(toValue)
				.build()
			);

      String type = field.substring(0,field.indexOf("OptOut"));
      String comment = String.format("You have successfully opted-%s to %s", optOut ? "out": "in", type);

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), user.guid(), util, comment,
          null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName());

			switch (field) {
				case "emailOptOut": user.setEmailOptOut(optOut); break;
				case "postOptOut": user.setPostOptOut(optOut); break;
				case "smsOptOut": user.setSmsOptOut(optOut); break;
				case "callOptOut": user.setCallOptOut(optOut); break;
				case "pushOptOut": user.setPushOptOut(optOut); break;
				case "leaderboardOptOut": user.setLeaderboardOptOut(optOut); break;
			}
			return userService.save(user);
		}
		return user;
	}

	public User optEmail(User user, boolean optOut, String ipAddress, LithiumTokenUtil util) throws Exception {
		return opt(user, optOut, ipAddress, "emailOptOut", util);
	}

	public User optPost(User user, boolean optOut, String ipAddress, LithiumTokenUtil util) throws Exception {
		return opt(user, optOut, ipAddress, "postOptOut", util);
	}
	
	public User optSMS(User user, boolean optOut, String ipAddress, LithiumTokenUtil util) throws Exception {
		return opt(user, optOut, ipAddress, "smsOptOut", util);
	}
	
	public User optCall(User user, boolean optOut, String ipAddress, LithiumTokenUtil util) throws Exception {
		return opt(user, optOut, ipAddress, "callOptOut", util);
	}
	
	public User optPush(User user, boolean optOut, String ipAddress, LithiumTokenUtil util) throws Exception {
		return opt(user, optOut, ipAddress, "pushOptOut", util);
	}
	
	public User optLeaderboard(User user, boolean optOut, String ipAddress, LithiumTokenUtil util) throws Exception {
		return opt(user, optOut, ipAddress, "leaderboardOptOut", util);
	}

	public User mutuallyExclusiveParent(User user) throws UserNotFoundException, Status550ServiceDomainClientException, Status469InvalidInputException {
    return userService.deriveRootEcosystemUser(user.getId());
  }

  public User optPromotions(User user, boolean optOut) throws Exception {

	  limitInternalSystemService.setPromotionsOptout(user.getDomain().getName(), user.getGuid(), optOut, user.getId());

    return user;
  }

  @Transactional
  public void groupOptOut(User user, boolean optOut, LithiumTokenUtil util, boolean ecosystemSync, String message) throws Exception {
    User update = userService.findForUpdate(user.getId());
    update.setEmailOptOut(optOut);
    update.setPostOptOut(optOut);
    update.setSmsOptOut(optOut);
    update.setCallOptOut(optOut);
    update.setPushOptOut(optOut);
    update.setLeaderboardOptOut(optOut);
    update.setPromotionsOptOut(optOut);
    PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder pubSubAccountChangeBuilder =
        PubSubMarketingPreferences.builder()
            .emailOptOut(optOut).postOptOut(optOut)
            .callOptOut(optOut).smsOptOut(optOut)
            .pushOptOut(optOut).leaderBoardOptOut(optOut)
            .promotionsOptOut(optOut);
    userService.save(update, ecosystemSync);

    List<ChangeLogFieldChange> clfc =
        changeLogService.copy(update, new User(),
            new String[]{"emailOptOut", "postOptOut", "smsOptOut", "callOptOut", "pushOptOut", "leaderboardOptOut", "promotionsOptOut"});
    changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", update.getId(), update.guid(), util, message,
        null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, update.domainName());

    pubSubUserService.buildAndSendPubSubAccountChangeOpt(user, PubSubEventOrigin.USER, pubSubAccountChangeBuilder, util, PubSubEventType.ACCOUNT_DELETED);

  }
}
