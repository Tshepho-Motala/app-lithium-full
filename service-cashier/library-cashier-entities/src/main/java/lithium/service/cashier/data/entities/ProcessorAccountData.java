package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.Map;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lithium.service.cashier.converter.JsonToMapConverter;
import lithium.service.cashier.data.views.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "cashier.ProcessorAccountData")
@Table(catalog = "lithium_cashier", name = "processor_account_data", indexes = {
    @Index(name="idx_processor_account", columnList="processor_account_id", unique=true),
})
public class ProcessorAccountData {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonView(Views.Internal.class)
  private long id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "processor_account_id", nullable = false)
  private ProcessorUserCard processorAccount;

  @Convert(converter = JsonToMapConverter.class)
  private Map<String, String> data;
}

