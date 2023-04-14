package lithium.service.reward.provider.casino.blueprint.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.service.GamesInternalClientService;
import lithium.service.reward.provider.casino.blueprint.utils.TestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static lithium.service.reward.provider.casino.blueprint.utils.FixtureUtil.fixture;

@ExtendWith(MockitoExtension.class)
public class ProviderGamesServiceTest {

    private ProviderGamesService providerGamesService;

    @Mock
    private GamesInternalClientService gamesInternalClientService;

    private ObjectMapper mapper;


    @Test
    public void must_filter_out_disabled_games() throws JsonProcessingException {
        List<Game> games = mapper.readValue(fixture(TestConstants.FIXTURE_ENABLED_AND_DISABLED_BLUEPRINT_GAMES), new TypeReference<List<Game>>() {});
        List<lithium.service.reward.client.dto.Game> filtered = providerGamesService.transformAndFilterGames(games);
        Assertions.assertTrue(games.size() > filtered.size());
    }

    @Test
    public void must_map_to_reward_game_correctly() throws JsonProcessingException {
        Game game = mapper.readValue(fixture(TestConstants.FIXTURE_SINGLE_BLUEPRINT_GAME), Game.class);
        lithium.service.reward.client.dto.Game localGame = providerGamesService.mapToGame(game);

        Assertions.assertEquals(game.getName(), localGame.getName());
        Assertions.assertEquals(game.getDescription(), localGame.getDescription());
        Assertions.assertEquals(game.getCommercialName(), localGame.getCommercialName());
        Assertions.assertEquals(game.getGuid(), localGame.getGuid());
        Assertions.assertNotNull(localGame.getProviderGameId());
        Assertions.assertTrue(localGame.getProviderGameId().startsWith("BP_"));
    }


    @Test
    public void must_get_return_a_list_of_games() throws Exception {
        String domainName = "livescore_uk";
        List<Game> games = mapper.readValue(fixture(TestConstants.FIXTURE_ENABLED_AND_DISABLED_BLUEPRINT_GAMES), new TypeReference<List<Game>>() {});
        Mockito.when(gamesInternalClientService.getGamesForDomainAndProvider(Mockito.anyString(), Mockito.anyString())).thenReturn(games);
        List<lithium.service.reward.client.dto.Game> providerGames =  providerGamesService.getProviderGames(domainName);
        Assertions.assertTrue(games.size() >= providerGames.size());
    }

    @BeforeEach
    public void setup() {
        mapper = new ObjectMapper();
        providerGamesService = new ProviderGamesService(gamesInternalClientService);
    }
}
