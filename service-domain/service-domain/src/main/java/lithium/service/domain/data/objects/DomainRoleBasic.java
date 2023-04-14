package lithium.service.domain.data.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainRoleBasic {
	private String role;
	private Boolean enabled;
}