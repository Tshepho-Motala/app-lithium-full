package lithium.service.casino.provider.roxor.storage.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.jpa.entity.EntityWithUniqueGuid;
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

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Table(indexes = {
        @Index(name = "idx_user_guid", columnList = "guid", unique = true),
        @Index(name = "idx_api_token", columnList = "apiToken" )
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements EntityWithUniqueGuid {

    @Version
    int version;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String guid;

    @Column(nullable=false, unique=true)
    private String apiToken;

    //TODO VERIFY IF NEEDED - Domain already linked on GAMEPLAY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Domain domain;

}
