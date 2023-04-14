package lithium.service.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.objects.IncompleteUserFilter;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.IncompleteUser;
import lithium.service.user.data.repositories.DomainRepository;
import lithium.service.user.data.repositories.IncompleteUserRepository;
import lithium.service.user.data.specifications.IncompleteUserSpecifications;
import lithium.service.user.services.DomainService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/{domainName}/incompleteusersforreport/")
@Slf4j
public class IncompleteUsersForReportController {
	@Autowired DomainService domainService;
	@Autowired DomainRepository domainRepository;
	@Autowired IncompleteUserRepository repository;
	@Autowired TokenStore tokenStore;

	private static final String FILTER_OPERATOR_EQUAL_TO = "equalTo";
	private static final String FILTER_OPERATOR_LESS_THAN = "lessThan";
	private static final String FILTER_OPERATOR_GREATER_THAN = "greaterThan";
	private static final String FILTER_OPERATOR_IN = "in";

	private static final String FILTER_FIELD_GENDER = "gender";
	private static final String FILTER_FIELD_STAGE = "stage";
	private static final String FILTER_FIELD_PLAYER_CREATED_DATE = "playerCreatedDate";

	@GetMapping("/table")
	public DataTableResponse<IncompleteUser> table(
		@PathVariable("domainName") String domainName,
		@RequestParam("matchAllFilters") Boolean matchAllFilters,
		@RequestParam("filters") String filters,
		DataTableRequest request
	) throws Exception {
		log.debug("Incomplete users table request " + request.toString());

		Specification<IncompleteUser> spec = null;

		ObjectMapper om = new ObjectMapper();
		IncompleteUserFilter[] filterArr = om.readValue(filters, IncompleteUserFilter[].class);

		if (filterArr != null && filterArr.length > 0) {
			for (IncompleteUserFilter filter: filterArr) {
				switch (filter.getField()) {
					case FILTER_FIELD_GENDER:
						if (spec == null) {
							spec = Specification.where(IncompleteUserSpecifications.gender(filter.getValue()));
						} else {
							if (matchAllFilters) {
								spec = spec.and(IncompleteUserSpecifications.gender(filter.getValue()));
							} else {
								spec = spec.or(IncompleteUserSpecifications.gender(filter.getValue()));
							}
						}
						break;
					case FILTER_FIELD_STAGE:
						boolean multipleStages = false;
						List<String> stages = new ArrayList<>();
						if (filter.getOperator().contentEquals(FILTER_OPERATOR_IN)) {
							multipleStages = true;
							stages = Arrays.asList(filter.getValue().split(","));
						}
						if (spec == null) {
							if (multipleStages) {
								spec = Specification.where(IncompleteUserSpecifications.stageIn(stages));
							} else {
								spec = Specification.where(IncompleteUserSpecifications.stage(filter.getValue()));
							}
						} else {
							if (matchAllFilters) {
								if (multipleStages) {
									spec = spec.and(IncompleteUserSpecifications.stageIn(stages));
								} else {
									spec = spec.and(IncompleteUserSpecifications.stage(filter.getValue()));
								}
							} else {
								if (multipleStages) {
									spec = spec.or(IncompleteUserSpecifications.stageIn(stages));
								} else {
									spec = spec.or(IncompleteUserSpecifications.stage(filter.getValue()));
								}
							}
						}
						break;
					case FILTER_FIELD_PLAYER_CREATED_DATE:
						spec = playerCreatedDate(matchAllFilters, filter, spec);
						break;
					default:
						throw new Exception("Filter not implemented");
				}
			}
		}

		List<Domain> domains = new ArrayList<>();
		domains.add(domainService.findOrCreate(domainName));

		if (spec == null) {
			spec = Specification.where(IncompleteUserSpecifications.domainIn(domains));
		} else {
			spec = spec.and(Specification.where(IncompleteUserSpecifications.domainIn(domains)));
		}

		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<IncompleteUser> s = Specification.where(IncompleteUserSpecifications.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}

		Page<IncompleteUser> userPageList = repository.findAll(spec, request.getPageRequest());

		return new DataTableResponse<>(request, userPageList);
	}

	private Specification<IncompleteUser> playerCreatedDate(
		Boolean matchAllFilters,
		IncompleteUserFilter filter,
		Specification<IncompleteUser> spec
	) throws Exception {
		Integer xDaysAgoInt = null;
		try {
			xDaysAgoInt = Integer.parseInt(filter.getValue());
		} catch (NumberFormatException e) {
			throw new Exception("Could not convert String fValue to Integer");
		}
		DateTime xDaysAgoDt = new DateTime();
		if (xDaysAgoInt.intValue() > 0)
			xDaysAgoDt = xDaysAgoDt.minusDays(xDaysAgoInt);
		switch (filter.getOperator()) {
			case FILTER_OPERATOR_EQUAL_TO:
				if (spec == null) {
					spec = Specification.where(IncompleteUserSpecifications.createdDateBetween(xDaysAgoDt.withTimeAtStartOfDay().toDate(), xDaysAgoDt.plusDays(1).withTimeAtStartOfDay().toDate()));
				} else {
					if (matchAllFilters) {
						spec = spec.and(IncompleteUserSpecifications.createdDateBetween(xDaysAgoDt.withTimeAtStartOfDay().toDate(), xDaysAgoDt.plusDays(1).withTimeAtStartOfDay().toDate()));
					} else {
						spec = spec.or(IncompleteUserSpecifications.createdDateBetween(xDaysAgoDt.withTimeAtStartOfDay().toDate(), xDaysAgoDt.plusDays(1).withTimeAtStartOfDay().toDate()));
					}
				}
				break;
			case FILTER_OPERATOR_LESS_THAN:
				if (spec == null) {
					spec = Specification.where(IncompleteUserSpecifications.createdDateBefore(xDaysAgoDt.withTimeAtStartOfDay().toDate()));
				} else {
					if (matchAllFilters) {
						spec = spec.and(IncompleteUserSpecifications.createdDateBefore(xDaysAgoDt.withTimeAtStartOfDay().toDate()));
					} else {
						spec = spec.or(IncompleteUserSpecifications.createdDateBefore(xDaysAgoDt.withTimeAtStartOfDay().toDate()));
					}
				}
				break;
			case FILTER_OPERATOR_GREATER_THAN:
				if (spec == null) {
					spec = Specification.where(IncompleteUserSpecifications.createdDateAfter(xDaysAgoDt.plusDays(1).withTimeAtStartOfDay().toDate()));
				} else {
					if (matchAllFilters) {
						spec = spec.and(IncompleteUserSpecifications.createdDateAfter(xDaysAgoDt.plusDays(1).withTimeAtStartOfDay().toDate()));
					} else {
						spec = spec.or(IncompleteUserSpecifications.createdDateAfter(xDaysAgoDt.plusDays(1).withTimeAtStartOfDay().toDate()));
					}
				}
				break;
			default:
				throw new Exception("Filter operator not implemented!");
		}
		return spec;
	}
}
