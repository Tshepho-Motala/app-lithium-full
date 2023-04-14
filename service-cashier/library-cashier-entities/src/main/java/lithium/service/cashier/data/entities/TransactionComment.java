package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity(name = "cashier.TransactionComment")
@Builder
@ToString(exclude = {"transaction", "workflow"})
@EqualsAndHashCode(exclude = {"transaction", "workflow"})
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(
    catalog = "lithium_cashier",
    name = "transaction_comment"
)
public class TransactionComment implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private Date timestamp;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  @JsonIgnore
  private Transaction transaction;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  @JsonIgnore
  private TransactionWorkflowHistory workflow;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private User author;

  @Column(nullable = true, length = 2000)
  private String comment;
}
