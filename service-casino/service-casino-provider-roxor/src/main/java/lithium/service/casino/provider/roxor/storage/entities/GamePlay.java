package lithium.service.casino.provider.roxor.storage.entities;

import lithium.jpa.entity.EntityWithUniqueGuid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import java.util.Set;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_game_play_guid", columnList = "guid", unique = true),
        @Index(name = "idx_game_play_roxor_status", columnList = "roxorStatus", unique = false),
        @Index(name="idx_created_date", columnList="createdDate", unique=true)
})
public class GamePlay implements EntityWithUniqueGuid {

    @Version
    int version;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String guid; //gamePlayId

    /*@OneToMany(fetch = FetchType.LAZY, mappedBy = "gamePlay", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(nullable = false)
    private Set<GamePlayRequest> gamePlayRequestSet;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Platform platform;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;

    @LastModifiedDate
    private long modifiedDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoxorStatus roxorStatus;

    public enum RoxorStatus {
        STARTED,
        FINISHED
    }

    @Column
    private Long balanceAfter;
}
