package lithium.service.report.games.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.games.data.entities.Label;

public interface LabelRepository extends PagingAndSortingRepository<Label, Long> {
	Label findByName(String name);
}