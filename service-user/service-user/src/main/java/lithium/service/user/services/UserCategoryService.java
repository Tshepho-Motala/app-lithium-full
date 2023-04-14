package lithium.service.user.services;

import static lithium.service.Response.Status.OK;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.objects.BasicUserCategory;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserCategory;
import lithium.service.user.data.entities.UserCategoryProjection;
import lithium.service.user.data.repositories.UserCategoryRepository;
import lithium.service.user.data.specifications.UserCategorySpecification;
import lithium.service.user.data.specifications.UserSpecifications;
import lithium.service.user.exceptions.Status400BadRequestException;
import lithium.service.user.exceptions.Status409DuplicateTagNameExistException;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Service
public class UserCategoryService {

  @Autowired UserCategoryRepository userCategoryRepository;
  @Autowired UserService userService;
  @Autowired ChangeLogService changeLogService;
  @Autowired LithiumTokenUtilService tokenService;
  @Autowired ModelMapper modelMapper;
  @Autowired DomainService domainService;


  public User categoryAddPlayer(
      final User user,
      List<UserCategory> userCategories,
      Principal principal
  ) throws Exception {
    log.debug("UserTag AddPlayer : " + user.guid() + " to : " + userCategories);

    userCategories = userCategories.stream().filter(userCategory -> !checkUserCategories(user.getUserCategories(), userCategory)).collect(Collectors.toList());

    if (!userCategories.isEmpty()) {
    List<ChangeLogFieldChange> clfc = userCategories.stream().map(userCategory -> ChangeLogFieldChange.builder()
        .field("tags")
        .fromValue(checkUserCategories(user.getUserCategories(), userCategory) ? userCategory.getName() : "EMPTY")
        .toValue(userCategory.getName())
        .build()).collect(Collectors.toList());

      changeLogService.registerChangesForNotesWithFullNameAndDomain(
          "user",
          "edit",
          user.getId(),
          principal.getName(),
          tokenService.getUtil(principal),
          "Added a player tag",
          null,
          clfc,
          Category.ACCOUNT,
          SubCategory.EDIT_DETAILS,
          0,
          user.domainName());

      userCategories.forEach(userCategory -> userService.save(user.toBuilder().userCategory(userCategory).build()));

      User savedUser = userService.findOne(user.getId());

      userService.addToSyncUserAttributesQueue(savedUser);

      return savedUser;
    }
    return user;
  }

  public User categoryRemovePlayer(
      final User user,
      List<UserCategory> userCategories,
      Principal principal
  ) throws Exception {
    log.debug("tagRemovePlayer : " + user.guid() + " from : " + userCategories);

    if (!userCategories.isEmpty()) {
      user.setUserCategories(getUpdatedCategories(user.getUserCategories(), userCategories));
      User savedUser = userService.save(user);

      userService.addToSyncUserAttributesQueue(savedUser);

      List<ChangeLogFieldChange> clfc = userCategories.stream().map(userCategory -> ChangeLogFieldChange.builder()
          .field("tags")
          .fromValue(userCategory.getName() != null ? userCategory.getName() : "NOT SETUP")
          .toValue("EMPTY")
          .build()).collect(Collectors.toList());

      changeLogService.registerChangesForNotesWithFullNameAndDomain(
          "user",
          "edit",
          user.getId(),
          principal.getName(),
          tokenService.getUtil(principal),
          "Removed a player tag",
          null,
          clfc,
          Category.ACCOUNT,
          SubCategory.EDIT_DETAILS,
          0,
          user.domainName());
    }
    return user;
  }
  public List<UserCategory> getUpdatedCategories(List<UserCategory> appliedCategories, List<UserCategory> categoriesToRemove) {
    if(categoriesToRemove == null || categoriesToRemove.isEmpty() || appliedCategories == null) {
      return appliedCategories;
    }
    List<Long> tagsToRemove = categoriesToRemove.stream().map(UserCategory::getId).collect(Collectors.toList());
    return appliedCategories.stream().filter(c -> !tagsToRemove.contains(c.getId())).collect(Collectors.toList());
  }
  
  /**
   *
   * @param categories
   * @param category
   * @return
   */
  public boolean checkUserCategories(List<UserCategory> categories, UserCategory category)
  {
    if (categories == null)  {
      return false;
    }
    return categories.stream().anyMatch(c -> c.getName().equalsIgnoreCase(category.getName()));
  }

  public List<UserCategory> findUserCategories(List<Long> tagIds, String domainName) {
    return userCategoryRepository.findAllByIdInAndDomainName(tagIds, domainName)
        .stream()
        .map(userCategoryProjection -> modelMapper.map(userCategoryProjection, UserCategory.class))
        .collect(Collectors.toList());
  }

