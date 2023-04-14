package lithium.service.user.services;

import javax.transaction.Transactional;
import lithium.service.access.client.AccessService;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.SignupEvent;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.SignupEventRepository;
import lithium.service.user.data.specifications.SignupEventSpecification;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

@Service
public class SignupEventService {
	@Autowired SignupEventRepository signupEventRepository;
	@Autowired UserService userService;

	public Page<SignupEvent> find(
		List<Domain> domains,
		Long userId,
		String signupDateRangeStart,
		String signupDateRangeEnd,
		Boolean successful,
		String searchValue,
		Pageable pageable
	) {
		Specification<SignupEvent> spec = Specification.where(SignupEventSpecification.domainIn(domains));

		if (userId != null) {
			User user = userService.findOne(userId);
			spec = spec.and(Specification.where(SignupEventSpecification.user(user)));
		}

		if (successful != null) {
			spec = spec.and(Specification.where(SignupEventSpecification.signupSuccessful(successful)));
		}


		spec = addToSpec(signupDateRangeStart, false, spec, SignupEventSpecification::signupDateRangeStart);
		spec = addToSpec(signupDateRangeEnd, true, spec, SignupEventSpecification::signupDateRangeEnd);

		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<SignupEvent> s = Specification.where(SignupEventSpecification.anyContains(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}

		Page<SignupEvent> signupEvents = signupEventRepository.findAll(spec, pageable);
		for(int i = 0; i < signupEvents.getContent().size(); i++) {
			final SignupEvent signupEvent = signupEvents.getContent().get(i);
			if ((signupEvent.getSuccessful()) && (signupEvent.getUser()!=null)) {
				signupEvent.getUser().setFirstName(escapeHtml(signupEvent.getUser().getFirstName()));
				signupEvent.getUser().setLastName(escapeHtml(signupEvent.getUser().getLastName()));
			}
		}
		return signupEvents;
	}

	/**
	 * Produces a specification or appends to an existing specification.
	 * The addDay flag can be used to add a day the the date passed in.
	 * @param hopefullyADate
	 * @param addDay
	 * @param spec
	 * @param predicateMethod
	 * @return modified specification
	 */
	private Specification<SignupEvent> addToSpec(final String hopefullyADate, boolean addDay, Specification<SignupEvent> spec, Function<Date, Specification<SignupEvent>> predicateMethod) {
		if (hopefullyADate != null && !hopefullyADate.isEmpty()) {
			DateTime someDate = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(hopefullyADate);
			if (addDay) {
				someDate = someDate.plusDays(1).withTimeAtStartOfDay();
			} else {
				someDate = someDate.withTimeAtStartOfDay();
			}
			Specification<SignupEvent> localSpec = Specification.where(predicateMethod.apply(someDate.toDate()));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	@Transactional
	public SignupEvent saveSignupEvent(Map<String, String> ipAndUserAgentData, Domain domain, String comment, long userId, Boolean successful) {
	  User user = userService.findOne(userId);
		SignupEvent signupEvent = SignupEvent.builder()
				.date(new Date())
				.domain(domain)
				.ipAddress((ipAndUserAgentData.get(AccessService.MAP_IP) != null)? ipAndUserAgentData.get(AccessService.MAP_IP): null)
				.country((ipAndUserAgentData.get(AccessService.MAP_COUNTRY) != null)? ipAndUserAgentData.get(AccessService.MAP_COUNTRY): null)
				.state((ipAndUserAgentData.get(AccessService.MAP_STATE) != null)? ipAndUserAgentData.get(AccessService.MAP_STATE): null)
				.city((ipAndUserAgentData.get(AccessService.MAP_CITY) != null)? ipAndUserAgentData.get(AccessService.MAP_CITY): null)
				.os((ipAndUserAgentData.get(AccessService.MAP_OS) != null)? ipAndUserAgentData.get(AccessService.MAP_OS): null)
				.browser((ipAndUserAgentData.get(AccessService.MAP_BROWSER) != null)? ipAndUserAgentData.get(AccessService.MAP_BROWSER): null)
        .userAgent(!ObjectUtils.isEmpty(ipAndUserAgentData.get(AccessService.MAP_USERAGENT)) ? ipAndUserAgentData.get(AccessService.MAP_USERAGENT) : null)
				.comment(comment)
				.user(user)
				.successful(successful)
				.build();
		signupEvent = signupEventRepository.save(signupEvent);
		return signupEvent;
	}
}
