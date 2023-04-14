package lithium.service.user.client.objects;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
/**
 * {@code IncompleteUserBasic} This class represents an incomplete user object
 * <br>
 * Each request will be based on the stage of execution
 * <br>
 */
public class IncompleteUserBasic {

    private int stage;
    public Map<String, String> additionalData;

    public int getStage() {
        return stage;
    }

    private void setStage(int stage) {
        this.stage = stage;
    }
}
