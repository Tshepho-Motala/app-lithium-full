package lithium.service.user.provider.threshold.data.entities;

import java.io.Serial;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "threshold_age_group")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThresholdAgeGroup implements Serializable {

  @Serial
  private static final long serialVersionUID = -4083539046290064349L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  @Version
  private int version;
  private int ageMax;
  private int ageMin;
  @ManyToOne
  private ThresholdRevision thresholdRevision;
  private boolean active;
}
