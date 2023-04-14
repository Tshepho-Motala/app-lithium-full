package lithium.service.user.client.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of={"name", "description"})
@JsonIgnoreProperties({ "roles" })
public class Category {
	private Long id;
	private String name;
	private String description;
//	@JsonBackReference("role_category")
//	private List<Role> roles;
}