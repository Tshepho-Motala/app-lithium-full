package lithium.service.cashier.processor.cc.ecardon.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultDetails {
	private String extendedDescription; // "Pending",
	private String acquirerResponse; //"PENDING",
	private String connectorTxID1; //"18b02d230-a6822f-4cbb-aee9-0bc07d90cfa4"
}
