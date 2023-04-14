package lithium.service.casino.service;

import java.util.Set;

import lithium.service.casino.data.entities.Bonus;
import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.util.DomainToPlaceholderBinder;
import lithium.service.sms.client.objects.SMSBasic;
import lithium.service.sms.client.stream.SMSStream;
import lithium.service.user.client.utils.UserToPlaceholderBinder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.casino.data.entities.PlayerBonusPending;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.User;

import static java.util.Optional.ofNullable;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASINO_BONUS_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASINO_BONUS_CODE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASINO_BONUS_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASINO_BONUS_PERCENTAGE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASINO_PLAY_THROUGH_CENTS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASINO_PLAY_THROUGH_REQUIRED_CENTS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.CASINO_TRIGGER_AMOUNT;

@Service
@AllArgsConstructor
public class CasinoMailSmsService {
	private final LithiumServiceClientFactory services;
	private final MailStream mailStream;
	private final SMSStream smsStream;
	
	public static final String ISO_LANG_CODE_ENG = "en";
	
	public static final String BONUS_STATE_ACTIVATE = "activate";
	public static final String BONUS_STATE_PENDING = "pending";
	public static final String BONUS_STATE_CANCEL = "cancel";
	public static final String BONUS_STATE_CANCEL_PENDING = "cancel_pending";
	
	private Set<Placeholder> constructPlaceholders(PlayerBonusHistory playerBonusHistory, PlayerBonusPending playerBonusPending, User user, Domain domain) {
		Set<Placeholder> placeholders = new UserToPlaceholderBinder(user).completePlaceholders();
		if (playerBonusHistory != null) {
			placeholders.add(CASINO_PLAY_THROUGH_CENTS.from(playerBonusHistory.getPlayThroughCents()));
			placeholders.add(CASINO_PLAY_THROUGH_REQUIRED_CENTS.from(playerBonusHistory.getPlayThroughRequiredCents()));
			placeholders.add(CASINO_TRIGGER_AMOUNT.from(playerBonusHistory.getTriggerAmount()));
			placeholders.add(CASINO_BONUS_AMOUNT.from(playerBonusHistory.getBonusAmount()));
			placeholders.add(CASINO_BONUS_PERCENTAGE.from(playerBonusHistory.getBonusPercentage()));
			if (playerBonusHistory.getBonus() != null) {
				placeholders.add(CASINO_BONUS_CODE.from(playerBonusHistory.getBonus().getBonusCode()));
				placeholders.add(CASINO_BONUS_NAME.from(playerBonusHistory.getBonus().getBonusName()));
			}
		} else if (playerBonusPending != null) {
			placeholders.add(CASINO_PLAY_THROUGH_REQUIRED_CENTS.from(playerBonusPending.getPlayThroughRequiredCents()));
			placeholders.add(CASINO_TRIGGER_AMOUNT.from(playerBonusPending.getTriggerAmount()));
			placeholders.add(CASINO_BONUS_AMOUNT.from(playerBonusPending.getBonusAmount()));
			placeholders.add(CASINO_BONUS_PERCENTAGE.from(playerBonusPending.getBonusPercentage()));
			ofNullable(playerBonusPending.getBonusRevision()).map(BonusRevision::getBonus).map(Bonus::getCurrent)
					.ifPresent(bonusRevision -> {
						placeholders.add(CASINO_BONUS_CODE.from(bonusRevision.getBonusCode()));
						placeholders.add(CASINO_BONUS_NAME.from(bonusRevision.getBonusName()));
			});
		}
		if (domain != null) {
			placeholders.addAll(new DomainToPlaceholderBinder(domain).completePlaceholders());
		}
		return placeholders;
	}
	
	private User getExternalUser(String userGuid) throws LithiumServiceClientFactoryException {
		UserApiInternalClient cl = services.target(UserApiInternalClient.class, "service-user", true);
		return cl.getUser(userGuid).getData();
	}
	
	private Domain getExternalDomain(String domainName) throws LithiumServiceClientFactoryException {
		DomainClient domainClient = services.target(DomainClient.class, "service-domain", true);
		Response<Domain> response = domainClient.findByName(domainName);
		if (response.isSuccessful()) {
			return response.getData();
		}
		return null;
	}

