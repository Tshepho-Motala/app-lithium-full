package lithium.service.casino.provider.incentive.storage.entities;

import lithium.jpa.entity.EntityWithUniqueCode;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_sport_code", columnList = "code", unique = true),
        @Index(name = "idx_sport_name", columnList = "name", unique = true),
})
public class Sport implements EntityWithUniqueCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    int version;

    @Column(nullable=false)
    private String code;

    @Column(nullable=false)
    private String name;

    @PrePersist
    private void prePersist() {
        code = code.toUpperCase();
    }
}

