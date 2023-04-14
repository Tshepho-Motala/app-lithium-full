
package lithium.service.casino.provider.incentive.storage.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
    @Index(name="idx_tran_id", columnList="betTransactionId", unique=true),
    @Index(name="idx_tran_timestamp", columnList="transactionTimestamp", unique=false),
    @Index(name="idx_lithium_accounting_id", columnList="lithiumAccountingId", unique=false)
})
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    int version;

    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionTimestamp;

    @Column(nullable = false)
    private String betTransactionId;

    private double totalOdds;
    private double totalStake;
    private double maxPotentialWin;

    private Long lithiumAccountingId;
    private Integer errorCode;
    private String errorMessage;
    @Column(nullable = true)
    private Long virtualCoinId; //This is the player_bonus_token_id in casino

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Placement placement;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;

    @LastModifiedDate
    private long modifiedDate;

    @JsonManagedReference("bet")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Settlement settlement;

    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch=FetchType.EAGER, mappedBy="bet", cascade=CascadeType.MERGE)
    @JsonManagedReference("Bet")
    private List<BetSelection> betSelections;

    @Transient
    private String betType;

    @Transient
    private BigDecimal postEntryAccountBalance;

    @PrePersist
    @PreUpdate
    public void sanitise() {
        if (errorMessage != null && errorMessage.length() > 255) {
            errorMessage = errorMessage.substring(0, 255);
        }
    }
}
