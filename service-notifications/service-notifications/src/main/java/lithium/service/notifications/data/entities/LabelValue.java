package lithium.service.notifications.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
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
import java.io.Serializable;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table( indexes = {@Index( name = "idx_label_value", columnList = "label_id, value", unique = true )} )
public class LabelValue implements Serializable {

  private static final long serialVersionUID = -3894743098667650308L;
  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private long id;

  @Version
  int version;

  @Column( nullable = true )
  @Size( min = 0, max = 2048 )
  private String value;

  @ManyToOne( fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinColumn(name = "label_id", referencedColumnName = "id", nullable = false)
  private Label label;
}
