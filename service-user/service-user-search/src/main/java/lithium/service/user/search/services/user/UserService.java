package lithium.service.user.search.services.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import lithium.client.changelog.ChangeLogService;
import lithium.jpa.util.TriFunction;
import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.client.datatable.DataTablePostRequest;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserCategory;
import lithium.service.user.search.data.repositories.user.UserCategoryRepository;
import lithium.service.user.search.data.repositories.user.UserRepository;
import lithium.service.user.search.data.specifications.UserSpecifications;
import lithium.service.user.search.services.cashier.TransactionService;
import lithium.tokens.LithiumTokenUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "user.UserService")
public class UserService {
  @Autowired
  @Qualifier("user.UserRepository")
  UserRepository userRepository;
  @Autowired
  @Qualifier("user.DomainService")
  DomainService domainService;
  @Autowired
  @Qualifier("user.UserCategoryRepository")
  UserCategoryRepository userCategoryRepository;
  @Autowired
  @Qualifier("cashier.TransactionService")
  TransactionService transactionService;
  @Autowired
  ChangeLogService changeLogService;
  @Autowired
  @Setter
  LimitInternalSystemService limitService;
  @Autowired
  private CachingDomainClientService cachingDomainClientService;

  public User findByDomainNameAndUsername(String domainName, String username) {
    return userRepository.findByDomainNameAndUsername(domainName.toLowerCase(), username.toLowerCase());
  }

  public List<Domain> filterRequestedDomainsForToken(String[] domainNames, LithiumTokenUtil tokenUtil) {
    List<Domain> domains = new ArrayList<>();
    List<Domain> allowedDomainsForUser = new ArrayList<>();

    tokenUtil.domainsWithRole("PLAYER_VIEW").forEach(jwtDomain -> {
      if ((jwtDomain.getPlayerDomain() != null) && (jwtDomain.getPlayerDomain())) {
        Domain domain = domainService.findOrCreate(jwtDomain.getName());
        if (domain != null) {
          allowedDomainsForUser.add(domain);
        }
      }
    });

    if (domainNames != null) {
      for (String domainName : domainNames) {
        if ((domainName == null) || (domainName.isEmpty())) {
          continue;
        }
        Domain d = domainService.findOrCreate(domainName);
        if (tokenUtil.hasRole(domainName, "PLAYER_VIEW")) {
          if (allowedDomainsForUser.contains(d)) {
            domains.add(d);
          }
        }
      }
    }

    return domains;
  }

