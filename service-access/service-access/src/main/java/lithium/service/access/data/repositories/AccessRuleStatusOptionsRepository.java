package lithium.service.access.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.access.data.entities.AccessRuleResultStatusOptions;

public interface AccessRuleStatusOptionsRepository extends PagingAndSortingRepository<AccessRuleResultStatusOptions, Long> {
	AccessRuleResultStatusOptions findByNameAndOutputAndOutcome(final String name, final boolean output, final boolean outcome);
	List<AccessRuleResultStatusOptions> findByOutput(boolean output);
	List<AccessRuleResultStatusOptions> findByOutcome(boolean outcome);
	List<AccessRuleResultStatusOptions> findByOutputAndOutcome(boolean output, boolean outcome);
	AccessRuleResultStatusOptions findByNameAndOutcomeTrue(final String key);
	AccessRuleResultStatusOptions findByNameAndOutputTrue(final String key);

	default AccessRuleResultStatusOptions findOrCreate(final String name, final boolean output, final boolean outcome) {
		AccessRuleResultStatusOptions accessRuleResultStatusOptions = findByNameAndOutputAndOutcome(name, output, outcome);
		if (accessRuleResultStatusOptions == null) {
				accessRuleResultStatusOptions = AccessRuleResultStatusOptions.builder()
						.name(name)
						.outcome(outcome)
						.output(output)
						.build();
			accessRuleResultStatusOptions = save(accessRuleResultStatusOptions);
		}
		return accessRuleResultStatusOptions;
	}

}
