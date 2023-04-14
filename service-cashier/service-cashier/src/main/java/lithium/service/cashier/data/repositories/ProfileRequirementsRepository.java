package lithium.service.cashier.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.ProfileRequirements;

public interface ProfileRequirementsRepository extends PagingAndSortingRepository<ProfileRequirements, Long> {
}
