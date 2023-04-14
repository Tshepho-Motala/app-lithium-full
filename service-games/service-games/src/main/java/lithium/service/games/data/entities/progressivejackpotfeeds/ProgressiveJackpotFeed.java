package lithium.service.games.data.entities.progressivejackpotfeeds;

import lithium.service.games.data.entities.GameSupplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Date;

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "progressive_jackpot_feed"
)
public class ProgressiveJackpotFeed {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    private Boolean enabled;

    @Column(name = "registered_on")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date registeredOn;

    @Column(name = "last_updated_on")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date lastUpdatedOn;

    @ManyToOne
    @JoinColumn
    private Module module;

    @ManyToOne
    @JoinColumn
    private GameSupplier gameSupplier;
}
