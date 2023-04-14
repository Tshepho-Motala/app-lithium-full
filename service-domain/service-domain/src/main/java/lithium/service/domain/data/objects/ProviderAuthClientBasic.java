package lithium.service.domain.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProviderAuthClientBasic implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;

	private Date creationDate;

	private String code; // (unique per domain, machine code)
	private String description;
	private String guid; // (generated from domain and code)

}