  public Page<User> buildUserTable(
      DataTablePostRequest request,
      LithiumTokenUtil tokenUtil
  ) throws Status550ServiceDomainClientException {
    String[] domainNames = request.requestDataArray("domainNames");
    String[] tags = request.requestDataArray("tags"); //categories
    List<Long> domainRestrictionsIds= request.requestDataListOfLong("restrictions");
    String username = request.requestData("username");
    String firstName = request.requestData("firstName");
    String lastName = request.requestData("lastName");
    Date signupDateRangeStart = request.requestDataDate("signupDateRangeStart");
    Date signupDateRangeEnd = request.requestDataDate("signupDateRangeEnd");
    String[] status = request.requestDataArray("status");
    String[] statusReason = request.requestDataArray("statusReason");
    String id = request.requestData("id");
    Date dateofbirthStartDate = request.requestDataDate("dateofbirthstartdate");
    Date dateofbirthEndDate = request.requestDataDate("dateofbirthenddate");
    String email = request.requestData("email");
    String mobileNumber = request.requestData("mobilenumber");
    String postalCode = request.requestData("postalcode");
    String currentAccountBalanceQuery = request.requestData("currentAccountBalanceQuery");
    String[] verificationStatus = request.requestDataArray("verificationstatus");
//    String includeexcludetestaccount = request.requestData("includeexcludetestaccount");
    String accountmanagementstatus = request.requestData("accountmanagementstatus");
    Date lastloginStartDate = request.requestDataDate("lastloginstartdate");
    Date lastloginEndDate = request.requestDataDate("lastloginenddate");
    String[] clientTypes = request.requestDataArray("clienttype");
    String assignedaccountmanager = request.requestData("assignedaccountmanager");
    Date lastdepositStartDate = request.requestDataDate("lastdepositstartdate");
    Date lastdepositEndDate = request.requestDataDate("lastdepositenddate");
    String isTestAccount = request.requestData("test");
    String isAgeVerified = request.requestData("ageVerified");
    String isAddressVerified = request.requestData("addressVerified");
    String isEmailValidated = request.requestData("emailValidated");
    String isActiveRestriction = request.requestData("isActiveRestriction");
    Date restrictionActiveFromDate = request.requestDataDate("restrictionActiveFromDate");
    // Cashier
    String cashierStatus = request.requestData("cashierTranStatus");
//    cashierStatus = "WAITFORAPPROVAL";
    Date cashierStartDate = request.requestDataDate("cashierStartDate");
    Date cashierEndDate = request.requestDataDate("cashierEndDate");
    String documentStatus = request.requestData("documentStatus");

    List<Long> affiliateLabelIds= request.requestDataListOfLong("affiliates");
    List<Domain> domains = filterRequestedDomainsForToken(domainNames, tokenUtil);
    if (domains.isEmpty()) {
      // No domains selected, returning empty list
      new PageImpl<>(Collections.emptyList(), request.getPageRequest(), 0);
    }

    List<UserCategory> userCategories = new ArrayList<>();
    if (tags != null && tags.length > 0) {
      for (String categoryId:tags) {
        if (categoryId == null || categoryId.isEmpty()) continue;
        UserCategory uc = userCategoryRepository.findOne(Long.valueOf(categoryId));
        userCategories.add(uc);
      }
    }

    final List<Long> verificationStatusList = (verificationStatus != null && verificationStatus.length > 0)
        ? Arrays.stream(verificationStatus)
        .map(s -> {
          try {
            return Long.parseLong(s);
          } catch (NumberFormatException e) {
            // do nothing
          }
          return null;
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList())
        : null;

    final List<String> statusList = (status != null && status.length > 0)
        ? Arrays.stream(status)
        .filter(p -> p != null && !p.trim().isEmpty())
        .collect(Collectors.toList())
        : null;

    final List<String> statusReasonList = (statusReason != null && statusReason.length > 0)
        ? Arrays.stream(statusReason)
        .filter(p -> p != null && !p.trim().isEmpty())
        .collect(Collectors.toList())
        : null;

    final List<String> clientTypeList = (clientTypes != null && clientTypes.length > 0)
        ? Arrays.stream(clientTypes)
        .filter(p -> p != null && !p.trim().isEmpty())
        .collect(Collectors.toList())
        : null;

    Page<User> users = null;

    Specification<User> spec = Specification.where(UserSpecifications.domainIn(domains));
    if (!userCategories.isEmpty()) {
      spec = spec.and(UserSpecifications.userCategoriesIn(userCategories));
    }

    // spec.and(UserSpecifications.fetchUserEvents());

    spec = addToSpec(username, spec, UserSpecifications::usernameStartsWith);
    spec = addToSpec(firstName, spec, UserSpecifications::firstNameStartsWith);
    spec = addToSpec(lastName, spec, UserSpecifications::lastNameStartsWith);
    spec = addToSpec(request.getSearchValue(), spec, UserSpecifications::any);
    spec = addToSpec(signupDateRangeStart, false, spec, UserSpecifications::signupDateRangeStart);
    spec = addToSpec(signupDateRangeEnd, true, spec, UserSpecifications::signupDateRangeEnd);
    spec = addToSpec(statusList, spec, UserSpecifications::statusesIn);
    spec = addToSpec(statusReasonList, spec, UserSpecifications::statusReasonsIn);
    spec = addToSpec(id, spec, UserSpecifications::idStartsWith);
    spec = addToSpec(dateofbirthStartDate, dateofbirthEndDate, spec, UserSpecifications::dateOfBirth);
    spec = addToSpec(email, spec, UserSpecifications::email);
    spec = addToSpec(mobileNumber, spec, UserSpecifications::mobilenumber);
    spec = addToSpec(postalCode, spec, UserSpecifications::postalcode);
    spec = addToSpecLongList(verificationStatusList, spec, UserSpecifications::verificationstatusesIn);
    spec = addToSpec(lastloginStartDate, lastloginEndDate, spec, UserSpecifications::lastlogin);
    spec = addToSpec(lastdepositStartDate, lastdepositEndDate, spec, UserSpecifications::lastdeposit);
    spec = addToSpec(clientTypeList, spec, UserSpecifications::clienttype);
    spec = addToSpec(isTestAccount, spec, UserSpecifications::isTestAccount);
    spec = addToSpec(isAgeVerified, spec, UserSpecifications::isAgeVerified);
    spec = addToSpec(isAddressVerified, spec, UserSpecifications::isAddressVerified);
    spec = addToSpec(isEmailValidated, spec, UserSpecifications::isEmailValidated);
    spec = spec.and(UserSpecifications.isDeleted(false));

    TransactionStatus transactionStatus = transactionService.findStatusByCode(cashierStatus);

    log.debug("cashier TransactionStatus: "+transactionStatus);
    log.debug("cashier StartDate: "+cashierStartDate);
    log.debug("cashier EndDate: "+cashierEndDate);

    spec = addToSpec(transactionStatus, cashierStartDate, cashierEndDate, spec, UserSpecifications::cashier);
    spec = addToSpec(currentAccountBalanceQuery, spec, UserSpecifications::currentAccountBalance);
    spec = addToSpec(domainRestrictionsIds, isActiveRestriction, restrictionActiveFromDate ,spec, UserSpecifications::userRestrictions);
    spec = addToSpec(documentStatus, spec, UserSpecifications::documentStatuses);

    spec = addToSpecLongList(affiliateLabelIds, spec, UserSpecifications::affiliatesLabels);


    users = userRepository.findAllBy(spec, request.getPageRequest());
    Map<String, List<User>> groupedUsers = users.getContent().stream().collect(Collectors.groupingBy(User::domainName));

    for(String domainNameKey : groupedUsers.keySet()) {
      lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainNameKey);
      String result = domain.findDomainSettingByName(DomainSettings.DISPLAY_LASTNAME_PREFIX.key()).orElse(null);
      if(result != null && result.equals("hide")) {
        List<User> domainUserList = groupedUsers.get(domainNameKey);
        List<User> userList = domainUserList.stream().filter(x -> x.getLastNamePrefix() != null)
            .filter(x -> !x.getLastNamePrefix().isEmpty()).collect(Collectors.toList());
        for (User user : userList) {
          user.setLastNamePrefix("");
        }
      }
    }

    for (User user : users) {
      user.setFirstName(StringEscapeUtils.escapeHtml(user.getFirstName()));
      user.setLastName(StringEscapeUtils.escapeHtml(user.getLastName()));
    }

    return users;
  }
  private Specification<User> addToSpec(
      TransactionStatus transactionStatus,
      Date startDate,
      Date endDate,
      Specification<User> spec,
      TriFunction<TransactionStatus, Date, Date, Specification<User>> predicateMethod
  ) {
    Specification<User> localSpec = Specification.where(predicateMethod.apply(transactionStatus, startDate, endDate));
    spec = (spec == null) ? localSpec : spec.and(localSpec);
    return spec;
  }

