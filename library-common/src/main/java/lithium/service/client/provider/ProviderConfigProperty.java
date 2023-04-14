package lithium.service.client.provider;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
public class ProviderConfigProperty implements Serializable{
	private static final long serialVersionUID = 8031155019608843468L;
	
	@Builder.Default
	private long version = 1L;
	private String name;
	@Builder.Default
	private boolean required = true;
	@Builder.Default
	private boolean disabled = false;
	private String tooltip;
	//Not sure if this should just be a string with datatype hint, leaving it as class for now
	@Builder.Default
	private Class<?> dataType = String.class;
}
