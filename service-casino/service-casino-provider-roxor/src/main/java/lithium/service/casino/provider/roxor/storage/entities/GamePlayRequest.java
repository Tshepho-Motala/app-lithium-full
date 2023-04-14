package lithium.service.casino.provider.roxor.storage.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_game_play_request_header_session_key", columnList = "headerSessionKey", unique = false),
        @Index(name = "idx_game_play_request_header_game_play_id", columnList = "headerGamePlayId", unique = false),
        @Index(name = "idx_game_play_request_status", columnList = "status", unique = false),
})
public class GamePlayRequest {
    @Version
    int version;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = true)
    private String headerSessionKey;

    @Column(nullable = false)
    private String headerGamePlayId;

    @Column
    private Long balanceAfter;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        CAPTURED,
        SUCCESS,
        ERROR
    }

    @Lob
    private String statusReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private GamePlay gamePlay;
}
