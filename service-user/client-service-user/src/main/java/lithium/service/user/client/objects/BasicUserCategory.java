package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BasicUserCategory {
	private Long id;
	private String name;
	private String description;
	private String domainName;
	private Boolean dwhVisible;
}