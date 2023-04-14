package lithium.service.user.data.entities;

import java.io.Serial;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@Table
public class CollectionDataRevisionEntry implements Serializable {

  @Serial
  private static final long serialVersionUID = -5594724707826357379L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long id;

  @ManyToOne
  @JoinColumn(nullable = false)
  public CollectionDataRevision collectionRevision;

  @ManyToOne
  @JoinColumn(nullable = false)
  public CollectionData collectionData;

  @ManyToOne
  @JoinColumn(nullable = false)
  public CollectionDataRevision lastUpdatedRevision;

  @Version
  public int version;

}
