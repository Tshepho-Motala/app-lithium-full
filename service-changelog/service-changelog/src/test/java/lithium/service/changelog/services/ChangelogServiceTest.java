package lithium.service.changelog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.security.Principal;
import java.util.Date;
import lithium.service.changelog.data.entities.Category;
import lithium.service.changelog.data.entities.ChangeLog;
import lithium.service.changelog.data.entities.ChangeLogType;
import lithium.service.changelog.data.entities.SubCategory;
import lithium.service.changelog.data.entities.User;
import lithium.service.changelog.data.repositories.ChangeLogRepository;
import lithium.service.changelog.data.repositories.ChangeLogTypeRepository;
import lithium.tokens.LithiumTokenUtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@ExtendWith(MockitoExtension.class)
class ChangelogServiceTest {

    @Mock
    private ChangeLogRepository changeLogRepository;
    @Mock
    private ChangeLogTypeRepository changeLogTypeRepository;
    @Mock
    private LithiumTokenUtilService tokenService;
    @InjectMocks
    private ChangelogEntriesService changelogEntriesService;
    ChangeLog changeLog;
    Category category;
    SubCategory subCategory;
    User user;
    ChangeLogType changeLogType;
    ChangeLog editedChangeLog;
    Principal principal;

    @BeforeEach
    void setUp() {
        changeLog = new ChangeLog();
        category = new Category();
        subCategory = new SubCategory();
        user = new User();
        changeLogType = new ChangeLogType();
        editedChangeLog = new ChangeLog();
        principal = new Principal() {
            @Override
            public String getName() {
                return "Tester";
            }
        };
    }

    void updateNote() {
        category.setName("Support");
        category.setId(1L);

        subCategory.setId(3L);
        subCategory.setName("Communications");
        subCategory.setCategory(category);

        user.setGuid("livescore/testing");
        user.setId(3);

        changeLogType.setId(4);
        changeLogType.setName("edit");

        changeLog.setId(123);
        changeLog.setCategory(category);
        changeLog.setPriority(0);
        changeLog.setSubCategory(subCategory);
        changeLog.setComments("This is a recurring problem");
        changeLog.setAuthorFullName("Testing User");
        changeLog.setAuthorUser(user);
        changeLog.setChangeDate(new Date());
        changeLog.setType(changeLogType);
        changeLog.setPriority(0);
        changeLog.setPinned(false);

        given(tokenService.getUtil(principal)).willThrow(new OAuth2Exception("Principal not instanceof OAuth2Authentication"));
        given(changeLogRepository.findByIdAndAuthorUserId(123, 3)).willReturn(changeLog);
        given(changeLogTypeRepository.findByName(lithium.util.ChangeLogType.EDIT.name())).willReturn(changeLogType);

        ChangeLog editedChangeLog = getEditedChangeLog();
        given(changeLogRepository.save(editedChangeLog)).willReturn(editedChangeLog);

        lithium.client.changelog.objects.ChangeLog createdChangeLog =
                changelogEntriesService.updateNote(123, user.getGuid(), category, subCategory, 0, "Edited this comment because problem has been fixed", null);
        assertThat(createdChangeLog.getComments()).isEqualTo("Edited this comment because problem has been fixed");
    }

    private ChangeLog getEditedChangeLog() {
        editedChangeLog = new ChangeLog();

        category.setName("Support");
        category.setId(1L);

        subCategory.setId(3L);
        subCategory.setName("Communications");
        subCategory.setCategory(category);

        user.setGuid("livescore/testing");
        user.setId(3);

        changeLogType.setId(4);
        changeLogType.setName("edit");

        editedChangeLog.setId(123);
        editedChangeLog.setCategory(category);
        editedChangeLog.setPriority(0);
        editedChangeLog.setSubCategory(subCategory);
        editedChangeLog.setComments("Edited this comment because problem has been fixed");
        editedChangeLog.setAuthorFullName("Testing User");
        editedChangeLog.setAuthorUser(user);
        editedChangeLog.setChangeDate(new Date());
        editedChangeLog.setType(changeLogType);
        editedChangeLog.setPriority(0);
        editedChangeLog.setPinned(false);
        return editedChangeLog;
    }
}