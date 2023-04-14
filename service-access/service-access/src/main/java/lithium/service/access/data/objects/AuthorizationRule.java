package lithium.service.access.data.objects;

import lithium.service.access.client.objects.Action;
import lithium.service.access.data.entities.AccessControlList;
import lithium.service.access.data.entities.AccessRule;
import lithium.service.access.data.entities.ExternalList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationRule {
	private AccessRule ruleset;
	private Long ruleId;
	private String ruleName;
	private String providerUrl;
	private Action actionSuccess;
	private Action actionFailed;
	private boolean enabled;
	private int priority;
	private Integer ipResetTime;
	private Boolean validateOnce;
	private Boolean external;
	private String message;
  private String timeoutMessage;
  private String reviewMessage;
	private AccessControlList rule;
	private ExternalList externalRule;

	private String userGuid;
	private Boolean test;
}
