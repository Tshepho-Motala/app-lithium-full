package lithium.service.reward.data.entities;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reward_revision_type_value", indexes = {
  @Index( name = "idx_revision_type_value", columnList = "reward_type_field_id, reward_revision_type_id", unique = false ),
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RewardRevisionTypeValue implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private long id;

  @Version
  private int version;

  @Column
  private String value;

  @Builder.Default
  @Column(nullable=false)
  private Boolean deleted = false;

  @OneToOne
  @JoinColumn
  private RewardTypeField rewardTypeField;

  @ManyToOne( fetch = FetchType.EAGER )
  private RewardRevisionType rewardRevisionType;
}
