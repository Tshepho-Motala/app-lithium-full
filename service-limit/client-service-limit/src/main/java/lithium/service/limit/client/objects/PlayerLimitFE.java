package lithium.service.limit.client.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PlayerLimitFE implements Serializable {
	private static final long serialVersionUID = -1;

	private String playerGuid;
	private String granularity;
	private BigDecimal amount;
	private BigDecimal amountUsed;
	private String type;
	private DateTime createdDate;
	private DateTime modifiedDate;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private DateTime appliedAt;
}
