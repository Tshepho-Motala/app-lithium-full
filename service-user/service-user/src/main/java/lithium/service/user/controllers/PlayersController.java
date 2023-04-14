package lithium.service.user.controllers;

import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.access.client.AccessService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.DomainRevisionLabelValue;
import lithium.service.user.client.objects.IncompleteUserBasic;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.PlayerRegistrationMethods;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.DomainRepository;
import lithium.service.user.data.repositories.IncompleteUserRepository;
import lithium.service.user.data.repositories.UserCategoryRepository;
import lithium.service.user.services.DomainService;
import lithium.service.user.services.IncompleteUserService;
import lithium.service.user.services.SignupEventService;
import lithium.service.user.services.SignupService;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.WeightedCollection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/players")
public class PlayersController {
	@Autowired DomainRepository domainRepository;
	@Autowired UserCategoryRepository userCategoryRepository;
	@Autowired TokenStore tokenStore;
	@Autowired LithiumServiceClientFactory factory;
	@Autowired DomainService domainService;
	@Autowired SignupEventService signupEventService;
	@Autowired AccessService accessService;
	@Autowired HttpServletRequest request;
	@Autowired SignupService signupService;
	@Autowired IncompleteUserRepository incompletUsersRepository;
	@Autowired ModelMapper modelMapper;
	@Autowired CachingDomainClientService cachingDomainClientService;
	@Autowired UserService userService;
  @Autowired private MessageSource messageSource;
  @Autowired private IncompleteUserService incompleteUserService;

	@GetMapping(path="/search")
	public Response<List<User>> list(
		@RequestParam(name="search") String search,
		LithiumTokenUtil tokenUtil
	) {
	  List<String> domains = tokenUtil.playerDomainsWithRole("PLAYER_VIEW").stream().map(jwtDomain -> jwtDomain.getName()).collect(Collectors.toList());
		List<User> players = userService.findByDomainNameInAndUsernameOrFirstNameOrLastNameOrEmail(domains, search);
		log.debug("players :: "+players);

		return Response.<List<User>>builder().status(OK).data(players).build();
	}

  @Deprecated
	@PostMapping("/{domainName}/register/incomplete/v1")
  /**
   * Should now use new endpoint in {@code FrontendPlayersController}
   */
	public Response<PlayerBasic> createIncomplete(
	    @PathVariable("domainName") String domainName,
      @RequestBody IncompleteUserBasic p,
      @RequestParam(required = false) String method,
      @RequestParam(required = false) String sha,
      @RequestParam(name = "apiAuthorizationId", defaultValue = "ls-gw", required = false) String apiAuthorizationId,
      @RequestHeader("Authorization") String authorization)
      throws Exception {
    if (method != null && !method.isEmpty() && !method.equalsIgnoreCase(PlayerRegistrationMethods.IDIN.getMethod())){
      Status methodNotFound = NOT_FOUND.message("Method Not Found");
      return Response.<PlayerBasic>builder().status(methodNotFound).message(methodNotFound.message()).build();
    }
    return incompleteUserService.registerIncompleteUser(domainName, p, sha, apiAuthorizationId, authorization);
  }
	
	@GetMapping("/{domainName}/find/incomplete")
	public Response<PlayerBasic> findIncomplete(@PathVariable("domainName") String domainName, @RequestParam("incompleteId") Long incompleteUserId) throws Exception {
		PlayerBasic pb = incompleteUserService.findIncompleteUser(incompleteUserId);
		if (pb == null) {
			return Response.<PlayerBasic>builder().status(Response.Status.NOT_FOUND).build();
		}
		return Response.<PlayerBasic>builder().status(OK).data(pb).build();
	}

	/** Deprecated in favor of api.frontend.controllers.RegisterController because of the declarative error handling */
	@Deprecated
	@PostMapping("/{domainName}/register")
	public Response<User> create(@PathVariable("domainName") String domainName, @RequestBody PlayerBasic p) throws Exception {
		try {
			p.setDomainName(domainName);
			return Response.<User>builder().data(signupService.registerPlayer(p, request)).status(OK).build(); //From register V1 flow
		} catch (Exception e) {
			// Have to do this to match the original API that only returned error 500 codes.
			throw new Exception(e.getMessage(), e);
		}
	}
	
	@GetMapping("/{domainName}/isunique")
	public Response<Boolean> isUnique(@PathVariable("domainName") String domain, @RequestParam("username") String username) {
		return Response.<Boolean>builder().data(userService.isUniqueUsername(domain, username, false)).build();
	}
	
