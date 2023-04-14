package lithium.service.casino.provider.roxor.data.response.evolution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {

    private String id;

    private String type;

    private String casinoId;

    private Long players;

    private Map<String, GameTable> tables = new HashMap<>();

}
