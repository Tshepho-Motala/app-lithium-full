package lithium.service.user.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lithium.services.user")
@Data
public class ServiceUserConfigurationProperties {
	private PasswordResetToken passwordResetToken = new PasswordResetToken();
	private EmailValidationToken emailValidationToken = new EmailValidationToken();
	private UserEvent userEvent = new UserEvent();
	private LoginBlockFailure loginBlockFailure = new LoginBlockFailure();
	private SessionInactivityTimeoutJob sessionInactivityTimeoutJob = new SessionInactivityTimeoutJob();
  private Jobs jobs = new Jobs();
  private HashMap<String, String> overrideGeoData;
  private List<PublicApiAuthorization> publicApiAuthorizations = new ArrayList<>();
  private ReferralFlag referralFlag = new ReferralFlag();

	@Data
	public static class PasswordResetToken {
		private String cron;
		private Integer keepAlive;
	}

	@Data
	public static class EmailValidationToken {
		private String cron;
		private Integer keepAlive;
		private Integer version = 1;
	}

	@Data
	public static class UserEvent {
		private Integer keepAlive;
	}

	@Data
	public static class LoginBlockFailure {
		private Integer intervalMs;
		private Integer threshold;
	}

  @Data
  public static class SessionInactivityTimeoutJob {
    private String cron;
    private Integer fetchSize;
  }

  @Data
  public static class Jobs {
    private PendingPlaytimeLimitCleanup pendingPlaytimeLimitCleanup;
    private GeoNamesUpdate geoNamesUpdate;
    private OptOutUpdate optOutUpdate;
  }

  @Data
  public static class PendingPlaytimeLimitCleanup {
    private String cron;
    private int pageSize = 1000;
  }

  @Data
  public static class GeoNamesUpdate {
	  private String fixedRateInMilliseconds;
	  private boolean isEnabled = false;
	  private int pageSize = 100;
	  private String searchCountry;
  }

  @Data
  public static class OptOutUpdate {
    private String cron;
    private int pageSize = 1000;
  }

  @Data
  public static class PublicApiAuthorization {
    private String id;
    private String secretKey;
  }

  @Data
  public static class ReferralFlag {
    private boolean enabled = false;
  }
}
