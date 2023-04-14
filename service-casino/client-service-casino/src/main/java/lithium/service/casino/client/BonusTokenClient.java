package lithium.service.casino.client;

import lithium.service.casino.client.objects.PlayerBonusToken;
import lithium.service.casino.exceptions.Status411InvalidUserGuidException;
import lithium.service.casino.exceptions.Status422InvalidParameterProvidedException;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

/**
 * Allows for the validation, reservation, redemption and lookup of tokens issued in the bonus system for a customer
 */
@FeignClient(name="service-casino")
public interface BonusTokenClient {

	/**
	 * Retrieve a list of active bonus tokens for a player
	 * @param playerGuid
	 */
	@RequestMapping("/system/casino/bonus-token/findBonusTokensByPlayer")
	@ResponseBody
	public List<PlayerBonusToken> findBonusTokensByPlayer(@RequestParam("playerGuid") String playerGuid
	) throws
			Status500UnhandledCasinoClientException;


	/**
	 * Verify the bonus token exists for a specific player
	 * @param playerGuid
	 * @param bonusTokenId
	 * @return
	 * @throws Status423InvalidBonusTokenException
	 */
	@RequestMapping("/system/casino/bonus-token/validate")
	@ResponseBody
	public PlayerBonusToken validateBonusToken(
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam("bonusToken") Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status500UnhandledCasinoClientException;

	/**
	 * Verifies the bonus token is assigned to the player and is in a usable state<br>
	 * If the bonus token is valid, the token is placed in a reserved state.<br>
	 * Once the token is in a reserved state, it will not be visible in the active token list until it is unreserved.
	 * @param playerGuid
	 * @param bonusTokenId
	 * @return
	 * @throws Status422InvalidParameterProvidedException
	 * @throws Status500UnhandledCasinoClientException
	 * @throws Status411InvalidUserGuidException
	 */
	@RequestMapping("/system/casino/bonus-token/reserve")
	@ResponseBody
	public PlayerBonusToken reserveBonusToken(
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam("bonusToken") Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException,
			Status500UnhandledCasinoClientException;

	/**
	 * Returns the bonus token to an active state if it was reserved before
	 * @param playerGuid
	 * @param bonusTokenId
	 * @return
	 * @throws Status422InvalidParameterProvidedException
	 * @throws Status500UnhandledCasinoClientException
	 * @throws Status411InvalidUserGuidException
	 */
	@RequestMapping("/system/casino/bonus-token/unreserve")
	@ResponseBody
	public PlayerBonusToken unreserveBonusToken(
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam("bonusToken") Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException,
			Status500UnhandledCasinoClientException;

	/**
	 * Verifies the bonus token is in an active or reserved state and redeems it.<br>
	 * This is a terminal operation, once the redemption has taken place, no new states will be allowed to be set.
	 * @param playerGuid
	 * @param bonusTokenId
	 * @return
	 * @throws Status422InvalidParameterProvidedException
	 * @throws Status500UnhandledCasinoClientException
	 * @throws Status411InvalidUserGuidException
	 */
	@RequestMapping("/system/casino/bonus-token/redeem")
	@ResponseBody
	public PlayerBonusToken redeemBonusToken(
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam("bonusToken") Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException,
			Status500UnhandledCasinoClientException;
}
