package lithium.service.user.data.entities;

import java.io.Serial;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Table
public class CollectionData implements Serializable {

  @Serial
  private static final long serialVersionUID = 4402604623147601278L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long id;

  @Column(nullable = false)
  public String collectionName;

  @Column(nullable = false)
  public String dataKey;

  @Column(nullable = true)
  public String dataValue;

  @Version
  public int version;

}
