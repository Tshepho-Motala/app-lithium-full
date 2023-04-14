package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@Setter
@Getter
@Table
public class CollectionDataRevision implements Serializable {

  @Serial
  private static final long serialVersionUID = -2755401542833368985L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long id;

  @ManyToOne
  @JoinColumn(nullable = false)
  @JsonIgnore
  public User user;

  @Column(nullable = false)
  public Date creationDate;

  @Version
  public int version;

}