	@GetMapping("{domainName}/isemailunique")
	public Response<Boolean> isEmailUnique(@PathVariable("domainName") String domain, @RequestParam("email") String email) {
		return Response.<Boolean>builder().data(userService.isUniqueEmail(domain, email, false)).build();
	}
	
	@PostMapping("{domainName}/ismobileunique")
	public Response<Boolean> isMobileUnique(@PathVariable("domainName") String domain, @RequestParam("mobile") String mobile) {
		return Response.<Boolean>builder().data(userService.isUniqueMobile(domain, mobile)).build();
	}
  /**
   *
   * @param domainName
   * @param password
   * @return
   */
  @PostMapping("{domainName}/is-password-ok")
  public Response<Boolean> isPasswordOk(
          @PathVariable("domainName") String domainName,
          @RequestParam("password") String password) throws Status550ServiceDomainClientException {

      Domain domain = domainRepository.findByName(domainName);
      if (domain == null) {
        throw new Status550ServiceDomainClientException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.NO_SUCH_DOMAIN", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "Unable to retrieve domain from domain service.", LocaleContextHolder.getLocale()));
      }
    return Response
              .<Boolean>builder()
              .data(userService.isPasswordOk(password))
              .build();
  }

  /**
   *
   * @param domainName
   * @param parameters
   * @return
   */
  @PostMapping("{domainName}/is-password-ok/v2")
  public Response<Boolean> isPasswordOkV2(
      @PathVariable("domainName") String domainName,
      @RequestParam Map<String, String> parameters) throws Status550ServiceDomainClientException {
    String password = parameters.get("password");
    if (StringUtils.isEmpty(password)) {
      return Response.<Boolean>builder().data(false).build();
    }
    Domain domain = domainRepository.findByName(domainName);
    if (domain == null) {
      throw new Status550ServiceDomainClientException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.NO_SUCH_DOMAIN",
          new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "Unable to retrieve domain from domain service.",
          LocaleContextHolder.getLocale()));
    }
    return Response.<Boolean>builder().data(userService.isPasswordOk(password)).build();
  }

	/**
	 * Responds with the user signup journey type that should be used by the signup process.
	 * The domain settings in UNA can be provided with a configuration to allow weighting per journey
	 * Label to use: signupUserJourneyType
	 * Value example: onepage:60,multipage:40...
	 * @param domain
	 * @return String value identifying the signup type flow that should be followed
	 */
	@GetMapping("{domainName}/signupjourneytype")
	public Response<String> signupJourneyType(@PathVariable("domainName") String domain) {
		String[] signupJourneyType = {""};
		try {
			List<DomainRevisionLabelValue> labelValueList = cachingDomainClientService.retrieveDomainFromDomainService(domain).getCurrent().getLabelValueList();
			Optional<DomainRevisionLabelValue> dlv = labelValueList.stream().filter(lv -> {
				if (("signupUserJourneyType").equalsIgnoreCase(lv.getLabelValue().getLabel().getName())) {
					return true;
				}
				return false;
			}).findFirst();
			dlv.ifPresent(d -> {
				String[] journeyOptions = d.getLabelValue().getValue().split(",");

				if (journeyOptions.length > 0) {
					WeightedCollection<String> weightedCollection = new WeightedCollection();
					for (int p = 0; p < journeyOptions.length; ++p) {
						String[] kv = journeyOptions[p].split(":");
						weightedCollection.add(Integer.parseInt(kv[1]), kv[0]);
					}
					signupJourneyType[0] = weightedCollection.next();
				} else {
					//Nothing to be done here
					log.debug("No signupUserJourneyType domain settings configuration for: " + domain);
				}
			});
		} catch (Exception ex) {
			log.warn("Failed while trying to read signupUserJourneyType domain setting for: " + domain, ex);
		}
//		ArrayList<String> journeyType = new ArrayList<>();
//		journeyType.add("onepage");
//		journeyType.add("multipage");

		return Response.<String>builder().data(signupJourneyType[0]).build();
	}

    @PostMapping(path="/{domainName}/isfullnameunique")
    public Response<Boolean> isfullnameunique(
            @PathVariable("domainName") String domainName,
            @RequestParam(name="firstName") String firstName,
            @RequestParam(name="lastName") String lastName,
            @RequestParam(name="dobDay") int dobDay,
            @RequestParam(name="dobMonth") int dobMonth,
            @RequestParam(name="dobYear") int dobYear
    ) {
        List<User> players = userService.findByDomainNameAndFirstNameAndLastNameAndBirthDay(domainName, firstName, lastName, dobDay, dobMonth, dobYear);
        return Response.<Boolean>builder().status(OK).data(players.isEmpty()).build();
    }

}
