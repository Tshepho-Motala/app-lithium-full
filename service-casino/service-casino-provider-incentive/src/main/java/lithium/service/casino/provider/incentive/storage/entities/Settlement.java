package lithium.service.casino.provider.incentive.storage.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.util.Date;

/* Event database to prevent massive duplication in selection table of similar events */

@Data
@ToString(exclude = "bet")
@EqualsAndHashCode(exclude = "bet")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
    @Index(name="idx_tran_id", columnList="settlementTransactionId", unique=true),
    @Index(name="idx_tran_timestamp", columnList="transactionTimestamp", unique=false),
    @Index(name="idx_lithium_accounting_id", columnList="lithiumAccountingId", unique=false)
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    int version;

    @Column(nullable = false)
    private String settlementTransactionId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionTimestamp;

    @JsonBackReference("bet")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Bet bet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private SettlementResult settlementResult;

    @Column(nullable = false)
    private double returns;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Currency currency;

    private Long lithiumAccountingId;
    private Integer errorCode;
    private String errorMessage;

}

