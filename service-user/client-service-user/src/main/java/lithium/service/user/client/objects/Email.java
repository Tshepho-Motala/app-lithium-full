package lithium.service.user.client.objects;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Email {
    private boolean emailValidated;
    private String comment;
}
