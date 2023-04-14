package lithium.service.document.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Label implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	
	private String value;
	
	private String domainName;
	
	private boolean enabled = true;
	
	private boolean deleted = false;
}
