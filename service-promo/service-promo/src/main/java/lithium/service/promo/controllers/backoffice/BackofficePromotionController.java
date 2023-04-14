package lithium.service.promo.controllers.backoffice;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;

import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.promo.client.objects.PromotionBO;
import lithium.service.promo.controllers.ValidationController;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.Reward;
import lithium.service.promo.objects.PromoQuery;
import lithium.service.promo.services.PromotionService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping( "/backoffice/promotion/v1" )
public class BackofficePromotionController extends ValidationController {

  @Autowired
  PromotionService promotionService;

  @GetMapping("{promotionId}")
  public Response<PromotionBO> findPromotion(
      @PathVariable("promotionId") Long promotionId,
      LithiumTokenUtil tokenUtil
  ) {
    PromotionBO promotion = null;
    try {
      promotion = promotionService.findOneAndConvert(promotionId);
      if (promotion != null) {
        return Response.<PromotionBO>builder().data(promotion).status(OK).build();
      } else {
        return Response.<PromotionBO>builder().status(NOT_FOUND).build();
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Response.<PromotionBO>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @PostMapping("/create-draft")
  public Response<PromotionBO> create(
      @Valid @RequestBody PromotionBO request,
      LithiumTokenUtil tokenUtil
  ) {
    PromotionBO promotion = null;
    try {
      promotion = promotionService.createV1(request, tokenUtil);
      return Response.<PromotionBO>builder().data(promotion).status(OK).build();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Response.<PromotionBO>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @PostMapping("/edit-draft")
  public Response<PromotionBO> edit(
      @RequestBody PromotionBO request,
      LithiumTokenUtil tokenUtil
  ) {
    PromotionBO promotion = null;
    try {
      promotion = promotionService.editV1(request, tokenUtil);
      return Response.<PromotionBO>builder().data(promotion).status(OK).build();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Response.<PromotionBO>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @PostMapping("/mark-draft-final/{promotionId}")
  public Response<PromotionBO> markAsCurrent(
      @PathVariable("promotionId") Long promotionId,
      LithiumTokenUtil tokenUtil
  ) {
    PromotionBO promotion = null;
    try {
      promotion = promotionService.markAsCurrentV1(promotionId, tokenUtil);
      return Response.<PromotionBO>builder().data(promotion).status(OK).build();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Response.<PromotionBO>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @PostMapping("/toggle-enabled/{id}")
  public Response<Boolean> toggleEnabled(@PathVariable("id") Promotion promotion, @RequestParam boolean enabled, LithiumTokenUtil lithiumTokenUtil) {
    return Response.<Boolean>builder().data(promotionService.toggleEnabled(promotion, enabled, lithiumTokenUtil))
            .status(OK)
            .build();
  }

  @GetMapping("/{id}/changelogs")
  public Response<ChangeLogs> changelogs(@PathVariable("id") Promotion promotion, @RequestParam(required = false, defaultValue = "0") Integer p) throws Exception {
    return promotionService.changelogs(promotion, new String[] {"promotion"},  p);
  }

  @PostMapping("/get-disabled-promotions-between-dates")
  public List<PromotionBO> getDisablePromotionsBetweenDates(@RequestBody PromoQuery query) {
    return promotionService.getDisabledPromotions(PromoQuery.builder()
            .domains(query.domains())
            .startDate(query.startDate())
            .endDate(query.endDate())
            .build());
  }

  @DeleteMapping("/delete/{id}")
  public Response<Boolean> delete(@PathVariable("id") Long promotionId, LithiumTokenUtil lithiumTokenUtil) {
    return Response.<Boolean>builder().data(promotionService.delete(promotionId, lithiumTokenUtil))
            .status(OK)
            .build();
  }
}