  public Response<UserCategory> createOrEditTag(BasicUserCategory basicUserCategory, Domain domain) throws Status400BadRequestException, Status409DuplicateTagNameExistException {
    if (basicUserCategory == null) {
      throw new Status400BadRequestException("Invalid request to create tag");
    }
    UserCategory userCategory = null;
    UserCategoryProjection userCategorySearchedUsingDomainAndName = userCategoryRepository.findByNameAndDomain(basicUserCategory.getName(), domain);
    if (basicUserCategory.getId() == null ) {
      if (userCategorySearchedUsingDomainAndName != null)  {
        throw new Status409DuplicateTagNameExistException("A tag with a similar name and domain already exists");
      }else {
        userCategory = createTag(basicUserCategory,domain);
        return Response.<UserCategory>builder().status(OK).data(userCategory).build();
      }
    }
    Optional<UserCategory> userCategoryRetrieved = userCategoryRepository.findById(basicUserCategory.getId());
    if(!userCategoryRetrieved.isPresent() && userCategorySearchedUsingDomainAndName == null ){
      userCategory = createTag(basicUserCategory, domain);
    } else if(tagIsADuplicate(userCategoryRetrieved, userCategorySearchedUsingDomainAndName)){
      throw new Status409DuplicateTagNameExistException("A tag with a similar name and domain already exists");
    } else {
      userCategory = editTag(userCategoryRetrieved.get(), basicUserCategory);
    }
    return Response.<UserCategory>builder().status(Response.Status.OK).data(userCategory).build();
  }

  private boolean tagIsADuplicate(Optional<UserCategory> userCategoryRetrieved, UserCategoryProjection userCategorySearchedUsingDomainAndName){
    if(!userCategoryRetrieved.isPresent() && !ObjectUtils.isEmpty(userCategorySearchedUsingDomainAndName)){
      return true;
    } else if(!userCategoryRetrieved.isPresent() &&  ObjectUtils.isEmpty(userCategorySearchedUsingDomainAndName)){
      return false;
    } else if(userCategoryRetrieved.isPresent() &&  ObjectUtils.isEmpty(userCategorySearchedUsingDomainAndName)) {
      return false;
    } else if(Optional.ofNullable(userCategorySearchedUsingDomainAndName).isPresent() &&  !userCategorySearchedUsingDomainAndName.getId().equals(userCategoryRetrieved.get().getId())){
      return true;
    } else if(Optional.ofNullable(userCategorySearchedUsingDomainAndName).isPresent()  &&  userCategorySearchedUsingDomainAndName.getId().equals(userCategoryRetrieved.get().getId())){
      return false;
    }
    return true;
  }

  private UserCategory createTag(BasicUserCategory basicUserCategory,
      lithium.service.user.data.entities.Domain domain){
    UserCategory userCategoryToBeCreated = UserCategory.builder()
        .name(basicUserCategory.getName())
        .description(basicUserCategory.getDescription())
        .dwhVisible(basicUserCategory.getDwhVisible() == null ? Boolean.FALSE : basicUserCategory.getDwhVisible())
        .domain(domain)
        .build();
    UserCategory userCategorySaved = userCategoryRepository.save(userCategoryToBeCreated);
    return userCategorySaved;

  }

  private UserCategory editTag(UserCategory userCategory, BasicUserCategory basicUserCategory){
    userCategory.setName(basicUserCategory.getName());
    userCategory.setDescription(basicUserCategory.getDescription());
    userCategory.setDwhVisible(basicUserCategory.getDwhVisible() == null ? Boolean.FALSE : basicUserCategory.getDwhVisible());
    UserCategory userCategorySaved = userCategoryRepository.save(userCategory);
    userCategorySaved.setUsers(null);
    return userCategorySaved;
  }

  public List<UserCategory> getPlayerTagListByDomain(String domainName){

    List<Domain> domains = new ArrayList<>();
    Domain domain = domainService.findOrCreate(domainName);
    domains.add(domain);

    Specification<UserCategory> spec;

    if (!domains.isEmpty()){
      spec = Specification.where(UserCategorySpecification.domainIn(domains));
    } else {
      return Collections.emptyList();
    }

    return userCategoryRepository
        .findAll(spec)
        .stream()
        .filter(UserCategory::getDwhVisible)
        .collect(Collectors.toList());
  }

  public List<UserCategory> getPlayerTagListByPlayerGuid(String playerGuid) {
    return userService.findFromGuid(playerGuid)
        .getUserCategories()
        .stream()
        .filter(UserCategory::getDwhVisible)
        .collect(Collectors.toList());
  }

