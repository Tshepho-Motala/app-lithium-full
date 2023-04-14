package lithium.service.changelog.controllers.backoffice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.Date;
import lithium.service.Response;
import lithium.service.changelog.data.entities.Category;
import lithium.service.changelog.data.entities.ChangeLog;
import lithium.service.changelog.data.entities.ChangeLogType;
import lithium.service.changelog.data.entities.SubCategory;
import lithium.service.changelog.data.entities.User;
import lithium.service.changelog.data.repositories.CategoryRepository;
import lithium.service.changelog.data.repositories.SubCategoryRepository;
import lithium.service.changelog.services.ChangelogEntriesService;
import lithium.tokens.LithiumTokenUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class BackOfficeChangelogEntriesControllerTest {

    @InjectMocks
    BackOfficeChangelogEntriesController changelogEntriesController;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    SubCategoryRepository subCategoryRepository;
    @Mock ChangelogEntriesService changelogEntriesService;

    ChangeLog changeLog;
    Category category;
    SubCategory subCategory;
    User user;
    ChangeLogType changeLogType;
    ChangeLog editedChangeLog;
    lithium.client.changelog.objects.ChangeLog input;
    LithiumTokenUtil util = Mockito.mock(LithiumTokenUtil.class);

    @BeforeEach
    void setUp() {
        changeLog = new ChangeLog();
        category = new Category();
        subCategory = new SubCategory();
        user = new User();
        changeLogType = new ChangeLogType();
        editedChangeLog = new ChangeLog();
        input = new lithium.client.changelog.objects.ChangeLog();
    }

    @Test
    void updateChangeLogNotes() {
        category.setName("Support");
        category.setId(1L);

        subCategory.setId(3L);
        subCategory.setName("Communications");
        subCategory.setCategory(category);

        input.setId(123);
        input.setAuthorFullName("Testing User");
        input.setAuthorGuid("3");
        input.setCategoryName("Support");
        input.setSubCategoryName("Communications");
        input.setChangeDate(new Date());
        input.setComments("This is a recurring problem");
        input.setPriority(0);
        input.setType("edit");
        input.setCategoryName(category.getName());
        input.setSubCategoryName(subCategory.getName());

        given(categoryRepository.findByName(input.getCategoryName())).willReturn(category);
        given(subCategoryRepository.findByName(input.getSubCategoryName())).willReturn(subCategory);

        given(changelogEntriesService.updateNote(eq(input.getId()), eq(input.getAuthorGuid()), eq(category), eq(subCategory), eq(input.getPriority()), eq(input.getComments()), eq(util)))
                .willReturn(input);

        Response<lithium.client.changelog.objects.ChangeLog> changeLogResponse = changelogEntriesController.updateChangeLogNotes(input, util);
        Assertions.assertTrue(changeLogResponse.isSuccessful());
    }
}