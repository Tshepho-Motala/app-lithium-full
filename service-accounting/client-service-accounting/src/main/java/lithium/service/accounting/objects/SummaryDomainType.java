package lithium.service.accounting.objects;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDomainType implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long tranCount;
	private Long debitCents;
	private Long creditCents;
	private Long openingBalanceCents;
	private Long closingBalanceCents;
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateStart;
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateEnd;
}
