package lithium.service.user.controllers;

import static lithium.service.Response.Status.FORBIDDEN;
import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.access.client.AccessService;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserProjection;
import lithium.service.user.data.repositories.DomainRepository;
import lithium.service.user.data.repositories.StatusRepository;
import lithium.service.user.data.specifications.UserSpecifications;
import lithium.service.user.services.DomainService;
import lithium.service.user.services.PubSubUserService;
import lithium.service.user.services.SignupEventService;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lithium.util.PasswordHashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestController
@RequestMapping("/{domainName}/users")
public class UsersController {
	@Value("${lithium.password.salt}")
	private String passwordSalt;
  @Value("${lithium.block.endpoint}")
  private Boolean blockEndpoint;
  @Value("${lithium.endpoint.user-list.block}")
  private boolean userListEndpointBlocked;

	@Autowired private DomainRepository domainRepository;
	@Autowired private StatusRepository statusRepository;
	@Autowired private UserService userService;
	@Autowired private DomainService domainService;
	@Autowired private CachingDomainClientService cachingDomainClientService;
	@Autowired private TokenStore tokenStore;
  @Autowired private LithiumTokenUtilService tokenService;
	@Autowired private ChangeLogService changeLogService;
	@Autowired private SignupEventService signupEventService;
	@Autowired private AccessService accessService;
	@Autowired private HttpServletRequest request;
  @Autowired private PubSubUserService pubSubUserService;

