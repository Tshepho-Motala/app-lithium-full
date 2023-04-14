package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_user",
    name = "incomplete_user_label_value"
)
public class IncompleteUserLabelValue {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private Integer version;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private LabelValue labelValue;
  @ManyToOne
  @JsonBackReference
  private IncompleteUser incompleteUser;
}
