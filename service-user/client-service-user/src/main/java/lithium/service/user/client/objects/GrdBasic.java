package lithium.service.user.client.objects;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrdBasic {
	private Long id;
	private Domain domain;
//	private List<Role> roles;
	private List<String> roles;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public
	static class Domain {
		private Long id;
		private String name;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Role {
		private Long id;
		private String name;
		private String role;
	}
}
