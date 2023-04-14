package lithium.service.user.search.data.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "user_search.User")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_user_search",
    name = "user",
    indexes = {
        @Index(name = "idx_user_guid", columnList = "guid", unique = true),
    }
)
public class User implements Serializable {

  private static final long serialVersionUID = 167897689L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Version
  int version;

  @Column(nullable = false)
  private String guid;

}
