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
public class SummaryAccount implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long id;
	int version;
	private Long tranCount;
	private Long debitCents;
	private Long creditCents;
	private Long openingBalanceCents;
	private Long closingBalanceCents;
	private Account account;
	private Period period;
	private boolean damaged;
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateStart;
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateEnd;
	
}
