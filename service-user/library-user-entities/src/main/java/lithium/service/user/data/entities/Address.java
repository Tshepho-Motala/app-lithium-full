package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
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
@Table(
    catalog = "lithium_user",
    name = "address",
    indexes = {
        @Index(name = "idx_city", columnList = "city", unique = false),
        @Index(name = "idx_cityCode", columnList = "cityCode", unique = false),
        @Index(name = "idx_adminLevel1", columnList = "adminLevel1", unique = false),
        @Index(name = "idx_adminLevel1Code", columnList = "adminLevel1Code", unique = false),
        @Index(name = "idx_country", columnList = "country", unique = false),
        @Index(name = "idx_countryCode", columnList = "countryCode", unique = false),
        @Index(name = "idx_address_postal_code", columnList = "postalCode", unique = false),
    }
)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class Address implements Serializable {

  private static final long serialVersionUID = -8586012009095938132L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  //	@ManyToOne(fetch=FetchType.LAZY)
//	@JsonBackReference("user_address")
  private Long userId;

  @Column(nullable = false)
  private String addressLine1;

  @Column
  private String addressLine2;

  @Column
  private String addressLine3;

  @Column(nullable = false)
  private String city;

  @Column(length = 10)
  private String cityCode;

  @Column(nullable = true)
  private Boolean manualAddress = false;

  @Column
  private String adminLevel1;

  @Column(length = 10)
  private String adminLevel1Code;

  @Column(nullable = false)
  private String country;

  @Column(length = 10)
  private String countryCode;

  @Column
  private String postalCode;

  public boolean isComplete() {
    if ((addressLine1 == null) || (city == null) || (country == null)) {
      return false;
    }
    return (!addressLine1.isEmpty()) && (!city.isEmpty()) && (!country.isEmpty());
  }
}
