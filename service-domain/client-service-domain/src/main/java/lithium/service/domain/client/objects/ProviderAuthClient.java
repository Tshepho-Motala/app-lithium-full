package lithium.service.domain.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude= {"domain", "password"})
@EqualsAndHashCode(exclude="domain")
public class ProviderAuthClient implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Domain domain;
	private Date creationDate;

	private String code; // (unique per domain, machine code)
	private String description;
	private String password;
	private String guid; // (generated from domain and code)
}
