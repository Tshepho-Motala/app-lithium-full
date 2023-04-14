package lithium.service.limit.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum CasinoBlockSubordinateType {
    SUBORDINATE_TYPE_1(1, 1),
    SUBORDINATE_TYPE_2(2, 7),
    SUBORDINATE_TYPE_3(3, 14),
    SUBORDINATE_TYPE_4(4, 21),
    SUBORDINATE_TYPE_5(5, 35);

    private Integer subType;

    private Integer numberOfDays;

    @JsonCreator
    public static Integer getDays(Integer subType) {
        for (CasinoBlockSubordinateType s: CasinoBlockSubordinateType.values()) {
            if (s.subType == subType) {
                return s.numberOfDays;
            }
        }
        return null;
    }
}
