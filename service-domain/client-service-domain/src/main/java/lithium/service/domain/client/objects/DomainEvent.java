package lithium.service.domain.client.objects;

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
public class DomainEvent implements Serializable {
	private static final long serialVersionUID = -1L;
	
	private Long id;
	private String domainName;
	private String type;
	private String message;
	private String data;
}