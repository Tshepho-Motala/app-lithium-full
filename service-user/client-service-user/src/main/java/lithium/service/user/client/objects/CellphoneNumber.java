package lithium.service.user.client.objects;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CellphoneNumber {
    private boolean cellphoneValidated;
    private String comment;
}
