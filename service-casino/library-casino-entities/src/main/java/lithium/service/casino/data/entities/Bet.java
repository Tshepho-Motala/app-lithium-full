package lithium.service.casino.data.entities;

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

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
    @Index(name="idx_provider_tran_id_kind_id", columnList="provider_id, betTransactionId, kind_id", unique=true),
    @Index(name="idx_tran_timestamp", columnList="transactionTimestamp", unique=false),
    @Index(name="idx_lithium_accounting_id", columnList="lithiumAccountingId", unique=true),
    @Index(name="idx_amount", columnList="amount", unique=false)
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bet {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BetRound betRound;

    @Column(nullable = false)
    private String betTransactionId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BetRequestKind kind;

    private double amount;

    private Double balanceAfter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Currency currency;

    @Column
    private Long lithiumAccountingId;

    @Column
    private Long sessionId;
}
