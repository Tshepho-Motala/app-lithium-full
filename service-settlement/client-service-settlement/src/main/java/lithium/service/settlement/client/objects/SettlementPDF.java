package lithium.service.settlement.client.objects;

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
public class SettlementPDF {
	private Long id;
	private int version;
	private byte[] pdf;
	private Boolean sent;
}
