package lithium.service.domain.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderProperty implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Provider provider;
	private String name;
	private String value;
}