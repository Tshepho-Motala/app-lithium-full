package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBasic {
	private Long id;
	private Domain domain;
	private String name;
	private String description;
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Domain {
		private Long id;
		private String name;
	}
}
