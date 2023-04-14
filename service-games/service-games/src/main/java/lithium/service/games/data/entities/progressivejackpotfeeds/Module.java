package lithium.service.games.data.entities.progressivejackpotfeeds;

import lithium.jpa.entity.EntityWithUniqueName;
import lithium.service.games.data.entities.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "module",
        indexes = {
                @Index(name = "idx_module_name", columnList = "name", unique = true)
        }
)
public class Module implements Serializable, EntityWithUniqueName {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    @Column(nullable=false)
    private String name;

}
