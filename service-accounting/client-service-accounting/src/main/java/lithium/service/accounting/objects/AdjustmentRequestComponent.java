package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentRequestComponent implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long amountCents;
	private DateTime date;
	private String accountCode;
	private String accountTypeCode;
	private String transactionTypeCode;
	private String contraAccountCode;
	private String contraAccountTypeCode;
	private String[] labels;
	private String currencyCode;
	private String domainName;
	private String ownerGuid;
	private String authorGuid;
	private Boolean allowNegativeAdjust;
	private List<ConstraintValidation> constraintValidations;

}
