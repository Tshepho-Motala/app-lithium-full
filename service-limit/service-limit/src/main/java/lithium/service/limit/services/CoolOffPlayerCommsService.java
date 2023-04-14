package lithium.service.limit.services;

import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.limit.data.entities.PlayerCoolOff;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.utils.UserToPlaceholderBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CoolOffPlayerCommsService {
	@Autowired private PlayerCommsService service;
	@Autowired private LimitPlaceholderService limitPlaceholderService;

	/**
	 * player.cooloff.added
	 * player.cooloff.cleared
	 */
	public void communicateWithPlayer(User user, PlayerCoolOff playerCoolOff) {
		String templateName = "player.cooloff.";
		if (playerCoolOff != null) {
			templateName += "added";
		} else {
			templateName += "cleared";
		}
		Set<Placeholder> placeholders = resolvePlaceholders(user, playerCoolOff);
		service.communicateWithPlayer(templateName, user, placeholders);
	}

	private Set<Placeholder> resolvePlaceholders(User user, PlayerCoolOff playerCoolOff) {
		Set<Placeholder> placeholders = new UserToPlaceholderBinder(user).completePlaceholders();
		placeholders.addAll(limitPlaceholderService.resolveCoolOffPlaceholders(playerCoolOff));
		return placeholders;
	}
}
