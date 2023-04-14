package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.service.cashier.data.views.Views;
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
    name = "domain_method"
)
public class DomainMethod implements Serializable {

  private static final long serialVersionUID = 6386377983884071315L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  private String name;

  private String description;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "image_id")
  @JsonView(Views.Image.class)
  private Image image;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private Method method;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private Domain domain;

  @Column(nullable = false)
  private Boolean enabled;

  @Column(nullable = false)
  private Boolean deleted;

  @Column(nullable = false)
  private Boolean deposit;

  @Column(nullable = false)
  private Integer priority;

  @Column(nullable = true)
  private String accessRule; // Access rule to be called when display of dm is being validated

  @Column(nullable = true)
  private String accessRuleOnTranInit; // Access rule to be called when deposit transaction is initialized

  @Column(nullable = false)
  private Boolean feDefault; // The default method the frontend will have selected

  @PrePersist
  private void prePersist() {
    if (priority == null) {
      priority = 999;
    }
  }
}
