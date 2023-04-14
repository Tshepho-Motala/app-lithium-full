package lithium.service.games.client.objects;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecommendedGameBasic extends GameResponseBasic{

    private Integer gameRank;

    @Builder
    public RecommendedGameBasic(String gameId, String gameName, String commercialGameName, String image, String supplierName, Boolean freeGame, String gameStudioName, Integer gameRank) {
        super(gameId, gameName, commercialGameName, image, supplierName, freeGame, gameStudioName);
        this.gameRank = gameRank;
    }
}
