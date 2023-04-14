package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryLabelValueTotal implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long tranCount;
	private Long debitCents;
	private Long creditCents;
	private String accountCode;
	private String labelValue;
	private String currencyCode;
}
