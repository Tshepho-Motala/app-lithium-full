package lithium.service.user.client.objects;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of={"name", "description", "grds", "domain"})
public class Group {
	private Long id;
	private String name;
	private String description;
	private List<GRD> grds;
	private Domain domain;
	private Boolean enabled;
	private Boolean deleted;
}