package lithium.service.casino.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PlayerBonusToken implements Serializable {
	private Long id;

	private Date createdDate;

	private Date expiryDate;

	private String currency;

	private Double amountDecimal; //This is either default amount or the custom token amount assigned for the player

	private Double minimumOdds;

	private Long bonusRevisionId;

	private Long playerBonusHistoryId;

	private Integer status; //TODO: This can be changed to an enum for easy identification across services
}
