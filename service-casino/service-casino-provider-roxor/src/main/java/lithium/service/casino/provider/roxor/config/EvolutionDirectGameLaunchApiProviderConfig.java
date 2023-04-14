package lithium.service.casino.provider.roxor.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvolutionDirectGameLaunchApiProviderConfig {

    private String url;

    private String casinoId;

    private String username;

    private String password;

}
