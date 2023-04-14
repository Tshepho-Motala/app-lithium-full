package lithium.service.reward.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import lithium.service.client.objects.Granularity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode( exclude = {"reward"} )
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
public class RewardRevision implements Serializable {

  private static final long serialVersionUID = -4674034648629362157L;
  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private Long id;
  private String name;
  private String code;
  private String description;
  private boolean enabled;

  private Integer validFor;

  @Enumerated( EnumType.STRING )
  private Granularity validForGranularity;

  @Transient //adding this here since this field is not used in the mean, will revisit this in the future when there is a need for it
  private String activationNotificationName;

  private Date created;
  private Date updated;

  @ManyToOne
  private Reward reward;

  @Singular
  @OneToMany( fetch = FetchType.EAGER, mappedBy = "rewardRevision", cascade = CascadeType.ALL )
  private List<RewardRevisionType> revisionTypes;

  @Override
  public String toString() {
    return new StringJoiner(", ", RewardRevision.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("name='" + name + "'")
        .add("code='" + code + "'")
        .add("description='" + description + "'")
        .add("enabled=" + enabled)
        .add("validFor=" + validFor)
        .add("validForGranularity=" + validForGranularity.name())
        .add("activationNotificationName='" + activationNotificationName + "'")
        .add("reward=" + reward.toShortString())
        .add("revisionTypes=" + revisionTypes)
        .toString();
  }

  public String toShortString() {
    return new StringJoiner(", ", RewardRevision.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("code='" + code + "'")
        .add("enabled=" + enabled)
        .add("revisionTypes=" + revisionTypes)
        .toString();
  }

  @PreUpdate
  public void beforeUpdate() {
    updated = new Date();
  }

  @PrePersist
  public void beforePersist() {
    created = new Date();
  }
}
