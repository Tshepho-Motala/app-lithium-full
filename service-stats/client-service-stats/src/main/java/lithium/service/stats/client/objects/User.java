package lithium.service.stats.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
	private static final long serialVersionUID = -22991428034566200L;
	private long id;
	private String guid;
	
	/// Utility methods
	public String domainName() {
		return guid.split("/")[0];
	}
	public String username() {
		return guid.split("/")[1];
	}
	public String guid() {
		return guid;
	}
}