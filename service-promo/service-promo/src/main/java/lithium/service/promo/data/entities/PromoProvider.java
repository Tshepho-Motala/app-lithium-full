package lithium.service.promo.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
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

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"activities"})
@Table(indexes = {
  @Index(name = "idx_promo_provider_url_and_category_id", columnList = "url, category_id", unique = true)
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class PromoProvider implements Serializable {

  @Serial
  private static final long serialVersionUID = 569391788045587554L;

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private long id;

  private String name;
  private String url;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "category_id")
  private Category category;

  @Singular
  @OneToMany( fetch = FetchType.EAGER, mappedBy = "promoProvider", cascade = CascadeType.ALL )
  private List<Activity> activities;
}