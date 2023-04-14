package lithium.service.user.data.entities;

import java.io.Serializable;
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
import javax.persistence.Version;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "user.LabelValue")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(catalog = "lithium_user",
    name = "label_value", indexes = {
    @Index(name = "idx_label_sha1", columnList = "label_id, sha1", unique = true),
})
public class LabelValue implements Serializable {

  private static final long serialVersionUID = 1L;
  @Version
  int version;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  @Column(nullable = true)
  @Size(min = 0, max = 4096)
  private String value;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private Label label;

  @Column(nullable = false)
  private String sha1;
}
