package lithium.service.domain.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainRole {
	private Long id;
	private Boolean enabled;
	private Boolean deleted;
	private Domain domain;
	private Role role;
}