  private Specification<User> addToSpec(final List<String> aString, Specification<User> spec,
      Function<List<String>, Specification<User>> predicateMethod) {
    if (aString != null && !aString.isEmpty()) {
      Specification<User> localSpec = Specification.where(predicateMethod.apply(aString));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  private Specification<User> addToSpecLongList(final List<Long> aLongList, Specification<User> spec,
      Function<List<Long>, Specification<User>> predicateMethod) {
    if (aLongList != null && !aLongList.isEmpty()) {
      Specification<User> localSpec = Specification.where(predicateMethod.apply(aLongList));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  private Specification<User> addToSpec(final String aString, Specification<User> spec, Function<String, Specification<User>> predicateMethod) {
    if (aString != null && !aString.isEmpty()) {
      Specification<User> localSpec = Specification.where(predicateMethod.apply(aString));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  private Specification<User> addToSpec(final Date aDate, boolean addDay, Specification<User> spec,
      Function<Date, Specification<User>> predicateMethod) {
    if (aDate != null) {
      DateTime someDate = new DateTime(aDate);
      if (addDay) {
        someDate = someDate.plusDays(1).withTimeAtStartOfDay();
      } else {
        someDate = someDate.withTimeAtStartOfDay();
      }
      Specification<User> localSpec = Specification.where(predicateMethod.apply(someDate.toDate()));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  private Specification<User> addToSpec(
      final Date dateRangeStart,
      final Date dateRangeEnd,
      Specification<User> spec,
      BiFunction<Date, Date, Specification<User>> predicateMethod) {

    if (dateRangeStart != null || dateRangeEnd != null) {

      LocalDateTime startDate = null;
      LocalDateTime endDate = null;

      if (dateRangeStart != null) {
        startDate = LocalDateTime.fromDateFields(dateRangeStart);
      }
      if (dateRangeEnd != null) {
        endDate = LocalDateTime.fromDateFields(dateRangeEnd);
      } else {
        endDate = LocalDateTime.now().plusYears(20);
      }

      if (startDate != null && startDate.isEqual(endDate)) {
        endDate = startDate.plusDays(1).withTime(0, 0, 0, 0);
      }

      final Specification<User> localSpec = Specification.where(predicateMethod.apply(
          startDate != null ? startDate.toDate() : null,
          endDate != null ? endDate.toDate() : null
      ));

      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }

    return spec;
  }

  private Specification<User> addToSpec(
      List<Long> domainRestrictions,
      String isActiveRestriction,
      Date restrictionActiveFromDate,
      Specification<User> spec,
      TriFunction<List<Long>, String, Date, Specification<User>> predicateMethod
  ) {
    Specification<User> localSpec = Specification.where(predicateMethod.apply(domainRestrictions, isActiveRestriction, restrictionActiveFromDate));
    spec = (spec == null) ? localSpec : spec.and(localSpec);
    return spec;
  }
}
