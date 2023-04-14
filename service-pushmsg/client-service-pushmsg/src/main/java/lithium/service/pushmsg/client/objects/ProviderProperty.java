package lithium.service.pushmsg.client.objects;

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
public class ProviderProperty implements Serializable {
	private static final long serialVersionUID = 8819641832905779435L;
	
	private Long id;
	private int version;
	private Provider provider;
	private String name;
	private String defaultValue;
	private String type;
	private String description;
}