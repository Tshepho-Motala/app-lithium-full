package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.service.cashier.converter.StringTruncate255Converter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "cashier.ProcessorAccountTransaction")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_cashier",
    name = "processor_account_transaction"
)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class ProcessorAccountTransaction implements Serializable {

  private static final long serialVersionUID = -969339311761532415L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonManagedReference
  private Long id;

  @Version
  private int version;

  @Column(nullable = false)
  private Date createdOn;

  @Column(nullable = true)
  private String processorReference;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private User user;

  @Column(nullable = false)
  private String errorCode;

  @Column(length = 255, nullable = true)
  @Convert(converter = StringTruncate255Converter.class)
  private String errorMessage;

  @Column(length = 255, nullable = true)
  @Convert(converter = StringTruncate255Converter.class)
  private String generalError;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private DomainMethodProcessor domainMethodProcessor;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "processor_account_id", referencedColumnName = "id", nullable = true)
  private ProcessorUserCard processorAccount;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "state_id")
  private ProcessorAccountTransactionState state;

  @Column(nullable = true)
  private String redirectUrl;

  @PrePersist
  private void prePersist() {
    if (this.createdOn == null) {
      this.createdOn = new Date();
    }
  }
}
