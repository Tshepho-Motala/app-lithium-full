package lithium.service.promo.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serial;
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
@Table(indexes = {
        @Index( name = "idx_activity_extra_field_rule", columnList = "activity_extra_field_id, rule_id" ),} )
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
public class ActivityExtraFieldRuleValue implements Serializable {

  @Serial
  private static final long serialVersionUID = -2096901384920126196L;

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private long id;

  @Version
  private int version;

  @Column
  private String value;

  @OneToOne
  @JoinColumn(name = "activity_extra_field_id")
  private ActivityExtraField activityExtraField;

  @ManyToOne( fetch = FetchType.EAGER )
  private Rule rule;

}
