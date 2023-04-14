package lithium.service.notifications.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
import java.io.Serializable;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString( exclude = "inbox" )
@EqualsAndHashCode( exclude = "inbox" )
@Table( indexes = {@Index( name = "idx_inbox_id", columnList = "inbox_id", unique = false ),
    @Index( name = "idx_label_value_id", columnList = "label_value_id", unique = false ),
    @Index( name = "idx_inbox_label", columnList = "inbox_id, label_id", unique = true )} )
public class InboxLabelValue implements Serializable {

  private static final long serialVersionUID = 7077952994702787092L;
  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private Long id;

  @Version
  int version;

  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn( nullable = false )
  @JsonBackReference
  private Inbox inbox;

  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn( nullable = false)
  private Label label;

  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn( nullable = false )
  private LabelValue labelValue;
}
