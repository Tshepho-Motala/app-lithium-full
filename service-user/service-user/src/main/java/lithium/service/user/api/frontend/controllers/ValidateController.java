package lithium.service.user.api.frontend.controllers;

import java.util.Collections;
import java.util.Set;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.Status404UserNotFoundException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.user.exceptions.Status406InvalidValidationTokenException;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import lithium.service.user.services.EmailValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@EnableCustomHttpErrorCodeExceptions
public class ValidateController {
    @Autowired EmailValidationService emailValidationService;
    @Autowired LocaleContextProcessor localeContextProcessor;

    @PostMapping("/frontend/{domainName}/validate/mail/1")
    public Set<String> step1(
        @PathVariable("domainName") String domainName,
        @RequestParam("eup") String emailOrUsername,
        @RequestParam(value = "locale", defaultValue = "en") String locale
    ) throws
        Status404UserNotFoundException,
        Status500InternalServerErrorException
    {
        localeContextProcessor.setLocaleContextHolder(locale, domainName);
        emailValidationService.step1(domainName, emailOrUsername);
        return Collections.singleton(emailOrUsername);
    }

    @PostMapping("/frontend/{domainName}/validate/mail/2")
    public Set<String> step2(
        @PathVariable("domainName") String domainName,
        @RequestParam("validationToken") String validationToken,
        @RequestParam("eup") String emailOrUsername,
        @RequestParam(value = "locale", defaultValue = "en") String locale
    ) throws
        Status404UserNotFoundException,
        Status406InvalidValidationTokenException,
        Status500InternalServerErrorException
    {
        localeContextProcessor.setLocaleContextHolder(locale, domainName);
        emailValidationService.step2(domainName, emailOrUsername, validationToken);
        return Collections.singleton(validationToken);
    }
}
