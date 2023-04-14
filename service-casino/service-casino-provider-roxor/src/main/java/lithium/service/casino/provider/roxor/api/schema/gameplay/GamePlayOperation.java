package lithium.service.casino.provider.roxor.api.schema.gameplay;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "operationType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AccrualOperation.class, name = "ACCRUAL"),
        @JsonSubTypes.Type(value = AccrualCancelOperation.class, name = "CANCEL_ACCRUAL"),
        @JsonSubTypes.Type(value = LifecycleStartOperation.class, name = "START_GAME_PLAY"),
        @JsonSubTypes.Type(value = LifecycleFinishOperation.class, name = "FINISH_GAME_PLAY"),
        @JsonSubTypes.Type(value = RewardOperation.class, name = "FREE_PLAY"),
        @JsonSubTypes.Type(value = RewardCancelOperation.class, name = "CANCEL_FREE_PLAY"),
        @JsonSubTypes.Type(value = TransferOperation.class, name = "TRANSFER"),
        @JsonSubTypes.Type(value = TransferCancelOperation.class, name = "CANCEL_TRANSFER"),
})
public class GamePlayOperation {
    private OperationTypeEnum operationType;
}
