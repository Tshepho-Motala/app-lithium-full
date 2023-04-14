package lithium.service.user.data.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(catalog = "lithium_user",
    name = "status_status_reason_association",
    indexes = {
        @Index(name = "idx_status_status_reason", columnList = "status_id, reason_id", unique = true)
    })
public class StatusStatusReasonAssociation {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private Status status;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private StatusReason reason;
}
