package lithium.service.user.services;

import junit.framework.TestCase;
import lithium.client.changelog.ChangeLogService;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.client.objects.BasicUserCategory;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.UserCategory;
import lithium.service.user.data.entities.UserCategoryProjection;
import lithium.service.user.data.repositories.DomainRepository;
import lithium.service.user.data.repositories.UserCategoryRepository;
import lithium.service.user.exceptions.Status100InvalidInputDataException;
import lithium.service.user.exceptions.Status400BadRequestException;
import lithium.service.user.exceptions.Status409DuplicateTagNameExistException;
import lithium.tokens.LithiumTokenUtilService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserCategoryServiceTest extends TestCase {

  @InjectMocks
  public UserCategoryService userCategoryService;

  @Mock
  UserCategoryRepository userCategoryRepository;
  @Mock
  UserService userService;
  @Mock
  ChangeLogService changeLogService;
  @Mock
  LithiumTokenUtilService tokenService;
  @Mock
  DomainRepository domainRepository;

  UserCategory userCategory = getUserCategory(1L);
  UserCategory userCategoryWithDifferentId = getUserCategory(2L);
  UserCategoryProjection userCategoryProjection = getUserCategoryProjection(userCategory);
  BasicUserCategory basicUserCategory = BasicUserCategory.builder()
      .id(1L)
      .description("test_description")
      .name("test_tag_name")
      .domainName("livescoredev")
      .dwhVisible(true)
      .build();
  Domain domain = getDomain();

  @Test(expected = Status400BadRequestException.class)
  public void shouldThrowStatus400BadRequestExceptionIfRequestIsNull() throws Status400BadRequestException, Status409DuplicateTagNameExistException {
    basicUserCategory = null;
    Response<UserCategory> userCategoryResponse = userCategoryService.createOrEditTag(basicUserCategory, domain);
    verify(userCategoryRepository, times(0)).save(any(UserCategory.class));
  }

  @Test(expected = Status409DuplicateTagNameExistException.class)
  public void shouldNotCreateATagIfIdIsNullAndAndThereIsAnotherTagWithTheSameNameInTheDomainItIsBeingCreatedOn()
      throws Status400BadRequestException, Status409DuplicateTagNameExistException {
    basicUserCategory.setId(null);
    when(userCategoryRepository.findByNameAndDomain(anyString(), any())).thenReturn(userCategoryProjection);
    Response<UserCategory> userCategoryResponse = userCategoryService.createOrEditTag(basicUserCategory, domain);
    verify(userCategoryRepository, times(0)).save(any(UserCategory.class));
  }

  @Test(expected = Status409DuplicateTagNameExistException.class)
  public void shouldNotCreateATagIfTagIdIsNotNullAndThereIsAnotherTagWithTheSameNameInTheDomainItIsBeingCreatedOnAndFindByIdReturnsNull()
      throws Status400BadRequestException, Status409DuplicateTagNameExistException {
    when(userCategoryRepository.findByNameAndDomain(anyString(), any())).thenReturn(userCategoryProjection);
    when(userCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());
    Response<UserCategory> userCategoryResponse = userCategoryService.createOrEditTag(basicUserCategory, domain);
    verify(userCategoryRepository, times(0)).save(any(UserCategory.class));
  }

  @Test
  public void shouldCreateATagIfIdIsNullAndThereIsNoOtherTagWithTheSameNameFoundInTheDomainItIsBeingCreatedOn()
      throws Status400BadRequestException, Status409DuplicateTagNameExistException {
    basicUserCategory.setId(null);
    when(userCategoryRepository.findByNameAndDomain(anyString(), any())).thenReturn(null);
    Response<UserCategory> userCategoryResponse = userCategoryService.createOrEditTag(basicUserCategory, domain);
    verify(userCategoryRepository, times(1)).save(any(UserCategory.class));
    assertEquals(Status.OK, userCategoryResponse.getStatus());
  }

  @Test
  public void shouldCreateATagIfTagIdIsNotNullAndThereIsNoOtherTagWithTheSameNameInTheDomainItIsBeingCreatedOnAndFindByIdReturnsNull()
      throws Status400BadRequestException, Status409DuplicateTagNameExistException {
    when(userCategoryRepository.findByNameAndDomain(anyString(), any())).thenReturn(null);
    when(userCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());
    Response<UserCategory> userCategoryResponse = userCategoryService.createOrEditTag(basicUserCategory, domain);
    verify(userCategoryRepository, times(1)).save(any(UserCategory.class));
    assertEquals(Status.OK, userCategoryResponse.getStatus());
  }

  @Test(expected = Status409DuplicateTagNameExistException.class)
  public void shouldNotEditATagIfTagIdIsNotNullGivingItANameThatIsAlreadyUsedByAnotherTagInTheDomainItIsBeingUpdatedOnAndFindByIdReturnsRecord()
      throws Status400BadRequestException, Status409DuplicateTagNameExistException {
    when(userCategoryRepository.findByNameAndDomain(anyString(), any())).thenReturn(userCategoryProjection);
    when(userCategoryRepository.findById(anyLong())).thenReturn(Optional.of(userCategoryWithDifferentId));
    Response<UserCategory> userCategoryResponse = userCategoryService.createOrEditTag(basicUserCategory, domain);
    verify(userCategoryRepository, times(0)).save(any(UserCategory.class));
  }

  @Test
  public void shouldEditATagIfTagIdIsNotNullAndThereIsNoOtherTagWithTheSameNameInTheDomainItIsBeingUpdatedOnAndFindByIdReturnsRecord()
      throws Status400BadRequestException, Status409DuplicateTagNameExistException, Status100InvalidInputDataException {
    when(userCategoryRepository.findByNameAndDomain(anyString(), any())).thenReturn(userCategoryProjection);
    when(userCategoryRepository.findById(anyLong())).thenReturn(Optional.of(userCategory));
    when(userCategoryRepository.save(userCategory)).thenReturn(new UserCategory());
    Response<UserCategory> userCategoryResponse = userCategoryService.createOrEditTag(basicUserCategory, domain);
    verify(userCategoryRepository, times(1)).save(any(UserCategory.class));
    assertEquals(Status.OK, userCategoryResponse.getStatus());
  }

  private static UserCategory getUserCategory(long id){
    return  UserCategory.builder()
        .description("test_description")
        .name("test_tag_name")
        .domain(getDomain())
        .dwhVisible(true)
        .id(id)
        .build();
  }

  private static  UserCategoryProjection getUserCategoryProjection(UserCategory userCategory){
    return new UserCategoryProjection() {
      @Override
      public Long getId() {
        return userCategory.getId();
      }

      @Override
      public Domain getDomain() {
        return userCategory.getDomain();
      }

      @Override
      public String getName() {
        return userCategory.getName();
      }

      @Override
      public String getDescription() {
        return userCategory.getDescription();
      }

      @Override
      public Boolean getDwhVisible() {
        return userCategory.getDwhVisible();
      }
    };
  }

  private static Domain getDomain(){
    return Domain.builder().id(1L).build();
  }
}
