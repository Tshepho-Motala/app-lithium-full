package lithium.service.role.client.objects;
import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "name")
public class Role {
	private String name;
	private String role;
	private String description;
	private Category category;
	
	@Transient
	@JsonIgnore
	public String getNameCode() {
		return ("GLOBAL.ROLE."+role+".NAME").toUpperCase();
	}

	@Transient
	@JsonIgnore
	public String getDescriptionCode() {
		return ("GLOBAL.ROLE."+role+".DESCR").toUpperCase();
	}
	
	@Data
	@Builder
	@ToString
	@AllArgsConstructor
	@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "name")
	public static class Category {
		private Long id;
		private String name;
		private String description;
		
		@Transient
		public String getNameCode() {
			return ("GLOBAL.CAT."+id+".NAME").toUpperCase();
		}

		@Transient
		public String getDescriptionCode() {
			return ("GLOBAL.CAT."+id+".DESCR").toUpperCase();
		}
	}
}