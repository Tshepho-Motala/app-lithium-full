package lithium.service.user.api.frontend.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.Status403AccessDeniedException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.data.entities.User;
import lithium.service.user.exceptions.Status420InvalidEmailException;
import lithium.service.user.exceptions.Status421InvalidCellphoneException;
import lithium.service.user.exceptions.Status422InvalidDateOfBirthException;
import lithium.service.user.exceptions.Status451UnderageException;
import lithium.service.user.exceptions.Status452UsernameNotUniqueException;
import lithium.exceptions.Status453EmailNotUniqueException;
import lithium.service.user.exceptions.Status454CellphoneNotUniqueException;
import lithium.service.user.exceptions.Status461UserNotUniqueException;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import lithium.service.user.services.SignupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@EnableCustomHttpErrorCodeExceptions
public class RegisterController {
    @Autowired
    SignupService signupService;
    @Autowired
    LocaleContextProcessor localeContextProcessor;

    @PostMapping("/frontend/{domainName}/register/v2")
    public User create(
            @PathVariable("domainName") String domainName,
            @Valid @RequestBody PlayerBasic p,
            @RequestParam(value = "locale", required = false) String locale,
            HttpServletRequest request
    ) throws
        Status403AccessDeniedException,
        Status422InvalidDateOfBirthException,
        Status426InvalidParameterProvidedException,
        Status451UnderageException,
        Status452UsernameNotUniqueException,
        Status453EmailNotUniqueException,
        Status454CellphoneNotUniqueException,
        Status500InternalServerErrorException,
        Status550ServiceDomainClientException,
        Status551ServiceAccessClientException,
        Status421InvalidCellphoneException,
        Status420InvalidEmailException,
        Status461UserNotUniqueException {
        p.setDomainName(domainName);
        localeContextProcessor.setLocaleContextHolder(locale, domainName);
        p.setUsername(p.getUsername() != null ? p.getUsername() : System.nanoTime() +"");
        return signupService.registerV2Player(p, request);
    }
}
