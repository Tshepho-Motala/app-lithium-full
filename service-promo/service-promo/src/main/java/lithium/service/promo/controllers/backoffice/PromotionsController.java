package lithium.service.promo.controllers.backoffice;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import lithium.service.promo.client.enums.Operation;
import lithium.service.promo.client.objects.PromotionBO;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.objects.PromoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.promo.services.PromotionService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/backoffice/promotions")
@Slf4j
public class PromotionsController {
	@Autowired
    PromotionService promotionService;

	@GetMapping("/get-promotions-with-events-in-period")
	public List<PromotionBO> getPromotionWithEventForPeriod(
			@RequestParam String[] domains,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
		return promotionService.getPromotionsWithEventsWithPeriod(PromoQuery.builder()
						.domains(Stream.of(domains).toList())
						.startDate(startDate)
						.endDate(endDate)
				.build());
	}

	@GetMapping("/get-disabled-promotions-between-dates")
	public List<PromotionBO> getDisablePromotionsBetweenDates(
			@RequestParam String[] domains,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
		return promotionService.getDisabledPromotions(PromoQuery.builder()
				.domains(Stream.of(domains).toList())
				.startDate(startDate)
				.endDate(endDate)
				.build());
	}

	@GetMapping("/table")
	public DataTableResponse<Promotion> missionsTable(@RequestParam("domains") List<String> domains, DataTableRequest request) {
		Page<Promotion> table = promotionService.findByDomains(domains, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}

	
//	@PostMapping("/create")
//	public Response<Promotion> create(@RequestBody lithium.service.promo.client.objects.Promotion request) {
//		Promotion promotion = null;
//		try {
//			promotion = promotionService.create(request);
//			return Response.<Promotion>builder().data(promotion).status(OK).build();
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return Response.<Promotion>builder().data(promotion).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
//		}
//	}


	@GetMapping("/rule/operations")
	public List<Operation> getPromotionRuleOperations() {
		return Arrays.stream(Operation.values()).toList();
	}
}