	// This should actually be part of an enum with number and string, but iswis
	private String bonusTypeLookup(int bonusTypeCode) {
		switch (bonusTypeCode) {
			case 0:
				return "signup";
			case 1:
				return "deposit";
			case 2:
				return "trigger";
		}
		return "unknown_type";
	}
	public void sendBonusMail(String state, PlayerBonusHistory playerBonusHistory, PlayerBonusPending playerBonusPending) throws Exception {
		if (playerBonusHistory != null && playerBonusPending != null) throw new Exception("Cannot have both playerBonus and playerBonusPending");
		User user = null;
		Domain domain = null;
		String bonusType = "";
		String bonusCode = "";
		String domainName = "";
		if (playerBonusHistory != null) {
			user = getExternalUser(playerBonusHistory.getPlayerBonus().getPlayerGuid());
			domain = getExternalDomain(playerBonusHistory.getBonus().getDomain().getName());
			bonusType = bonusTypeLookup(playerBonusHistory.getBonus().getBonusType());
			bonusCode = playerBonusHistory.getBonus().getBonusCode();
			domainName = playerBonusHistory.getBonus().getDomain().getName();
		}
		if (playerBonusPending != null) {
			user = getExternalUser(playerBonusPending.getPlayerGuid());
			domain = getExternalDomain(playerBonusPending.getBonusRevision().getDomain().getName());
			bonusType = bonusTypeLookup(playerBonusPending.getBonusRevision().getBonusType());
			bonusCode = playerBonusPending.getBonusRevision().getBonusCode();
			domainName = playerBonusPending.getBonusRevision().getDomain().getName();
		}
		bonusCode = (bonusCode.length() > 0)? bonusCode: "nobonuscode";
		if (user.getEmail() != null) {
			mailStream.process(
					EmailData.builder()
							.authorSystem()
							.domainName(domainName)
							.emailTemplateName(("player." + bonusType + ".bonus." + bonusCode + "." + state).toLowerCase())
							.emailTemplateLang(ISO_LANG_CODE_ENG)
							.to(user.getEmail())
							.priority(1)
							.userGuid(user.guid())
							.placeholders(constructPlaceholders(playerBonusHistory, playerBonusPending, user, domain))
							.build()
			);
		}
	}

	public void sendBonusSms(String state, PlayerBonusHistory playerBonusHistory, PlayerBonusPending playerBonusPending) throws Exception {
		if (playerBonusHistory != null && playerBonusPending != null) throw new Exception("Cannot have both playerBonus and playerBonusPending");
		User user = null;
		Domain domain = null;
		String bonusType = "";
		String bonusCode = "";
		String domainName = "";
		if (playerBonusHistory != null) {
			user = getExternalUser(playerBonusHistory.getPlayerBonus().getPlayerGuid());
			domain = getExternalDomain(playerBonusHistory.getBonus().getDomain().getName());
			bonusType = bonusTypeLookup(playerBonusHistory.getBonus().getBonusType());
			bonusCode = playerBonusHistory.getBonus().getBonusCode();
			domainName = playerBonusHistory.getBonus().getDomain().getName();
		}
		if (playerBonusPending != null) {
			user = getExternalUser(playerBonusPending.getPlayerGuid());
			domain = getExternalDomain(playerBonusPending.getBonusRevision().getDomain().getName());
			bonusType = bonusTypeLookup(playerBonusPending.getBonusRevision().getBonusType());
			bonusCode = playerBonusPending.getBonusRevision().getBonusCode();
			domainName = playerBonusPending.getBonusRevision().getDomain().getName();
		}
		bonusCode = (bonusCode.length() > 0)? bonusCode: "nobonuscode";
		String to = user.getCellphoneNumber();
		if (to != null)
			smsStream.process(
					SMSBasic.builder()
							.domainName(domainName)
							.smsTemplateName(("sms.player."+bonusType+".bonus."+bonusCode+"."+state).toLowerCase())
							.smsTemplateLang(ISO_LANG_CODE_ENG)
							.to(to)
							.priority(1)
							.userGuid(user.guid())
							.placeholders(constructPlaceholders(playerBonusHistory, playerBonusPending, user, domain))
							.build()
			);
	}
}
