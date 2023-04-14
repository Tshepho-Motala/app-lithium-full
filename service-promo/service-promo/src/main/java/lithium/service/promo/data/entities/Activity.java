package lithium.service.promo.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString( exclude = "promoProvider" )
@Table(indexes = {
        @Index(name = "idx_provider_activity", columnList = "promo_provider_id, name", unique = true )} )
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
public class Activity implements Serializable {

  @Serial
  private static final long serialVersionUID = -2795725018332183741L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  @JsonManagedReference("activities")
  private PromoProvider promoProvider;

  private String name; // e.g. freespins
  private Boolean requiresValue; //Some activities do not really require a value for, e.g registration-success

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "activity", cascade = CascadeType.ALL)
  @Singular
  @JsonManagedReference("activity")
  private List<ActivityExtraField> extraFields;
}
