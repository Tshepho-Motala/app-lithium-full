package lithium.service.reward.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lithium.service.reward.client.dto.FieldDataType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reward_type_field", indexes = {
    @Index( name = "idx_reward_type_name", columnList = "reward_type_id, name", unique = true ),} )
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
public class RewardTypeField {

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private Long id;

  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn( nullable = false )
  private RewardType rewardType;

  private String name; // e.g. freespins
  @Enumerated( EnumType.STRING )
  private FieldDataType dataType; // e.g. number
  private String description; // e.g. Please specify the amount of freespins to award.
}
