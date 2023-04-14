package lithium.service.casino.provider.sportsbook.storage.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.util.Date;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@ToString
public class OpenBetsOpMigrationAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    private Date createdDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn
    private Bet bet;

    @ManyToOne
    @JoinColumn
    private ReservationCommit reservationCommit;
}
