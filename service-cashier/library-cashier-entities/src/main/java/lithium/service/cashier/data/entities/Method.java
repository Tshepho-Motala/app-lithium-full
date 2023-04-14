package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.service.cashier.data.views.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
    name = "method",
    indexes = {
        @Index(name = "idx_code", columnList = "code", unique = true)
    }
)
public class Method implements Serializable {

  private static final long serialVersionUID = -5791186162662490522L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  @Column(nullable = false)
  private Boolean enabled;

  @Default
  private Boolean inApp = false;

  private String platform; //apple/google/null

  @OneToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "image_id")
  @JsonView(Views.Image.class)
  private Image image;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String code;

  @PrePersist
  private void prePersist() {
    if (enabled == null) {
      enabled = true;
    }
    if (inApp == null) {
      inApp = false;
    }
  }
}
