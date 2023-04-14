package lithium.service.report.players.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.players.data.entities.Label;

public interface LabelRepository extends PagingAndSortingRepository<Label, Long> {
	Label findByName(String name);
}