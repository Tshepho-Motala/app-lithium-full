package lithium.service.domain.data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
		@Index(name="idx_role_role", columnList="role", unique=true)
})
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String role;
	
	public Role(String role) {
		super();
		this.role = role;
	}
	
	@Transient
	public String getRoleNameKey() {
		return "Role." + role + ".name";
	}

	@Transient
	public String getDescrKey() {
		return "Role." + role + ".descr";
	}
}