package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"domainMethodProcessorProfiles", "domainMethodProcessorUsers"})
@JsonIgnoreProperties({"domainMethodProcessorProfiles", "domainMethodProcessorUsers"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(
    catalog = "lithium_cashier",
    name = "domain_method_processor"
)
public class DomainMethodProcessor implements Serializable {

  private static final long serialVersionUID = -9017531520181295510L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  @Column(nullable = true)
  private String description;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private Processor processor;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private DomainMethod domainMethod;

  private Double weight;

  @Column(nullable = false)
  private Boolean enabled;

  @Column(nullable = true)
  private Boolean active;

  @Column(nullable = false)
  private Boolean deleted;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "fees_id")
  private Fees fees;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "limits_id")
  private Limits limits;  // Global Player Limits - Used to be DL

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "domain_limits_id")
  @Builder.Default
  private Limits domainLimits = Limits.builder().build();

  @Builder.Default
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "domainMethodProcessor", cascade = CascadeType.ALL)
  private List<DomainMethodProcessorProfile> domainMethodProcessorProfiles = new ArrayList<>();

  @Builder.Default
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "domainMethodProcessor", cascade = CascadeType.ALL)
  private List<DomainMethodProcessorUser> domainMethodProcessorUsers = new ArrayList<>();

  @Column(nullable = true)
  private String accessRule; // Access rule to be called when display of dmp is being validated

  @Column(nullable = true)
  private String accessRuleOnTranInit; // Access rule to be called when deposit/payout transaction is initialized

  @Column(nullable = true)
  private Boolean reserveFundsOnWithdrawal;
}
