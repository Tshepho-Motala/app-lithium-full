package lithium.service.casino.cms.storage.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lithium.service.casino.cms.config.SqlTimeDeserializer;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(
        name = "banner",
        indexes = {
                @Index(name = "idx_time_from", columnList = "timeFrom"),
                @Index(name = "idx_time_to", columnList = "timeTo")
        }
)
public class Banner implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    @Column
    private String name;

    @Column(nullable = false, columnDefinition = "DATE DEFAULT CURRENT DATE")
    private Date startDate;

    @Column
    @JsonFormat(pattern = "HH:mm")
    @JsonDeserialize(using = SqlTimeDeserializer.class)
    private Time timeFrom;

    @Column
    @JsonFormat(pattern = "HH:mm")
    @JsonDeserialize(using = SqlTimeDeserializer.class)
    private Time timeTo;

    @Column
    private String link;

    @Column(nullable = false)
    private String imageUrl;

    @Column
    private String termsUrl;

    @Column
    private String displayText;

    @Column
    private String recurrencePattern;

    @Column
    private Integer lengthInDays;

    @Column
    private Boolean singleDay;

    @Column
    private Boolean loggedIn;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Domain domain;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "banner")
    private List<BannerSchedule> bannerSchedules = new ArrayList<>();

    @OneToMany(
            mappedBy = "tag",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<BannerTag> tags = new ArrayList<>();

}
