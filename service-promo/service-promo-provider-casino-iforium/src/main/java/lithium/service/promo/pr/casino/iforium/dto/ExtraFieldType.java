package lithium.service.promo.pr.casino.iforium.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;

@ToString
@AllArgsConstructor
public enum ExtraFieldType {

    GAME_PROVIDER("game_provider"),
    GAME("game"),
    GAME_TYPE("game_type");

    @Getter
    @Setter
    private String type;

    public static ExtraFieldType fromType(String type) {
        return Arrays.stream(ExtraFieldType.values())
                .filter(f -> f.type.equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }
}
