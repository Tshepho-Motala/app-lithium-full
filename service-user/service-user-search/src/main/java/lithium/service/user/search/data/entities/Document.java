package lithium.service.user.search.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity(name = "user_search.Documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_user_search",
    name = "documents"
)
public class Document implements Serializable {
  @Id
  private long id;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @OneToOne
  @JoinColumn(name = "status_id")
  private DocumentStatus status;

  @Column(nullable = false, name = "sensitive_doc")
  private boolean sensitive;

  @Column(nullable = false, name = "deleted")
  private boolean deleted;
}
