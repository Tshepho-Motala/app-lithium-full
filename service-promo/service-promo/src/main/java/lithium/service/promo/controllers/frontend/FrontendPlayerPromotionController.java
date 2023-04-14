package lithium.service.promo.controllers.frontend;

import lithium.service.promo.client.objects.frontend.UserPromotionFE;
import lithium.service.promo.mappers.UserPromotionFEMapper;
import lithium.service.promo.services.UserPromotionService;
import lithium.tokens.LithiumTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/frontend/player")
public class FrontendPlayerPromotionController {

    private final UserPromotionService userPromotionService;
    private final UserPromotionFEMapper userPromotionFEMapper = new UserPromotionFEMapper();

    @PostMapping("/find/current/v1")
    public List<UserPromotionFE> getCurrent( LithiumTokenUtil util) {
        return userPromotionService.findCurrent(util.guid()).stream()
                .map(userPromotionFEMapper::mapToUserPromotionFE)
                .toList();
    }


    @PostMapping("/find/completed/v1")
    public List<UserPromotionFE> getCompleted(LithiumTokenUtil util) {
        return userPromotionService.findCompleted(util.guid()).stream()
                .map(userPromotionFEMapper::mapToUserPromotionFE)
                .toList();
    }
}
