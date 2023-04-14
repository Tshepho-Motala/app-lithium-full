import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.data.entities.BonusRoundTrack;
import lithium.service.casino.data.repositories.BonusRoundTrackRepository;
import lithium.service.casino.data.repositories.GameRepository;
import lithium.service.casino.service.BonusRoundTrackService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class TestingMockito {

	@Mock
	private BonusRoundTrackRepository bonusRoundTrackRepositoryMock;
	@Mock
	private GameRepository gameRepositoryMock;
	
	@InjectMocks
	BonusRoundTrackService brts;
	
	@Test
	public void saveBonusRound() {
		BetRequest betRequest = new BetRequest("luckybetz/christest", 0L, 0L, "round1", "mygame", false,
				"sessionid1", 0L, "transactionid1", false, 1, "USD", null, CasinoTranType.CASINO_BET, null, null, null, null, null, null, null, null, null, null);
		BonusRoundTrack brt = brts.findBonusRound(betRequest);
		assertNull(brt);
	}
}
