package lithium.service.notifications.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
        @Index(name="idx_inbox_user_id", columnList="user_id", unique=true)
})
public class InboxUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 6371029477981186909L;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Integer ctaCount;

    @Column(nullable = false)
    private Integer readCount;

    @Column(nullable = false)
    private Integer unreadCount;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(nullable=false)
    private User user;

    @Version
    private int version;
}
