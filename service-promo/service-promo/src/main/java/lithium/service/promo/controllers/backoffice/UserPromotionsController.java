package lithium.service.promo.controllers.backoffice;

import static lithium.service.Response.Status.OK;

import java.util.ArrayList;
import java.util.List;

import lithium.service.promo.data.entities.PromotionRevision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.promo.data.entities.UserPromotion;
import lithium.service.promo.services.UserPromotionService;

@RestController
@RequestMapping("/backoffice/user-promotions")
public class UserPromotionsController {
	@Autowired
    UserPromotionService service;
	
	@GetMapping("/table")
	public DataTableResponse<UserPromotion> activeUserMissionsTable(
		@RequestParam("domains") List<String> domains,
		@RequestParam(name="userGuid", required=false) String userGuid,
		@RequestParam(name="active", required=false) Boolean active,
		@RequestParam(name = "current", required = false) Boolean current,
		@RequestParam(name="startedDateRangeStart", required=false) String startedDateRangeStart,
		@RequestParam(name="startedDateRangeEnd", required=false) String startedDateRangeEnd,
		DataTableRequest request
	) {
		Page<UserPromotion> table = service.findUserPromotionsByDomains(
			domains,
			userGuid,
			active,
			current,
			startedDateRangeStart,
			startedDateRangeEnd,
			null,
			request.getSearchValue(),
			request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@GetMapping("/{promotionRevisionId}/table")
	public DataTableResponse<UserPromotion> revisionMissionsTable(@PathVariable("promotionRevisionId") PromotionRevision promotionRevision, DataTableRequest request) {
		List<String> domains = new ArrayList<>();
		domains.add(promotionRevision.getDomain().getName());
		Page<UserPromotion> table = service.findUserPromotionsByDomains(
			domains,
			null,
			null,
			false,
			null,
			null,
				promotionRevision,
			request.getSearchValue(),
			request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@GetMapping("/{id}")
	public Response<UserPromotion> findUserMissionById(@PathVariable("id") UserPromotion userPromotion) {
		return Response.<UserPromotion>builder().data(userPromotion).status(OK).build();
	}
}
