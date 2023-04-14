package lithium.service.casino.provider.roxor.storage.entities;

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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Operation {
    @Version
    int version;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;

    @LastModifiedDate
    private long modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private GamePlay gamePlay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private GamePlayRequest gamePlayRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private OperationType operationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Source source;

    @Column
    private Long amountCents;

    @Column
    private String transferId;

    @Column
    private String poolId;

    @Column
    private String accrualId;

    @Column
    private String reference;

    @Column
    private Long lithiumAccountingId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        REQUESTED,
        PROCESSING,
        RESULT,
        ERROR,
        IGNORE,
        DUPLICATE
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Operation associatedOperation;
}
