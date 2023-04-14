package lithium.service.promo.client.objects;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor
@Getter
public enum UserCategoryType implements Serializable {
    TYPE_WHITELIST("whitelist"),
    TYPE_BLACKLIST("blacklist");

    private String type;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static UserCategoryType fromType(String type) {
        for (UserCategoryType g : UserCategoryType.values()) {
            if (g.type.equalsIgnoreCase(type)) {
                return g;
            }
        }
        return null;
    }
}
