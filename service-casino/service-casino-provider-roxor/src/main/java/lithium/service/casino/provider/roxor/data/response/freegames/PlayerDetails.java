package lithium.service.casino.provider.roxor.data.response.freegames;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({ "summary"})
public class PlayerDetails {

    private List<Wins> wins;

    private String currentDay;

    private List<String> daysPlayed;

    private String dfgPicksRemaining;

    private MfgDetails mfgDetails;

    private Summary  summary;

}