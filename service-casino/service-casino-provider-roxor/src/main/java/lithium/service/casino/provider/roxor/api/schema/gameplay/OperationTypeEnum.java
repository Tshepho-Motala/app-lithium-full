package lithium.service.casino.provider.roxor.api.schema.gameplay;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OperationTypeEnum {
    START_GAME_PLAY,
    TRANSFER,
    CANCEL_TRANSFER,
    FINISH_GAME_PLAY,
    ACCRUAL,
    CANCEL_ACCRUAL,
    FREE_PLAY,
    CANCEL_FREE_PLAY;
}
