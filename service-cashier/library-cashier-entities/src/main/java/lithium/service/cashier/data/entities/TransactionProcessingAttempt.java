package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(
    catalog = "lithium_cashier",
    name = "transaction_processing_attempt"
)
public class TransactionProcessingAttempt implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private Date timestamp;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private Transaction transaction;

  //TODO Link to domain processor instance

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private TransactionWorkflowHistory workflowFrom;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private TransactionWorkflowHistory workflowTo;

  @Column(nullable = true)
  private Boolean success;

  @Column(nullable = true, length = 32000)
  private String processorMessages;

  @Column(nullable = true)
  private String processorReference;

  @Column(nullable = true, length = 32000)
  private String processorRawRequest;

  @Column(nullable = true, length = 32000)
  private String processorRawResponse;

  @Column(nullable = false)
  private boolean cleaned;

  @PrePersist
  private void prePersist() {
    if (timestamp == null) {
      timestamp = new Date();
    }
  }
}
