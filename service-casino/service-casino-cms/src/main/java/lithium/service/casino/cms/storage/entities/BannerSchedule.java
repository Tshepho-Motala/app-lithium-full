package lithium.service.casino.cms.storage.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
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
import java.io.Serializable;
import java.time.ZoneId;
import java.util.Date;

@Data
@Entity
@Table(
        name = "banner_schedule",
        indexes = {
                @Index(name = "idx_start_date", columnList = "startDate"),
                @Index(name = "idx_end_date", columnList = "endDate")
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class BannerSchedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    private Date startDate;

    private Date endDate;

    private Boolean closed;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean deleted = false;

    @ManyToOne
    @JoinColumn(name="banner_id", nullable = false)
    private Banner banner;


    public static BannerSchedule create(Banner banner, Date startDate) {
        BannerSchedule bannerSchedule = new BannerSchedule();
        bannerSchedule.setStartDate(startDate);
        Date endDate = Date.from(bannerSchedule.getStartDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(banner.getLengthInDays() - 1)
                .atTime(23, 59, 59)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        bannerSchedule.setEndDate(endDate);
        bannerSchedule.setDeleted(false);
        bannerSchedule.setClosed(false);
        return bannerSchedule;
    }

}
