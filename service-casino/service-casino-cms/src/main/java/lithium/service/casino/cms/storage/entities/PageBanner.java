package lithium.service.casino.cms.storage.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Entity
@Table(
        name = "page_banner",
        indexes = {
                @Index(name = "idx_lobby_page_banner", columnList = "primaryNavCode, secondaryNavCode, channel, lobby_id, deleted" )
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageBanner implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Integer version;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "lobby_id")
    private Lobby lobby;

    @ManyToOne
    @JoinColumn(name = "banner_id")
    private Banner banner;

    @Column(nullable = false)
    private String primaryNavCode;

    @Column(nullable = false)
    private String secondaryNavCode;

    @Column(nullable = false)
    private String channel;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer position;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean deleted = false;

}
