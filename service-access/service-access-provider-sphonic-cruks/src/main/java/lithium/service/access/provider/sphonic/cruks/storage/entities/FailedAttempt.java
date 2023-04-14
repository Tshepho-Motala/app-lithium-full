package lithium.service.access.provider.sphonic.cruks.storage.entities;

import lithium.service.access.provider.sphonic.data.entities.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    indexes = {
        @Index(name = "idx_last_attempted_at", columnList = "lastAttemptedAt", unique = false),
        @Index(name = "idx_user", columnList = "user_id", unique = true)
    }
)
public class FailedAttempt implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private Long totalAttempts;

    @Column()
    private Date lastAttemptedAt;

    @Column(nullable = false)
    private String lastFailureMessage;

    @Column()
    private Date firstFailedAttempt;

    @Column(nullable = false)
    private String firstFailedMessage;
}

