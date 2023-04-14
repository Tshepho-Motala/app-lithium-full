package lithium.service.games.data.entities.progressivejackpotfeeds;

import com.fasterxml.jackson.annotation.JsonInclude;
import lithium.service.games.data.entities.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "progressive_jackpot_game_balance"
)
public class ProgressiveJackpotGameBalance {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    int version;

    @Column(name = "progressive_id")
    private String progressiveId;

    @Column(name = "amount")
    private BigDecimal amount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "won_by_amount")
    private BigDecimal wonByAmount;

    @ManyToOne
    @JoinColumn
    private Game game;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Currency currency;
}
