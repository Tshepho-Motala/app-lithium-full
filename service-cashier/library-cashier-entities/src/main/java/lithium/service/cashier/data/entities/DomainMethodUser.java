package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.Column;
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
    name = "domain_method_user",
    indexes = {
        @Index(name = "idx_pt_domain_method_id_user_id", columnList = "domain_method_id, user_id", unique = true),
    }
)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class DomainMethodUser implements Serializable {

  private static final long serialVersionUID = 5566608334690890932L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private DomainMethod domainMethod;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private User user;

  @Column(nullable = true)
  private Boolean enabled;

  @Column(nullable = true)
  private Integer priority;
}
