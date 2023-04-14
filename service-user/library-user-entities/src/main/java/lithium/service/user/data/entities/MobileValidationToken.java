package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(catalog = "lithium_user",
    name = "mobile_validation_token",
    indexes = {@Index(name = "idx_upt_createdon", columnList = "createdOn")})
public class MobileValidationToken {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(nullable = false, unique = false)
  private String token;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(unique = true, nullable = false)
  private User user;

  @Column(nullable = false)
  private Date createdOn;

  public MobileValidationToken(String token, User user, Date createdOn) {
    this.token = token;
    this.user = user;
    this.createdOn = createdOn;
  }
}
