package lithium.service.promo.controllers.backoffice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.promo.client.objects.Promotion;
import lithium.service.promo.exceptions.PromotionNotFoundException;
import lithium.service.promo.services.PromotionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping( "/backoffice/promotion" )
public class BackofficePatchPromotionController {

  @Autowired
  PromotionService promotionService;
  @Autowired
  ObjectMapper objectMapper;

  @PatchMapping( path = "/{id}", consumes = "application/json-patch+json" )
  public ResponseEntity<Promotion> patchPromotion(@PathVariable Long id, @RequestBody String patch) {
    try {
      Promotion promotionPatched = promotionService.applyPatchToPromotion(patch, id);
      return ResponseEntity.ok(promotionPatched);
    } catch (JsonProcessingException e) {
      log.error("Json patch error..", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (PromotionNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
