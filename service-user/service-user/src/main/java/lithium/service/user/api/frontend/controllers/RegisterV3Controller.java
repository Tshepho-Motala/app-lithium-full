package lithium.service.user.api.frontend.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status403AccessDeniedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status407IpBlockedException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status432UserExistsInAnotherExclusiveDomainException;
import lithium.exceptions.Status447AccountFrozenException;
import lithium.exceptions.Status453EmailNotUniqueException;
import lithium.exceptions.Status460LoginRestrictedException;
import lithium.exceptions.Status465DomainUnknownCountryException;
import lithium.exceptions.Status492ExcessiveFailedLoginBlockException;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.exceptions.Status420InvalidEmailException;
import lithium.service.user.exceptions.Status421InvalidCellphoneException;
import lithium.service.user.exceptions.Status422InvalidDateOfBirthException;
import lithium.service.user.exceptions.Status431UserExistsInEcosystemException;
import lithium.service.user.exceptions.Status451UnderageException;
import lithium.service.user.exceptions.Status452UsernameNotUniqueException;
import lithium.service.user.exceptions.Status454CellphoneNotUniqueException;
import lithium.service.user.exceptions.Status461UserNotUniqueException;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import lithium.service.user.services.SignupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@EnableCustomHttpErrorCodeExceptions
@Validated
public class RegisterV3Controller {
    @Autowired
    SignupService signupService;
    @Autowired
    LocaleContextProcessor localeContextProcessor;

    @PostMapping("/frontend/{domainName}/register/v3")
    public OAuth2AccessToken create(
            @PathVariable("domainName") String domainName,
            @Valid @RequestBody PlayerBasic p,
            HttpServletRequest request,
            @RequestParam(value = "locale", required = false) String locale,
            @RequestHeader ("Authorization") String authorization
    ) throws
        Status401UnAuthorisedException,
        Status403AccessDeniedException,
        Status405UserDisabledException,
        Status407IpBlockedException,
        Status422InvalidDateOfBirthException,
        Status426InvalidParameterProvidedException,
        Status431UserExistsInEcosystemException,
        Status432UserExistsInAnotherExclusiveDomainException,
        Status447AccountFrozenException,
        Status451UnderageException,
        Status452UsernameNotUniqueException,
        Status453EmailNotUniqueException,
        Status454CellphoneNotUniqueException,
        Status460LoginRestrictedException,
        Status461UserNotUniqueException,
        Status490SoftSelfExclusionException,
        Status491PermanentSelfExclusionException,
        Status492ExcessiveFailedLoginBlockException,
        Status496PlayerCoolingOffException,
        Status500InternalServerErrorException,
        Status550ServiceDomainClientException,
        Status500LimitInternalSystemClientException,
        lithium.exceptions.Status500InternalServerErrorException,
        Status551ServiceAccessClientException,
        Status421InvalidCellphoneException,
        Status420InvalidEmailException,
        Status465DomainUnknownCountryException {
        p.setDomainName(domainName);
        localeContextProcessor.setLocaleContextHolder(locale, domainName);
        p.setUsername(p.getUsername() != null ? p.getUsername() : System.nanoTime() +"");
        return signupService.registerV3Player(p, request, authorization);
    }
}
