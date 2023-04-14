package lithium.service.casino.provider.roxor.api.schema.gameplay;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TypeEnum {
    DEBIT,
    CREDIT,
    JACKPOT_CREDIT
}
