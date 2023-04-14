package lithium.service.user;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.translate.client.stream.TranslationsStream;
import lithium.service.user.client.enums.UserLinkTypes;
import lithium.service.user.client.objects.AccountCode;
import lithium.service.user.data.entities.Category;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.GRD;
import lithium.service.user.data.entities.Granularity;
import lithium.service.user.data.entities.Group;
import lithium.service.user.data.entities.IncompleteUser;
import lithium.service.user.data.entities.Role;
import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.TransactionTypeAccount;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserLinkType;
import lithium.service.user.data.repositories.CategoryRepository;
import lithium.service.user.data.repositories.DomainRepository;
import lithium.service.user.data.repositories.GRDRepository;
import lithium.service.user.data.repositories.GroupRepository;
import lithium.service.user.data.repositories.IncompleteUserRepository;
import lithium.service.user.data.repositories.RoleRepository;
import lithium.service.user.data.repositories.GranularityRepository;
import lithium.service.user.data.repositories.StatusRepository;
import lithium.service.user.data.repositories.TransactionTypeAccountRepository;
import lithium.service.user.data.repositories.UserLinkTypeRepository;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import lithium.service.user.services.UserService;
import lithium.util.ExceptionMessageUtil;
import lithium.util.PasswordHashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
//@EnableAsync
@Configuration
public class Init {
	@Value("${lithium.password.salt}")
	private String passwordSalt;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private GRDRepository grdRepository;
	@Autowired
	private InitTranslationService translationService;
	@Autowired
	private TransactionTypeAccountRepository transactionTypeAccountRepository;
	@Autowired
	private StatusRepository statusRepository;
	@Autowired 
	private  IncompleteUserRepository  incompleteUserRepository ;
	@Autowired
  private GranularityRepository granularityRepository;
	@Autowired
  UserLinkTypeRepository userLinkTypeRepository;
	
	//@PostConstruct
	public void init() throws LithiumServiceClientFactoryException, Status500InternalServerErrorException {
		Category adminCategory = createCategory("AdminCategory", "Super Network Admin Operations");
		Role adminRole = createRole(adminCategory, "Admin User", "ADMIN", "Super Network Admin Role");
		
		translationService.startRegisterTranslation(adminCategory, adminRole);
		
		Group adminGroup = createGroup("AdminGroup", "Admin Group", "default");
		
		Group luckybetzGroup = createGroup("LuckybetzGroup", "LB Admin Group", "luckybetz");
		
		createGRD("default", adminGroup, adminRole, adminCategory, true, true);
		
		createGRD("luckybetz", luckybetzGroup, adminRole, adminCategory, true, true);

		Status statusOpen = statusRepository.findByName(lithium.service.user.client.enums.Status.OPEN.statusName());

		createUser("default", "admin", "Gauteng", "Super", "Administrator", "super@default.com", Arrays.asList(adminGroup), statusOpen);
//		createUser("epn", "admin2", "Gauteng", "EPN", "Administrator", "super2@default.com", Collections.emptyList()); //Arrays.asList(adminGroup));

//		createUser("default", "admin2", "Gauteng", "Administrator", "Administrator", "admin@default.com", Arrays.asList(viewUserRole, editUserRole, createUserRole, delUserRole, viewTranslateRole, editTranslateRole), Arrays.asList(group1, group2, group3));
//		createUser("default", "johanvdb", "Gauteng", "Johan", "van den Berg", "johanvdb@default.com", Arrays.asList(viewUserRole, editUserRole, createUserRole), Arrays.asList(group1));
//		createUser("epn", "riaans", "Gauteng", "Riaan", "Schoeman", "riaans@default.com", Collections.emptyList());
//		createUser("default", "chrisc", "Gauteng", "Chris", "Cornelissen", "chrisc@default.com", Arrays.asList(viewUserRole), Arrays.asList(group1));
//		
//		createUser("epn", "epn", "Gauteng", "Player", "Administrator", "epn@default.com");
//		createUser("epn", "lynn", "Gauteng", "Player", "Administrator", "epn@default.com");
//		createUser("ffp", "ffp", "Gauteng", "ffp Player", "Viewer", "ffp@default.com", Collections.emptyList());
//		createUser("int", "int", "Gauteng", "int Player", "Viewer", "int@default.com", Collections.emptyList());
		
		createUser("luckybetz", "luckyuser", "luckyuser123", "LuckyBucky", "Betzor", "int@default.com",  Arrays.asList(luckybetzGroup), statusOpen);
		
	}

