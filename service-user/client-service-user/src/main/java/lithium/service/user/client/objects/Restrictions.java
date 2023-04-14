package lithium.service.user.client.objects;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
//Using lithium.service.limit.client.objects.Access as restrictions object here causes circular-dependency hence this is a copy of 'Access object'.
public class Restrictions {
    @Builder.Default
    private boolean casinoAllowed = true;
    @Builder.Default
    private boolean casinoSystemPlaced = false;
    private String casinoErrorMessage;
    @Builder.Default
    private boolean loginAllowed = true;
    private String loginErrorMessage;
    @Builder.Default
    private boolean depositAllowed = true;
    private String depositErrorMessage;
    @Builder.Default
    private boolean withdrawAllowed = true;
    private String withdrawErrorMessage;
    @Builder.Default
    private boolean betPlacementAllowed = true;
    private String betPlacementErrorMessage;
    @Builder.Default
    private boolean compsAllowed = true;
    @Builder.Default
    private boolean compsSystemPlaced = false;
    private String compsErrorMessage;
    @Builder.Default
    private boolean f2pAllowed = true;
    private String f2pErrorMessage;
}
