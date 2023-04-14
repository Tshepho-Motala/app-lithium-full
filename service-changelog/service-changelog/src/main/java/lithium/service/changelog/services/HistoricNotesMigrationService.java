package lithium.service.changelog.services;

import lithium.service.changelog.data.entities.Category;
import lithium.service.changelog.data.entities.SubCategory;
import lithium.service.changelog.data.entities.User;
import lithium.service.libraryvbmigration.data.dto.AccountingNotes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoricNotesMigrationService {

  private final UserService userService;
  private final CategoryService categoryService;
  private final ChangelogEntriesService changelogEntriesService;

  public void prepAccountNotesData(AccountingNotes details) {

    Category category = categoryService.findOrCreateCategory(details.getCategory());

    categoryService.findOrCreateSubCategory(details.getSubCategory(), category);
  }
  public void ingestAccountNotes(AccountingNotes details) throws Exception {
    User user = userService.findOrCreate(details.getPlayerguid());

    Category category = categoryService.findCategoryByName(details.getCategory());

    SubCategory subCategory = categoryService.findSubCategoryByName(details.getSubCategory());

    lithium.client.changelog.objects.ChangeLog log = lithium.client.changelog.objects.ChangeLog.builder()
        .categoryName(category.getName())
        .subCategoryName(subCategory.getName())
        .domainName(details.getDomainName())
        .changeDate(details.getCreationDate())
        .dateUpdated(details.isDeleted() ? details.getDeletionDate() : details.getCreationDate())
        .comments(details.getComments())
        .entityRecordId(details.getEntityId())
        .authorGuid(user.getGuid())
        .authorFullName(user.getGuid())
        .priority(50)
        .deleted(details.isDeleted())
        .build();

    changelogEntriesService.addMigrationNote(log);
  }


}