  private Status createStatus(String name, String description, boolean userEnabled) {
    Status status = statusRepository.findByName(name);
    if (status == null) {
      status = statusRepository.save(
          Status.builder()
              .name(name)
              .description(description)
              .userEnabled(userEnabled)
              .deleted(false)
              .build()
      );
    }
    return status;
  }

  private GRD createGRD(String domainName, Group group, Role role, Category category, Boolean selfApplied, Boolean descending) {
    if (grdRepository.findByDomainNameAndGroupAndRole(domainName, group, role) == null) {
      Domain domain = null;
      if (domainRepository.findByName(domainName) == null) {
        domain = domainRepository.save(Domain.builder().name(domainName).build());
      } else {
        domain = domainRepository.findByName(domainName);
      }
      GRD grd = grdRepository.save(
//				GRD.builder().domain(domain).group(group).role(role).category(category).build()
          GRD.builder().domain(domain).group(group).role(role).selfApplied(selfApplied).descending(descending).build()
//				GRD.builder().domain(domain).grdGroup(
//					GRDGroup.builder().group(group).build()
//				).grdRole(
//					GRDRole.builder().role(role).build()
//				).build()
      );
      return grdRepository.findOne(grd.getId());
    }
    return null;
  }

  private Role createRole(Category category, String name, String roleStr, String description) {
    Role role = roleRepository.findByRole(roleStr);
    if (role == null) {
//			Role r = roleRepository.save(Role.builder().role(role).name(name).description(description).roleCategory(RoleCategory.builder().category(category).build()).build());
      Role r = roleRepository.save(Role.builder().role(roleStr).name(name).description(description).category(category).build());
      role = roleRepository.findOne(r.getId());
    }
    return role;
  }

  private Category createCategory(String name, String description) {
    Category category = categoryRepository.findByName(name);
    if (category == null) {
      Category c = categoryRepository.save(Category.builder().name(name).description(description).build());
      category = categoryRepository.findOne(c.getId());
    }
    return category;
  }

  private Group createGroup(String name, String description, String domainName) {
    if (groupRepository.findByName(name) == null) {
      Domain domain = null;
      if (!domainName.isEmpty()) {
        if (domainRepository.findByName(domainName) == null) {
          domain = domainRepository.save(Domain.builder().name(domainName).build());
        } else {
          domain = domainRepository.findByName(domainName);
        }
      }
      Group g = groupRepository.save(Group.builder().name(name).description(description).domain(domain).enabled(true).deleted(false).build());
      return groupRepository.findOne(g.getId());
    }
    return null;
  }
//	private Group createGroup(String name, String description) {
//		return createGroup(name, description, "");
//	}
//	private void updateGroupGrd(Group group, List<GRD> grds) {
//		Group g = groupRepository.findOne(group.getId());
//		g.setGrds((grds));
//		groupRepository.save(g);
//	}

  private void createUser(String domain, String username, String password, String firstname, String lastname, String email, List<Group> groups,
      Status status) throws Status500InternalServerErrorException {
    createUser(domain, username, password, firstname, lastname, email, null, groups, status);
  }

  private void createUser(String domainName, String username, String password, String firstname, String lastname, String email, List<Role> roles,
      List<Group> groups, Status status) throws Status500InternalServerErrorException {
    domainName = domainName.toLowerCase();
    username = username.toLowerCase();
    if (userService.findByDomainNameAndUsername(domainName, username.toLowerCase()) == null) {
      Domain domain = null;
      if (domainRepository.findByName(domainName) == null) {
        domain = domainRepository.save(Domain.builder().name(domainName).build());
      } else {
        domain = domainRepository.findByName(domainName);
      }

      String passwordHash;
      try {
        passwordHash = PasswordHashing.hashPassword(password, passwordSalt);
      } catch (lithium.exceptions.Status500InternalServerErrorException e) {
        log.error("Password Hash Exception " + ExceptionMessageUtil.allMessages(e), e);
        throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e));
      }

