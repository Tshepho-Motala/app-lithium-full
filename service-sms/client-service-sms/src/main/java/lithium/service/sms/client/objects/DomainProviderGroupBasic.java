package lithium.service.sms.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomainProviderGroupBasic {
	private String name;
	private Boolean enabled;
	private Boolean deleted;
	private Integer priority;
	private Long providerId;
	private String domainName;
	private String accessRule;
}