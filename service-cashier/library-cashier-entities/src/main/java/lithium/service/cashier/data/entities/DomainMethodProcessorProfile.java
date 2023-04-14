package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
    name = "domain_method_processor_profile",
    indexes = {
        @Index(name = "idx_pt_domain_method_processor_id_profile_id", columnList = "domain_method_processor_id, profile_id", unique = true),
    }
)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class DomainMethodProcessorProfile implements Serializable {

  private static final long serialVersionUID = -5284994979078951572L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "limits_id")
  private Limits limits;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "fees_id")
  private Fees fees;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private DomainMethodProcessor domainMethodProcessor;

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(nullable=false)
//	private DomainMethodProfile domainMethodProfile;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private Profile profile;

  private Double weight;
  private Boolean enabled;

}
