package lithium.service.casino.provider.roxor.data.response.freegames;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GamesAvailabilityDto {

    private List<GamesAvailability> gamesAvailability;
}
