package lithium.service.user.jobs;

import lithium.leader.LeaderCandidate;
import lithium.service.user.config.ServiceUserConfigurationProperties;
import lithium.service.user.data.entities.EmailValidationToken;
import lithium.service.user.data.entities.UserPasswordToken;
import lithium.service.user.data.repositories.EmailValidationTokenRepository;
import lithium.service.user.data.repositories.UserPasswordTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class TokenInvalidateJob {
	@Autowired LeaderCandidate leaderCandidate;
	@Autowired UserPasswordTokenRepository userPasswordTokenRepository;
	@Autowired EmailValidationTokenRepository emailValidationTokenRepository;
	@Autowired ServiceUserConfigurationProperties properties;
	
	@Scheduled(cron="${lithium.services.user.password-reset-token.cron}")
	public void invalidatePasswordResetTokens() {
		log.trace("Token Invalidate: expiring password reset tokens");
		
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		
		for (UserPasswordToken token: userPasswordTokenRepository.findAll()) {
			int minutesPassed = minutesBetweenDates(token.getCreatedOn(), new Date());
			if (minutesPassed >= properties.getPasswordResetToken().getKeepAlive()) {
				log.debug("Password reset token (" + token.getId() + ") has expired. "
						+ "Minutes passed (" + minutesPassed + ") and keep alive time is (" + properties.getPasswordResetToken().getKeepAlive()
						+ "). Deleting token");
				userPasswordTokenRepository.delete(token);
			}
		}
	}
	
	@Scheduled(cron="${lithium.services.user.email-validation-token.cron}")
	public void invalidateEmailValidationTokens() {
		log.trace("Token Invalidate: expiring email validation tokens");
		
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		
		for (EmailValidationToken token: emailValidationTokenRepository.findAll()) {
			int minutesPassed = minutesBetweenDates(token.getCreatedOn(), new Date());
			if (minutesPassed >= properties.getEmailValidationToken().getKeepAlive()) {
				log.debug("Email validation token (" + token.getId() + ") has expired. "
						+ "Minutes passed (" + minutesPassed + ") and keep alive time is (" + properties.getEmailValidationToken().getKeepAlive()
						+ "). Deleting token");
				emailValidationTokenRepository.delete(token);
			}
		}
	}
	
	private int minutesBetweenDates(Date first, Date second) {
		Minutes minutes = Minutes.minutesBetween(new DateTime(first), new DateTime(second));
		return minutes.getMinutes();
	}
}
