package lithium.service.access.provider.sphonic.cruks.storage.entities;

import lithium.jpa.entity.EntityWithUniqueGuid;
import lithium.jpa.entity.EntityWithUniqueName;
import lithium.service.access.provider.sphonic.data.entities.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    indexes = {
        @Index(name = "idx_guid", columnList = "guid", unique = true)
    }
)
public class User implements Serializable, EntityWithUniqueGuid {
    private static final long serialVersionUID = -4824788441857279541L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    private String guid;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Domain domain;
}

