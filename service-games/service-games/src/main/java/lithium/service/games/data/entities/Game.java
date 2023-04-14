package lithium.service.games.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import lithium.service.games.client.objects.Label;
import lithium.service.games.data.entities.supplier.SupplierGameMetaData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Data
@ToString(exclude = "gameChannels")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "game",
    indexes = {
        @Index(name = "idx_gm_guid_domain", columnList = "guid, domain_id", unique = true),
        @Index(name = "idx_supplier_game_guid", columnList = "supplier_game_guid")
    }
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Game implements Serializable {

	private static final long serialVersionUID = 1L;

	@Version
	private int version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;

    @Column(nullable = false)
    private String commercialName;

	@ManyToOne
  @JoinColumn(nullable = false)
  private Domain domain;

	private String providerGameId;

    private String supplierGameRewardGuid;

	private boolean enabled;

	private boolean visible;

	private boolean locked;

  @Column(name = "exclude_recently_played", columnDefinition="bit default 0")
  private boolean excludeRecentlyPlayed;

	@Column(nullable=true, length=1000000)
	private String lockedMessage;

	private String guid; //Consists of providerServiceName and providerGameId

	private String description;

	private BigDecimal rtp;

	private Date activeDate;

  private Date inactiveDate;

	private String providerGuid;

  @Column(name = "supplier_game_guid")
  private String supplierGameGuid;

  private Date introductionDate;

	private Boolean freeSpinEnabled;

	private Boolean casinoChipEnabled;

  private Boolean instantRewardEnabled;

  private Boolean instantRewardFreespinEnabled;

	private Boolean freeSpinValueRequired;

	private Boolean freeSpinPlayThroughEnabled;

  private Boolean progressiveJackpot; // denotes that a game has a progressive jackpot

  private Boolean networkedJackpotPool; // denotes that a game is part of a networked jackpot pool

  private Boolean localJackpotPool; // denotes that a game is part of a local jackpot pool

  private Boolean freeGame;
  @Column(nullable = false)
  private Boolean liveCasino;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	@JsonManagedReference("game")
	private GameCurrency gameCurrency;

	@ManyToOne
  @JoinColumn
  private GameSupplier gameSupplier;

  @ManyToOne
  @JoinColumn(name = "game_type_id")
  private GameType primaryGameType;

  @ManyToOne
  @JoinColumn
  private GameType secondaryGameType;

  @OneToMany(mappedBy = "game")
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<GameChannel> gameChannels = new ArrayList<>();

  @Transient
	private HashMap<String, Label> labels;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "supplier_game_meta_data_id")
  @JsonManagedReference
  private SupplierGameMetaData supplierGameMetaData;

  @ManyToOne
  @JoinColumn
  private GameStudio gameStudio;

  @Column(name = "module_supplier_id")
  private String moduleSupplierId;
}
