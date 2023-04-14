package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "user")
@Table(
    catalog = "lithium_user",
    name = "user_data_entry",
    indexes = {@Index(name = "idx_user_datakey", columnList = "user_id, dataKey", unique = true)})
public class UserDataEntry implements Serializable {

  private static final long serialVersionUID = 1L;
  @Version
  int version;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  @JsonBackReference
  private User user;

  private String dataKey;
  private String dataValue;
}
