package lithium.service.cashier.client.frontend;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoRequest {
	private Long transactionId;
	@Default
	private Map<String, DoStateFieldGroup> inputFieldGroups = new HashMap<>();
	private Integer stage;
	private String state;
	private Boolean mobile;
	private String deviceId;
	
	public String inputField(String field, String stage) {
		if (inputFieldGroups!=null) {
			DoStateFieldGroup doStateFieldGroup = inputFieldGroups.get(stage);
			if (doStateFieldGroup!=null) {
				DoStateField doStateField = doStateFieldGroup.getFields().get(field);
				if (doStateField!=null) {
					String value = doStateField.getValue();
					if ((value!=null)&&(!value.isEmpty())) return value;
				}
			}
		}
		return null;
	}
	
	public String productGuid() {
		return inputField("productGuid", "1");
	}
	
	public boolean hasProductGuid() {
		if (productGuid()!=null) return true;
		return false;
	}
}
