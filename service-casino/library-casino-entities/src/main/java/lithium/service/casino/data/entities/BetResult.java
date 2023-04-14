package lithium.service.casino.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
    @Index(name="idx_provider_tran_id", columnList="provider_id, betResultTransactionId", unique=true),
    @Index(name="idx_tran_timestamp", columnList="transactionTimestamp", unique=false),
    @Index(name="idx_lithium_accounting_id", columnList="lithiumAccountingId", unique=true),
    @Index(name="idx_returns", columnList="returns", unique=false)
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BetResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    int version;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;

    @LastModifiedDate
    private long modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private String betResultTransactionId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionTimestamp;

    @JsonBackReference("betRound")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BetRound betRound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BetResultKind betResultKind;

    @Column(nullable = false)
    private double returns;

    private double balanceAfter;

    @Column(nullable = false)
    private boolean roundComplete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Currency currency;

    private Long lithiumAccountingId;

}

