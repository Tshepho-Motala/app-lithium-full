package lithium.service.document.data.repositories;

import lithium.service.document.data.entities.DocumentType;
import lithium.service.document.data.entities.Domain;
import lithium.service.document.data.entities.ReviewReason;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ReviewReasonRepository extends PagingAndSortingRepository<ReviewReason, Long> {
    ReviewReason findByDomainAndName(Domain domain, String name);
    List<ReviewReason> findAllByDomainNameAndEnabledTrue(String domainName);
    default ReviewReason findOrCreate(Domain domain, String name) {
        ReviewReason reviewReason = findByDomainAndName(domain, name);
        if (reviewReason == null) {
            reviewReason = ReviewReason.builder()
                    .domain(domain)
                    .name(name)
                    .enabled(true)
                    .build();
            save(reviewReason);
        }
        return reviewReason;
    }

    default ReviewReason findOne(Long id) {
        return findById(id).orElse(null);
    }
}