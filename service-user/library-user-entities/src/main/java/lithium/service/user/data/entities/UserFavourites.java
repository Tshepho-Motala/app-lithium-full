package lithium.service.user.data.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    catalog = "lithium_user",
    name = "user_favourites"
)
public class UserFavourites implements Serializable {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  public Long id;

  @Column(nullable = true)
  @Lob
  public String events;

  @Column(nullable = true)
  @Lob
  public String competitions;

  @Column(nullable = false)
  public Date lastUpdated;
}
