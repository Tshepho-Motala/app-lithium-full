package lithium.service.cashier.data.entities.frontend;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class TransactionFE {
  private Long id;
  private DateTime date;
  private String activity;
  private String method;
  private String methodCode;
  private Long amountCents;
  private BigDecimal amount;
  private Long feeCents;
  private BigDecimal fee;
  private String status;
  private String statusDisplay;
  private String processorReference;
  private String processorDescription;
  private String paymentType;
  private String errorMessage;
}
