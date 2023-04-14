package lithium.service.user.client.objects;

import java.beans.Transient;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of={"role", "name", "category"})
public class Role implements GrantedAuthority {
	private static final long serialVersionUID = -4850865325613345605L;
	private Long id;
	private String name;
	private String role;
	private String description;
	private Category category;
	
	public Role(String name, String role, String description) {
		super();
		this.name = name;
		this.role = role;
		this.description = description;
	}

	@Transient
	public String getRoleNameKey() {
		return "Role." + role + ".name";
	}

	@Transient
	public String getDescrKey() {
		return "Role." + role + ".descr";
	}
	
	@JsonIgnore
	public String getAuthority() {
		return getRole();
	}
}