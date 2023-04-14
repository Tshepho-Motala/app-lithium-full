package lithium.service.casino.provider.roxor.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameplayOperationEventRequest {

    private Metadata metadata;
    @NonNull
    private Payload payload;

}