  public List<UserCategory> setPlayerTagsByPlayerGuid(String playerGuid, List<Long> tagIds) {
    User user = userService.findFromGuid(playerGuid);
    List<UserCategory> allByIdInAndDomainName = userCategoryRepository.findByIdInAndDomainName(tagIds, user.getDomain().getName());
    /* Using a set to avoid duplicates */
    Set<UserCategory> userCategorySet = new HashSet<>();
    List<String> oldTags = user.getUserCategories().stream().map(UserCategory::getName).toList();
    userCategorySet.addAll(user.getUserCategories());
    userCategorySet.addAll(allByIdInAndDomainName);
    List<UserCategory> finalList = new ArrayList<>(userCategorySet);
    user.setUserCategories(finalList);
    userService.save(user);
    List<ChangeLogFieldChange> clfc = new ArrayList<>(Collections.singletonList(ChangeLogFieldChange.builder()
        .field("tags")
        .fromValue(oldTags.isEmpty() ? "EMPTY" : oldTags.toString())
        .toValue(String.valueOf(user.getUserCategories().stream().map(UserCategory::getName).toList()))
        .build()));
    changeLogService.registerChangesWithDomainAndFullName("user", "edit", user.getId(), lithium.service.user.client.objects.User.EXTERNAL_GUID, null, null, clfc,
        Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName(), lithium.service.user.client.objects.User.EXTERNAL_GUID);

    return allByIdInAndDomainName;
  }

  public List<UserCategory> removePlayerTagsByPlayerGuid(String playerGuid, List<Long> tagIds) {
    User user = userService.findFromGuid(playerGuid);
    List<String> oldTags = user.getUserCategories().stream().map(UserCategory::getName).toList();
    List<UserCategory> filteredUserCategories = user.getUserCategories()
        .stream()
        .filter(userCategory -> !tagIds.contains(userCategory.getId()))
        .collect(Collectors.toList());
    user.setUserCategories(filteredUserCategories);
    userService.save(user, true);

    List<ChangeLogFieldChange> clfc = new ArrayList<>(Collections.singletonList(ChangeLogFieldChange.builder()
        .field("tags")
        .fromValue(oldTags.isEmpty() ? "EMPTY" : oldTags.toString())
        .toValue(user.getUserCategories().isEmpty() ? "EMPTY" : String.valueOf(user.getUserCategories().stream().map(UserCategory::getName).toList()))
        .build()));
    changeLogService.registerChangesWithDomainAndFullName("user", "delete", user.getId(), lithium.service.user.client.objects.User.EXTERNAL_GUID,
        null, null, clfc,
        Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName(), lithium.service.user.client.objects.User.EXTERNAL_GUID);

    return  filteredUserCategories;
  }

  private List<Domain> getDomains(@RequestParam(name = "domainNames") String[] domainNames) throws Status550ServiceDomainClientException {
    List<Domain> domains = new ArrayList<>();
    for (String domainName : domainNames) {
      if ((domainName == null) || (domainName.isEmpty())) {
        continue;
      }
      Domain domain = domainService.findDomainByName(domainName);
      log.debug("" + domain);
      domains.add(domain);
    }
    return domains;
  }

  public Set<UserCategoryProjection> getCategoryList(LithiumTokenUtil tokenUtil, String[] domainNames) {
    DomainValidationUtil.filterDomainsWithRoles(domainNames, tokenUtil, "PLAYER_TAG_EDIT");

    List<Domain> domains = getDomains(domainNames);

    if (domains.isEmpty()) {
      return Collections.emptySet();
    }

    return userCategoryRepository.findAllByDomainIn(domains);
  }

  public Page<UserCategoryProjection> getCategoryTable(String[] domainNames, DataTableRequest request) {
    List<Domain> domains = getDomains(domainNames);

    if (domains.isEmpty()) {
      return Page.empty();
    }

    if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
      return userCategoryRepository.findByDomainInAndNameContainingOrDescriptionContaining(domains, request.getSearchValue(),
          request.getSearchValue(), request.getPageRequest());
    }

    return userCategoryRepository.findByDomainIn(domains, request.getPageRequest());
  }

  public Page<User> getCategoryViewPlayers(long userCategory, DataTableRequest request) {
    log.debug("UserTagId :" + userCategory);
    Specification<User> spec = Specification.where(UserSpecifications.userCategories(userCategory));

    if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
      Specification<User> s = Specification.where(UserSpecifications.any(request.getSearchValue()));
      spec = spec.and(s);
    }

    return userService.findAll(spec, request.getPageRequest());
  }

  public Set<UserCategoryProjection> getCategoriesFromIds(LithiumTokenUtil tokenUtil, String domainName, Long[] categoryIds)
      throws Status500InternalServerErrorException {
    DomainValidationUtil.validate(domainName, "PLAYER_TAG_EDIT", tokenUtil);
    return userCategoryRepository.findAllByIdIn(Arrays.asList(categoryIds));
  }

  public UserCategory deleteCategory(UserCategory category) {
     userCategoryRepository.delete(category);
    return category;
  }


  public Long countByUserCategories(Long userCategoryId) {
    return userService.countByUserCategoriesId(userCategoryId);
  }

  public UserCategoryProjection getCategoryById(Long userCategoryId) {
    return userCategoryRepository.findUserCategoriesById(userCategoryId);
  }
}
