package lithium.service.casino.provider.roxor.data.response.evolution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Road {

    private Location location;

    private String color;

    private String score;

    private String ties;

    private Boolean playerPair;

    private Boolean bankerPair;

    private Boolean natural;

}
