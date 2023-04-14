package lithium.service.casino.provider.incentive.storage.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickAnySettlementPick {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    int version;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private PickAnyEntryPick entryPick;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private PickAnySettlement settlement;

    @Column(nullable = false)
    private Long homeScoreResult;

    @Column(nullable = false)
    private Long awayScoreResult;

    @Column(nullable = false)
    private Long pointsResult;

}
