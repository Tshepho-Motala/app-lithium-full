package lithium.service.mail.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DomainProviderProperty implements Serializable {
	private static final long serialVersionUID = -542565555170171791L;
	
	private Long id;
	private int version;
	private String value;
	private ProviderProperty providerProperty;
	private DomainProvider domainProvider;
}