package lithium.service.user.threshold.data.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.service.client.objects.Granularity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table( indexes = {@Index( name = "idx_threshold_all", columnList = "domain_id,ageMin,ageMax,type_id,granularity", unique = true )} )
public class Threshold implements Serializable {

  @Serial
  private static final long serialVersionUID = 5371960597017399009L;

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private long id;
  @Version
  private int version;
  @OneToOne
  @JsonManagedReference( "threshold" )
  private ThresholdRevision current;

  private boolean active = true;
  private Integer ageMin;
  private Integer ageMax;

  @ManyToOne
  private Domain domain;
  @ManyToOne
  private Type type;

  @Column
  @Enumerated( EnumType.STRING )
  @JsonFormat( shape = JsonFormat.Shape.STRING )
  private Granularity granularity;

  @PreUpdate
  @PrePersist
  void defaults() {
    if (ageMin == null) {
      ageMin = -1;
    }
    if (ageMax == null) {
      ageMax = -1;
    }
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Threshold.class.getSimpleName() + "[", "]").add("id=" + id)
        .add("current=" + current.toShortString())
        .add("active=" + active)
        .add("ageMin=" + ageMin)
        .add("ageMax=" + ageMax)
        .add("domain=" + domain.getName())
        .add("type=" + type.getName())
        .add("granularity=" + granularity.type())
        .toString();
  }
}