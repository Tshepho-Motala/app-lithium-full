package lithium.service.promo.controllers.external;

import java.util.List;
import lithium.exceptions.Status470HashInvalidException;
import lithium.service.promo.dtos.ExclusiveAddRequest;
import lithium.service.promo.dtos.ExclusiveAddResponse;
import lithium.service.promo.dtos.PromotionExt;
import lithium.service.promo.services.ExternalPromoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/external" )
@RequiredArgsConstructor
public class ExternalPromotionController {

  private final ExternalPromoService externalPromoService;

  @GetMapping( "/find-promotions" )
  public List<PromotionExt> findPromotions(@RequestParam( "domainName" ) String domainName, @RequestParam( "sha256" ) String sha)
  throws Status470HashInvalidException
  {
    externalPromoService.validateSha(domainName, sha);
    return externalPromoService.getPromotionsForDomain(domainName);
  }

  @PostMapping( "/add-players" )
  public ExclusiveAddResponse addExclusivePlayers(@RequestBody ExclusiveAddRequest exclusiveAddRequest)
  throws Status470HashInvalidException
  {
    externalPromoService.validateSha(exclusiveAddRequest.getPromotionId().toString(), exclusiveAddRequest.getSha256());
    return externalPromoService.addPlayersToPromotion(exclusiveAddRequest.getPromotionId(), exclusiveAddRequest.getPlayers());
  }

}
