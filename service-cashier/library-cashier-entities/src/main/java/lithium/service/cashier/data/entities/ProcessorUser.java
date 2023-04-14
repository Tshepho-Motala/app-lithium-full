package lithium.service.cashier.data.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_cashier",
    name = "processor_user",
    indexes = {
        @Index(name = "idx_pu_unq", columnList = "user_id, domain_method_processor_id", unique = true)
    }
)
public class ProcessorUser implements Serializable {

  private static final long serialVersionUID = -1;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "domain_method_processor_id", nullable = false)
  private DomainMethodProcessor domainMethodProcessor;

  private String processorUserId; //The user id assigned to the user by the specific processor
}