      User user = User.builder()
          .domain(domain)
          .username(username.toLowerCase())
          .passwordHash(passwordHash)
          .firstName(firstname)
          .lastName(lastname)
//					.enabled(true)
          .deleted(false)
          .email(email)
          .emailValidated(true)
          .cellphoneValidated(false)
//					.roles(roles)
          .groups(groups)
          .createdDate(new Date())
          .updatedDate(new Date())
          .status(status)
          .build();
      userService.save(user);
      createIncompleteSignUpUsers(user);
    }
  }

  private void createIncompleteSignUpUsers(User user) {
    String cell = "0849839098";
    String email = "abandoned@signupds.com";
    String domainName = "livescore_nl";

    IncompleteUser userInProgress = IncompleteUser.builder()
        .cellphoneNumber(cell)
        .countryCode(user.getCountryCode())
        .createdDate(user.getCreatedDate())
        .email(email)
        .domain(domainRepository.findByName(domainName))
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .build();
    IncompleteUser checkedUser = incompleteUserRepository.findByFirstName(user.getFirstName());
    log.info("Searched incomplete user ");

    if (checkedUser == null) {
      log.info("Saving incompleted dummy user ");
      IncompleteUser savedNewTestingUser = incompleteUserRepository.save(userInProgress);
      if (savedNewTestingUser != null) {
        log.info("Testing incomplete user added: ",savedNewTestingUser.getId());
        log.debug(savedNewTestingUser.toString());

      }

    } else {
      log.info("Dummy user already saved");
    }
  }

  public void initUserLinkTypes() {
    for (UserLinkTypes type : UserLinkTypes.values()) {
      UserLinkType userLinkType = userLinkTypeRepository.findByCode(type.code());
      if (userLinkType == null) {
        userLinkType = UserLinkType.builder()
            .code(type.code())
            .linkDirectionSensitive(type.linkDirectionSensitive())
            .description(type.description())
            .build();
        userLinkTypeRepository.save(userLinkType);
      }
    }
  }

  public void initGranularity() {
    Arrays.stream(lithium.service.client.objects.Granularity.values())
        .forEach(granularity -> granularityRepository.findOrCreateByType(granularity.type(),
            () -> Granularity.builder().id(granularity.granularity()).type(granularity.type()).build()));
  }

    public void setupAccountCodesFromEnum() {
        Arrays.stream(AccountCode.values())
                .filter(code -> Objects.isNull(transactionTypeAccountRepository.findByAccountTypeCode(code.getAccountTypeCode())))
                .forEach(code -> transactionTypeAccountRepository.save(
                        TransactionTypeAccount.builder()
                                .accountTypeCode(code.getAccountTypeCode())
                                .debit(code.isDebit())
                                .credit(code.isCredit())
                                .build()));
    }

  @Service
  class InitTranslationService {

    @Autowired private LithiumServiceClientFactory services;
    @Autowired private TranslationsStream translationsStream;

    @Retryable(backoff = @Backoff(delay = 10000), maxAttempts = 30)
    public void startRegisterTranslation(Category category, Role role) throws LithiumServiceClientFactoryException {
      log.info("startRegisterTranslation : " + role + "  ::  " + category);
      registerTranslations(category.getNameCode(), category.getName());
      registerTranslations(category.getDescriptionCode(), category.getDescription());
      registerTranslations(role.getNameCode(), role.getName());
      registerTranslations(role.getDescriptionCode(), role.getDescription());
    }

    private void registerTranslations(String code, String value) {
      log.info("Register Translation : " + code + " value :" + value);
      try {
        translationsStream.registerTranslation(new lithium.service.translate.client.objects.Domain("default"), "en", code, value);
      } catch (Exception e) {
        log.error("Could not register translation for : " + code + " value :" + value, e);
      }
    }
  }
}
