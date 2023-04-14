package lithium.service.games.client.objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.Date;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Game implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private long id;
	
	private String name;

	private String commercialName;
	
	private Domain domain;
	
	private String providerGameId;

	private String supplierGameRewardGuid;

	private String supplierGameGuid;
	
	private boolean enabled;

	private boolean excludeRecentlyPlayed;

	private boolean visible;
	
	private boolean locked;
	
	private String lockedMessage;
	
	private boolean hasLockImage;
	
	private String guid; //Consists of providerServiceName and providerGameId
	
	private String description;

	private BigDecimal rtp;

	private Date introductionDate;

	private java.util.Date activeDate;

	private java.util.Date inactiveDate;

	private String providerGuid; //Essentially the eureka service name

	private Boolean freeSpinEnabled;

	private Boolean freeSpinValueRequired;

	private Boolean freeSpinPlayThroughEnabled;

	private Boolean casinoChipEnabled;

	private Boolean instantRewardEnabled;

	private Boolean instantRewardFreespinEnabled;

	private GameCurrency gameCurrency;

	private GameSupplier gameSupplier;

	private GameType primaryGameType;

	private GameType secondaryGameType;

	private HashMap<String, Label> labels;

	private Boolean progressiveJackpot; // denotes that a game has a progressive jackpot

	private Boolean networkedJackpotPool; // denotes that a game is part of a networked jackpot pool

	private Boolean localJackpotPool; // denotes that a game is part of a local jackpot pool

	private Boolean freeGame;

	private Boolean liveCasino;

	private String cdnImageUrl;

	private List<String> channels;

	private GameStudio gameStudio;

	private String moduleSupplierId;
}
