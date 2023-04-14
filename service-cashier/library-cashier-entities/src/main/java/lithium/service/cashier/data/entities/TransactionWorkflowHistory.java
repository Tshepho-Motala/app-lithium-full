package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString(exclude = {"transaction"})
@EqualsAndHashCode(exclude = "transaction")
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(
    catalog = "lithium_cashier",
    name = "transaction_workflow_history",
    indexes = {
        @Index(name = "idx_twh_process_time", columnList = "processTime", unique = false),
        @Index(name = "idx_twh_timestamp", columnList = "timestamp", unique = false)
    }
)
public class TransactionWorkflowHistory implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true)
  private User author;

  @Column(nullable = false)
  private Date timestamp;

  @Column(nullable = true)
  private Date processTime;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  @JsonBackReference
  private Transaction transaction;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true)
  private DomainMethodProcessor processor;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private TransactionStatus status;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true)
  private User assignedTo;

  @Column(nullable = true)
  private Long accountingReference;

  @Column(nullable = false)
  private Integer stage;

  @Column(nullable = true)
  private String source;

  @Column(nullable = true)
  private String billingDescriptor;

  @Transient
  private List<TransactionComment> comments;


  @PrePersist
  private void prePersist() {
    if (this.timestamp == null) {
      this.timestamp = new Date();
    }
  }



}
