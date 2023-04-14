package lithium.service.casino.provider.roxor.data.response.freegames;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties({ "gamesAvailability"})
public class Summary {

    private PlayerDetails playerDetails;

    private GamesAvailability gamesAvailability;

}