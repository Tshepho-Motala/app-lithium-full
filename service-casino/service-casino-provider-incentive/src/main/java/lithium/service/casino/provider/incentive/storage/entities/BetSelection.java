package lithium.service.casino.provider.incentive.storage.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class BetSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    int version;

    private double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @JsonBackReference("Bet")
    private Bet bet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Selection selection;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private SelectionType selectionType;

    @ManyToOne(fetch = FetchType.EAGER)
    private Sport sport;

    @ManyToOne(fetch = FetchType.EAGER)
    private Competition competition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Market market;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Event event;

    @JsonManagedReference("BetSelection")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = true)
    private SettlementSelection settlementSelection;
}

