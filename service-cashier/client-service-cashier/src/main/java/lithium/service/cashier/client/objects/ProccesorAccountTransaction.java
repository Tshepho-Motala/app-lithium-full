package lithium.service.cashier.client.objects;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;
import java.math.BigDecimal;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class ProccesorAccountTransaction {
  private Long id;
  private DateTime date;
  private String user;
  private String method;
  private String state;
  private String redirectUrl;
  private String generalError;
  private ProcessorAccount processorAccount;
}
