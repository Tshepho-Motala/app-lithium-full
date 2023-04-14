package lithium.service.casino.provider.roxor.storage.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.jpa.entity.EntityWithUniqueName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/* Event database to prevent massive duplication in selection table of similar events */

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_domain_name", columnList = "name", unique = true),
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Domain implements EntityWithUniqueName {

    @Version
    int version;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
}

