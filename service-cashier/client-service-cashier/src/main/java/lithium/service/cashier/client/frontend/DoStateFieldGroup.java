package lithium.service.cashier.client.frontend;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoStateFieldGroup {
	private String header;
	private String description;
	private Map<String, DoStateField> fields = new HashMap<>();
}