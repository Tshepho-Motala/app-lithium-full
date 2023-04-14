package lithium.service.casino.provider.evolution.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    private String id;

    private String type;

    private Details details;

}
