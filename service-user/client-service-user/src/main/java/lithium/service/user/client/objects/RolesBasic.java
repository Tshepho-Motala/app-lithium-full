package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolesBasic {
	private Long id;
	private String role;
	private Boolean self;
	private Boolean child;
}
