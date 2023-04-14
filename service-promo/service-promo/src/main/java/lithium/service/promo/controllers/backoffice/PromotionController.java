package lithium.service.promo.controllers.backoffice;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.util.List;
import java.util.Objects;

import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.UserCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.promo.data.entities.Challenge;
import lithium.service.promo.data.entities.PromotionRevision;
import lithium.service.promo.data.entities.Rule;
import lithium.service.promo.services.PromotionService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

@RestController
@RequestMapping("/backoffice/promotion/{id}")
@Slf4j
public class PromotionController {
	@Autowired
    private PromotionService service;
	
	@GetMapping
	public Response<Promotion> get(@PathVariable("id") Promotion promotion) {
		return Response.<Promotion>builder().data(promotion).status(OK).build();
	}
	
	@GetMapping("/revision/{promotionRevisionId}")
	public Response<PromotionRevision> findRevision(@PathVariable("id") Promotion promotion, @PathVariable("promotionRevisionId") PromotionRevision promotionRevision) {
		if (!Objects.equals(promotion.getId(), promotionRevision.getPromotion().getId())) {
			String msg = "Revision does not match promotion";
			log.error(msg);
			return Response.<PromotionRevision>builder().data(null).status(INTERNAL_SERVER_ERROR).message(msg).build();
		} else {
			return Response.<PromotionRevision>builder().data(promotionRevision).status(OK).build();
		}
	}
	
	@GetMapping("/revisions")
	public DataTableResponse<PromotionRevision> missionRevisionsTable(@PathVariable("id") Promotion promotion, DataTableRequest request) {
		Page<PromotionRevision> table = service.findRevisionsByPromotion(promotion, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@GetMapping("/modify")
	public Response<Promotion> modify(@PathVariable("id") Promotion promotion, LithiumTokenUtil tokenUtil) {
		try {
			promotion = service.modify(promotion, tokenUtil.guid());
			return Response.<Promotion>builder().data(promotion).status(OK).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Promotion>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping("/modify")
	public Response<Promotion> modify(
		@PathVariable("id") Promotion promotion,
		@RequestBody lithium.service.promo.client.objects.Promotion promotionPost
	) {
		try {
			promotion = service.modify(promotion, promotionPost);
			return Response.<Promotion>builder().data(promotion).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Promotion>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping("/modifyAndSaveCurrent")
	public Response<Promotion> modifyAndSaveCurrent(
		@PathVariable("id") Promotion promotion,
		@RequestBody lithium.service.promo.client.objects.Promotion promotionPost
	) {
		try {
			promotion = service.modifyAndSaveCurrent(promotion, promotionPost);
			return Response.<Promotion>builder().data(promotion).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Promotion>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}

//	@PostMapping("/add-grouped-challenges")
//	public Response<Promotion> addChallenge(
//			@PathVariable("id") Promotion promotion,
//			@RequestBody lithium.service.promo.client.objects.ChallengeGroup challengeGroup
//	) {
//		try {
//			promotion = service.addGroupedChallenges(promotion, challengeGroup);
//			return Response.<Promotion>builder().data(promotion).status(OK).build();
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return Response.<Promotion>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
//		}
//	}
	
	@PostMapping("/addChallenge")
	public Response<Promotion> addChallenge(
		@PathVariable("id") Promotion promotion,
		@Valid @RequestBody lithium.service.promo.client.objects.Challenge challengePost
	) {
		try {
			promotion = service.addChallenge(promotion, challengePost);
			return Response.<Promotion>builder().data(promotion).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Promotion>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@DeleteMapping("/removeChallenge/{challengeId}")
	public Response<Promotion> removeChallenge(
		@PathVariable("id") Promotion promotion,
		@PathVariable("challengeId") Challenge challenge
	) {
		try {
			promotion = service.removeChallenge(promotion, challenge);
			return Response.<Promotion>builder().data(promotion).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Promotion>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping("/modifyChallenge/{challengeId}")
	public Response<Promotion> modifyChallenge(
		@PathVariable("id") Promotion promotion,
		@PathVariable("challengeId") Challenge challenge,
		@Valid @RequestBody lithium.service.promo.client.objects.Challenge challengePost
	) {
		try {
			promotion = service.modifyChallenge(promotion, challenge, challengePost);
			return Response.<Promotion>builder().data(promotion).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Promotion>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping("/challenge/{challengeId}/getIcon")
	public ResponseEntity<byte[]> getChallengeIcon(@PathVariable("challengeId") Long challengeId) {
		return service.getChallengeIconAsResponseEntity(challengeId);
	}
	
	@PostMapping("/challenge/{challengeId}/addChallengeRule")
	public Response<Promotion> addChallengeRule(
		@PathVariable("id") Promotion promotion,
		@PathVariable("challengeId") Challenge challenge,
		@RequestBody lithium.service.promo.client.objects.Rule challengeRulePost
	) {
		try {
			promotion = service.addChallengeRule(promotion, challenge, challengeRulePost);
			return Response.<Promotion>builder().data(promotion).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Promotion>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@DeleteMapping("/challenge/{challengeId}/removeChallengeRule/{challengeRuleId}")
	public Response<Promotion> removeChallengeRule(
		@PathVariable("id") Promotion promotion,
		@PathVariable("challengeId") Challenge challenge,
		@PathVariable("challengeRuleId") Rule rule
	) {
		try {
			promotion = service.removeChallengeRule(promotion, challenge, rule);
			return Response.<Promotion>builder().data(promotion).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Promotion>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping("/challenge/{challengeId}/modifyChallengeRule/{challengeRuleId}")
	public Response<Promotion> modifyChallengeRule(
		@PathVariable("id") Promotion promotion,
		@PathVariable("challengeId") Challenge challenge,
		@PathVariable("challengeRuleId") Rule rule,
		@RequestBody lithium.service.promo.client.objects.Rule challengeRulePost
	) {
		try {
			promotion = service.modifyChallengeRule(promotion, challenge, rule, challengeRulePost);
			return Response.<Promotion>builder().data(promotion).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Promotion>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}

	@PostMapping("/user-categories/add")
	public Response<Promotion> addUserCategories(
			@PathVariable("id") Promotion promotion,
			@RequestBody List<lithium.service.promo.client.objects.UserCategory> userCategories,
			LithiumTokenUtil tokenUtil
	) {
		try {
			promotion = service.addUserCategories(promotion, userCategories);

			return Response.<Promotion>builder().data(promotion).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Promotion>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}

	@DeleteMapping("/user-categories/{userCategory}")
	public Response<UserCategory> addUserCategories(
			@PathVariable("id") Promotion promotion,
			@PathVariable("userCategory") UserCategory userCategory,
			LithiumTokenUtil tokenUtil
	) {
		try {
			service.deleteUserCategory(userCategory);

			return Response.<UserCategory>builder().data(userCategory).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<UserCategory>builder().data(userCategory).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
}
