package lithium.service.settlement.client.objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
public class SettlementEntry implements Serializable {
	private static final long serialVersionUID = -6619751689766864590L;
	
	private Long id;
	private int version;
	private Settlement settlement;
	private BigDecimal amount;
	private Date dateStart;
	private Date dateEnd;
	private String description;
	private List<SettlementEntryLabelValue> labelValues;
	private Map<String, String> labelValueMap;
}
