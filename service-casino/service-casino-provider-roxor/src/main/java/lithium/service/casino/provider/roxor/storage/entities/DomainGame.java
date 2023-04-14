/**
 * There used to be a game table in local database which was used in the purpose of optimisation mentioned in the other comment here.
 * However on Roxor side, the same game can be configured on multiple websites.
 * Before service-casino-provider-roxor was tracking the already configured games in game table and when it was receiving a transaction for another domain of an already existing game,
 * it wasn't calling downstream service to create that game for the new website.
 * That's the root cause of RTECH-13848 which aims to enable the same game being added to different domains, therefore to address this we had to create a new table domain-game instead of old game, to allow the downstream call for a game to be configured on multiple websites
 *
 * */


package lithium.service.casino.provider.roxor.storage.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.io.Serializable;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Table(indexes = {
        @Index(name = "idx_domain_game", columnList = "domainName,gameKey", unique = true)
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DomainGame implements Serializable {
    private static final long serialVersionUID = -7983173285745543209L;

    @Version
    int version;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String domainName;

    @Column(nullable = false)
    private String gameKey;

}
