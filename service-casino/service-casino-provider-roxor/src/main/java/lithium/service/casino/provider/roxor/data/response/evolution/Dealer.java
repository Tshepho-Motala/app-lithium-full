package lithium.service.casino.provider.roxor.data.response.evolution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dealer {

    private String dealerId;

    private String name;
}
