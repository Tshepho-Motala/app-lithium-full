package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(
    catalog = "lithium_cashier",
    name = "processor",
    indexes = {
        @Index(name = "idx_code", columnList = "code", unique = true)
    }
)
public class Processor implements Serializable {

  private static final long serialVersionUID = -969339311761532415L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  @Column(nullable = false)
  private Boolean enabled;

  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private Boolean deposit;

  @Column(nullable = false)
  private Boolean withdraw;

  private String name;

  @Column(nullable = false)
  private String url;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "processor", cascade = CascadeType.ALL)
  private List<ProcessorProperty> properties = new ArrayList<>();

  @OneToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "fees_id")
  private Fees fees;

  @OneToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "limits_id")
  private Limits limits;

  @PrePersist
  private void prePersist() {
    if (enabled == null) {
      enabled = true;
    }
    if (deposit == null) {
      deposit = false;
    }
    if (withdraw == null) {
      withdraw = false;
    }
  }

}
