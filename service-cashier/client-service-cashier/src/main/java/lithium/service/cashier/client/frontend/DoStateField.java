package lithium.service.cashier.client.frontend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoStateField {
	
	private String code;
	private String type;
	private String name;
	private String description;
	private Integer sizeXs;
	private Integer sizeMd;
	private String value;
	private String valueError;
	private Boolean required;
	private Integer displayOrder;
	@Default
	private Boolean readOnly = false;
	private String extra;
	
	public static class DoStateFieldBuilder {
		private Boolean required = true;
	}
}