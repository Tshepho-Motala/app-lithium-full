package lithium.service.reward.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode( exclude = "edit" )
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
public class Reward implements Serializable {

  private static final long serialVersionUID = 8622339402837301807L;

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private Long id;

  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn( nullable = false )
  private User editUser;

  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn( nullable = false )
  private Domain domain;

  @ManyToOne( fetch = FetchType.EAGER )
  private RewardRevision current;

  @ManyToOne( fetch = FetchType.LAZY )
  private RewardRevision edit;

  private Date created;
  private Date updated;

  @Override
  public String toString() {
    return new StringJoiner(", ", Reward.class.getSimpleName() + "[", "]").add("id=" + id)
        .add("editUser=" + editUser)
        .add("domain=" + domain.getName())
        .add("current=" + ((current != null) ? current.toShortString(): null))
        .add("edit=" + ((edit != null) ? edit.toShortString(): null))
        .toString();
  }

  public String toShortString() {
    return new StringJoiner(", ", Reward.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("editUser=" + editUser.guid())
        .add("domain=" + domain.getName())
        .add("current=" + current.toShortString())
        .toString();
  }

  @PreUpdate
  public void beforeUpdate() {
    updated = new Date();
  }

  @PrePersist
  public void beforePersist() {
    created = new Date();
  }}