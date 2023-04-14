package lithium.service.promo.pr.casino.iforium.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public enum GameProvider {
    BLUEPRINT("Blueprint", "43");

    private String providerName;
    private String providerId;
}
