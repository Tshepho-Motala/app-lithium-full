package lithium.service.machine.client.objects;

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
public class Status {
	private Long id;
	private String name;
	private String description;
	private Boolean enabled;
	private Boolean deleted;
}