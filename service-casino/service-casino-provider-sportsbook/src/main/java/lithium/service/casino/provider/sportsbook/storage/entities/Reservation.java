package lithium.service.casino.provider.sportsbook.storage.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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

@Entity
@Data
@ToString(exclude = { "reservationCancel", "reservationCommit" })
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name="idx_reserve_id", columnList="reserveId", unique=true),
        @Index(name="idx_res_cancel", columnList="reservation_cancel_id", unique=true),
        @Index(name="idx_res_commit", columnList="reservation_commit_id", unique=true),
        @Index(name="idx_accounting_id", columnList="accountingTransactionId", unique=true),
        @Index(name="idx_session_id", columnList="sessionId", unique=false),
        @Index(name="idx_timestamp", columnList="timestamp", unique=false),
        @Index(name="idx_reservation_status_id_acc_last_rechecked", columnList="reservation_status_id, accountingLastRechecked", unique=false),
        @Index(name="idx_created_date", columnList = "createdDate", unique = false)
})
@EqualsAndHashCode
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Reservation {
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private long reserveId;

    @Column(nullable = false)
    private Double amount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Column(nullable = true)
    private Double balanceAfter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Currency currency;

    @Column(nullable = true)
    private Long accountingTransactionId;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private Double bonusUsedAmount;

    @Column(nullable = false)
    private Double totalBetAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn()
    private ReservationStatus reservationStatus;

    @Column(nullable = true)
    private Date accountingLastRechecked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private ReservationCancel reservationCancel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private ReservationCommit reservationCommit;

    @Column(nullable = true)
    private Long betCount;
}
