package lithium.service.domain.client.objects;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class Role {
	private Long id;
	private String role;
	
	public Role(String role) {
		this.role = role;
	}
}