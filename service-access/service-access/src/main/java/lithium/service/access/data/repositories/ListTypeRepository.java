package lithium.service.access.data.repositories;

import lithium.service.access.data.entities.ListType;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ListTypeRepository extends PagingAndSortingRepository<ListType, Long> {
	ListType findByName(String listTypeName);

    default ListType findOrCreate(
      String name,
      String displayName,
      String description
    ) {
      ListType listType = findByName(name);
      if (listType == null) {
        listType = ListType.builder()
          .name(name)
          .displayName(displayName)
          .description(description)
          .build();
        listType = save(listType);
      }
      return listType;
    };
}
