package lithium.service.settlement.client.objects;

import java.util.Date;
import java.util.List;

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
public class BatchSettlements {
	private Long id;
	private String name;
	private Date dateStart;
	private Date dateEnd;
	private List<Settlement> settlements;
	private Domain domain;
}
