package lithium.service.casino.system.controllers;

import lithium.service.casino.client.BonusTokenClient;
import lithium.service.casino.client.objects.PlayerBonusToken;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.service.BonusTokenService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class BonusTokenController implements BonusTokenClient {

	@Autowired @Setter
	private BonusTokenService bonusTokenService;

	@RequestMapping("/system/casino/bonus-token/findBonusTokensByPlayer")
	@Override
	@ResponseBody
	public List<PlayerBonusToken> findBonusTokensByPlayer(@RequestParam("playerGuid") String playerGuid) {
		return bonusTokenService.handlePlayerTokenLookup(playerGuid);
	}

	@RequestMapping("/system/casino/bonus-token/validate")
	@Override
	@ResponseBody
	public PlayerBonusToken validateBonusToken(
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam("bonusToken") Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException {
		return bonusTokenService.handleBonusTokenValidation(playerGuid, bonusTokenId);
	}

	@RequestMapping("/system/casino/bonus-token/reserve")
	@Override
	@ResponseBody
	public PlayerBonusToken reserveBonusToken(
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam("bonusToken") Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException {
		return bonusTokenService.reserveBonusToken(playerGuid, bonusTokenId);
	}

	@RequestMapping("/system/casino/bonus-token/unreserve")
	@Override
	@ResponseBody
	public PlayerBonusToken unreserveBonusToken(
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam("bonusToken") Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException {
		return bonusTokenService.unreserveBonusToken(playerGuid, bonusTokenId);
	}

	@RequestMapping("/system/casino/bonus-token/redeem")
	@Override
	@ResponseBody
	public PlayerBonusToken redeemBonusToken(
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam("bonusToken") Long bonusTokenId
	) throws
			Status423InvalidBonusTokenException,
			Status424InvalidBonusTokenStateException {
		return bonusTokenService.redeemBonusToken(playerGuid, bonusTokenId);
	}

}
