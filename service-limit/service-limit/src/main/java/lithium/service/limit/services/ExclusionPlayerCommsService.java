package lithium.service.limit.services;

import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.limit.data.entities.PlayerExclusionV2;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.utils.UserToPlaceholderBinder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;



@Service
public class ExclusionPlayerCommsService {
	@Autowired private PlayerCommsService service;
	@Autowired private LimitPlaceholderService limitPlaceholderService;

	public static String PERMANENT_EXCLUSION_TEMPLATE = "player.exclusion.added.permanent";
	public static String SOFT_EXCLUSION_TEMPLATE = "player.exclusion.added.soft";
	public static String REMOVED_EXCLUSION_TEMPLATE = "player.exclusion.removed";

	/**
	 * player.exclusion.added.permanent
	 * player.exclusion.added.soft
	 * player.exclusion.removed
	 */
	public void communicateWithPlayerV2(User user, PlayerExclusionV2 playerExclusionV2) {
		String templateName;
		if (playerExclusionV2 != null) {
			if (playerExclusionV2.isPermanent()) {
				templateName = PERMANENT_EXCLUSION_TEMPLATE;
			} else {
				templateName = SOFT_EXCLUSION_TEMPLATE;
			}
		} else {
			templateName = REMOVED_EXCLUSION_TEMPLATE;
		}
		notifyPlayer(templateName, user, playerExclusionV2);
	}

	public void notifyPlayer(String templateName, User user, PlayerExclusionV2 exclusion){
		Set<Placeholder> placeholders = resolvePlaceholders(user, exclusion);
		service.communicateWithPlayer(templateName, user, placeholders);
	}

	private Set<Placeholder> resolvePlaceholders(User user, PlayerExclusionV2 exclusion) {
		Set<Placeholder> placeholders = new UserToPlaceholderBinder(user).completePlaceholders();
		placeholders.addAll(limitPlaceholderService.resolveExclusionPlaceholders(exclusion));
		return placeholders;
	}
}