	@GetMapping("/findFromGuid")
	public Response<User> findFromGuid(
		@RequestParam("guid") String guid,
    WebRequest webRequest
	) {
    String mapAsString = webRequest.getParameterMap().keySet().stream()
        .map(key -> key + "=" + webRequest.getParameterValues(key)[0])
        .collect(Collectors.joining(", ", "{", "}"));

    log.debug("webRequest: "+webRequest+" : "+webRequest.getUserPrincipal()+" || parameters: "+ mapAsString);
    if (blockEndpoint) {
      log.warn("Endpoint blocked because of property: lithium.block.endpoint");
      return Response.<User>builder().status(INTERNAL_SERVER_ERROR).build();
    }
		User user = null;
		try {
			user = userService.findFromGuid(guid);
			return Response.<User>builder().data(user).status(OK).build();
		} catch (Exception e) {
			log.error("Could not find user "+guid, e);
			return Response.<User>builder().message(e.getMessage()).status(INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/{username}/guidtouserid")
	public Response<Long> guidToUserId(
		@PathVariable("domainName") String domainName,
		@PathVariable("username") String username
	) {
		User user = null;
		try {
			user = userService.findByDomainNameAndUsername(domainName, username);
			return Response.<Long>builder().data(user.getId()).status(OK).build();
		} catch (Exception e) {
			log.error("Could not find user " + domainName + "/" + username, e);
			return Response.<Long>builder().message(e.getMessage()).status(INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping(path="/list")
	public Response<List<UserProjection>> list(
		@PathVariable("domainName") String domainName,
		@RequestParam(name="search") String search,
		Principal principal
	) {
		List<UserProjection> users = userService.findByDomainNameAndUsernameOrFirstNameOrLastNameOrEmail(domainName, search+"%");
		UserProjection up = userService.findByGuid(domainName, domainName+"/"+search);
		if (up != null) users.add(up);
		log.debug("users :: "+users);

		return Response.<List<UserProjection>>builder().status(OK).data(users).build();
	}
	
	//TODO I see we are returning all users, we need to limit based on the users' domain and roles.
	@GetMapping
	public List<User> users(
		@PathVariable("domainName") String domainName,
		@RequestParam(required=false) String search,
    WebRequest webRequest,
		LithiumTokenUtil tokenUtil
	) {
    String mapAsString = webRequest.getParameterMap().keySet().stream()
        .map(key -> key + "=" + webRequest.getParameterValues(key)[0])
        .collect(Collectors.joining(", ", "{", "}"));

    log.debug("webRequest: "+webRequest+" : "+webRequest.getUserPrincipal()+" || parameters: "+ mapAsString);
    if (userListEndpointBlocked) {
      String ip = webRequest.getHeader("X-Forwarded-For");
      String ua = webRequest.getHeader("User-Agent");
      log.warn("Endpoint blocked because of property: lithium.endpoint.user-list.block. domainName: {}, search: {}, tokenUtil.guid: {}, ip: {}, ua: {},"
              + " parameters: {}", domainName, search, tokenUtil.guid(), ip, ua, mapAsString);
      return Collections.emptyList();
    }
		Specification<User> spec = null;
		
		List<Domain> domains = new ArrayList<>();
//		List<String> domainNames = util.domainsWithRole("ADMIN");
//		if (domainNames != null) {
//			//TODO retrieve domains out of the principal and compare / filter against the supplied domains
//			for (String domainName: domainNames) {
		Domain domain = domainService.findOrCreate(domainName);
		if (domain != null) domains.add(domain);
//			}
		spec = Specification.where(UserSpecifications.domainIn(domains));
//		}
		
		if ((search != null) && (!search.isEmpty())) {
			Specification<User> s = Specification.where(UserSpecifications.any(search));
			spec = (spec == null)? s: spec.and(s);
		}
		List<User> users = userService.findAll(spec);
		log.debug("users :: "+users);
		return users;
	}

	@GetMapping("/table")
	public DataTableResponse<lithium.service.user.client.objects.User> table(@PathVariable("domainName") String domainName,
																			 @RequestParam(name="domainNames", required=false) String[] domainNames,
																			 @RequestParam(name="players", required=false) Boolean players,
																			 @RequestParam(name="labelNameString", required=false) String labelNameString,
																			 @RequestParam(name="labelValueString", required=false) String labelValueString,
																			 DataTableRequest request,
                                       WebRequest webRequest,
																			 Principal principal) throws Exception {
		log.debug("Users table request " + request.toString());
    String mapAsString = webRequest.getParameterMap().keySet().stream()
        .map(key -> key + "=" + webRequest.getParameterValues(key)[0])
        .collect(Collectors.joining(", ", "{", "}"));

    log.debug("webRequest: "+webRequest+" : "+webRequest.getUserPrincipal()+" || parameters: "+ mapAsString);
    if (blockEndpoint) {
      log.warn("Endpoint blocked because of property: lithium.block.endpoint");
      return new DataTableResponse<>(request, Collections.emptyList());
    }
		
		lithium.service.domain.client.objects.Domain externalDomain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
		if (externalDomain == null) throw new Exception("No such domain");
		if (!externalDomain.getEnabled()) throw new Exception("Domain disabled");
		if (externalDomain.getDeleted()) throw new Exception("Domain does not exist");
		Domain domain = domainService.findOrCreate(domainName);
		LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, principal).build();
		
		Specification<User> spec = null;
		
		List<Domain> domains = new ArrayList<>();
		
		if (domainNames != null) {
			//TODO retrieve domains out of the principal and compare / filter against the supplied domains
			for (String n: domainNames) {
				Domain d = domainRepository.findByName(n);
				if (d != null) {
					if (tokenUtil.hasRole(n, "USER_VIEW")) {
						domains.add(d);
					}
				}
			}
		}
		
		domains.add(domain);
		
		if (domains.size() > 0) {
			spec = Specification.where(UserSpecifications.domainIn(domains));
		}
		
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<User> s = Specification.where(UserSpecifications.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}

		if (labelNameString != null) {
			Specification<User> s = Specification.where(UserSpecifications.userWithLabel(labelNameString, labelValueString));
			spec = (spec == null)? s: spec.and(s);
		}
		
		Page<User> userPageList = userService.findAll(spec, request.getPageRequest());
		Page<lithium.service.user.client.objects.User> resultPage = userPageList.map(userService::convertUser);
		
		return new DataTableResponse<>(request, resultPage);
	}
	

	

	@PostMapping
	public Response<User> create(@PathVariable("domainName") String domainName, @RequestBody PlayerBasic o, Principal principal) throws Exception {
		lithium.service.domain.client.objects.Domain externalDomain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
		if (externalDomain == null) throw new Exception("No such domain");
		if (!externalDomain.getEnabled()) throw new Exception("Domain disabled");
		if (externalDomain.getDeleted()) throw new Exception("Domain does not exist");
//		if (!externalDomain.getPlayers()) throw new Exception("This is not a player domain");
		Domain domain = domainService.findOrCreate(domainName);
		
		String ipAddress = request.getRemoteAddr();
		if (request.getHeader("X-Forwarded-For") != null) {
			ipAddress = request.getHeader("X-Forwarded-For");
		}
		String userAgent = request.getHeader("User-Agent");
		if (request.getHeader("User-Agent-Forwarded") != null) {
			userAgent = request.getHeader("User-Agent-Forwarded");
		}

		Map<String, String> ipAndUserAgentData = accessService.parseIpAndUserAgent(ipAddress, userAgent);
    String preSignupAccessRule = externalDomain.getPreSignupAccessRule();
    if (preSignupAccessRule != null && !preSignupAccessRule.isEmpty()) {
      AuthorizationResult authorizationResult =
          accessService.checkAuthorization(domainName, preSignupAccessRule, null, ipAndUserAgentData, null, null, false, o);
      log.info("authorizationResult of preSignupCheck " + authorizationResult);
      boolean authSuccessful = (authorizationResult != null)? authorizationResult.isSuccessful(): true;
      if (!authSuccessful) {
        signupEventService.saveSignupEvent(ipAndUserAgentData, domain, authorizationResult.getMessage(), 0, false);
        return Response.<User>builder().status(FORBIDDEN).message(authorizationResult.getErrorMessage()).build();
      }
    }

		if (!userService.isUniqueUsername(domainName, o.getUsername())) throw new Exception("The username is not unique");

		Status status = statusRepository.findByName(lithium.service.user.client.enums.Status.OPEN.statusName());
		User user = User.builder()
				.domain(domain)
				.username(o.getUsername().toLowerCase())
				.passwordHash(PasswordHashing.hashPassword(o.getPassword(), passwordSalt))
				.email(o.getEmail().toLowerCase())
				.emailValidated(false)
				.firstName(o.getFirstName())
				.lastName(o.getLastName())
				.countryCode(o.getCountryCode())
				.referrerGuid(o.getReferrerGuid())
				.createdDate(new Date())
				.updatedDate(new Date())
				.status(status)
				.build();
		userService.save(user);

    String signupAccessRule = externalDomain.getSignupAccessRule();
    if (signupAccessRule != null && !signupAccessRule.isEmpty()) {
      AuthorizationResult authorizationResult = accessService.checkAuthorization(domainName, signupAccessRule, null, ipAndUserAgentData);
      log.info("authorizationResult " + authorizationResult);
      boolean authSuccessful = (authorizationResult != null)? authorizationResult.isSuccessful(): true;
      if (!authSuccessful) {
        signupEventService.saveSignupEvent(ipAndUserAgentData, domain, authorizationResult.getMessage(), 0, false);
        return Response.<User>builder().status(FORBIDDEN).message(authorizationResult.getErrorMessage()).build();
      }
    }

		signupEventService.saveSignupEvent(ipAndUserAgentData, domain, null, user.getId(), true);

		List<ChangeLogFieldChange> clfc = changeLogService.copy(user, new User(),
				new String[] { "domain", "username", "email", "firstName", "lastName", "countryCode", "createdDate", "updatedDate" ,"referrerGuid"});

		changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "create", user.getId(), principal.getName(), tokenService.getUtil(principal),
        o.getComments(), null, clfc, Category.ACCOUNT, SubCategory.ACCOUNT_CREATION, 0, domainName);

    pubSubUserService.buildAndSendPubSubAccountCreate(user, PubSubEventType.ACCOUNT_CREATE);
		return Response.<User>builder().data(user).build();
	}

	@RequestMapping("/isunique")
	public Response<Boolean> isUnique(@PathVariable("domainName") String domain, @RequestParam("username") String username,
      @RequestParam(value = "id", required = false) User user) {
    if(ObjectUtils.isEmpty(user)){
      return Response.<Boolean>builder().data(userService.isUniqueUsername(domain.toLowerCase(), username.toLowerCase())).build();
    }

    if (user.getUsername().equals(username)) {
      return Response.<Boolean>builder().data(true).build();
    }
    return Response.<Boolean>builder().data(userService.isUniqueUsername(domain.toLowerCase(), username.toLowerCase(), user)).build();
	}
	
	//TODO: should maybe run this using paging
	@GetMapping("/usersByDomainAndLabel")
	public List<lithium.service.user.client.objects.User> usersByDomainAndLabel(
		@PathVariable("domainName") String domainName,
		@RequestParam("labelName") String labelName
	) {
		Specification<User> spec = null;

		spec = Specification.where(UserSpecifications.byDomainAndLabel(domainName, labelName));

		List<User> users = userService.findAll(spec);

		return users.stream()
        .map(userService::convertUser)
        .collect(Collectors.toList());
	}

  @RequestMapping(value = "/find-users-by-usernames-or-guids", method = RequestMethod.POST)
  Response<List<User>> findByGuids(@PathVariable("domainName") String domainName,  @RequestBody List<String> userGuids) {
    Specification<User> specifications = Specification.where(UserSpecifications.guidOrUsernameIn(userGuids));

    return Response.<List<User>>builder()
        .data(userService.findAll(specifications))
        .status(Response.Status.OK_SUCCESS)
        .build();
  }
}
