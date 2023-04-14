package lithium.service.accounting.objects.frontend;

import lithium.service.accounting.objects.Account;
import lithium.service.accounting.objects.Period;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FrontendSummary implements Serializable {
	private Long tranCount;
	private Long debitCents;
	private BigDecimal debit;
	private Long creditCents;
	private BigDecimal credit;
	private Date dateStart;
	private Date dateEnd;
}
