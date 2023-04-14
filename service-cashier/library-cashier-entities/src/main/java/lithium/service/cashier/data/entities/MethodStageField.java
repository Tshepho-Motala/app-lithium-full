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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    name = "method_stage_field"
)
public class MethodStageField implements Serializable {

  private static final long serialVersionUID = -969339311761532415L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private MethodStage stage;

  @Column(nullable = false)
  private boolean input;

  @Column(nullable = false)
  private String code;
  @Column(nullable = false)
  private String type;
  @Column(nullable = false)
  private String name;
  @Column(length = 5000)
  private String description;
  private Integer sizeXs;
  private Integer sizeMd;
  private Integer displayOrder;
  private Boolean required;

  @PrePersist
  void defaults() {
    if (type == null) {
      type = "string";
    }
    if (required == null) {
      required = true;
    }
  }

}
