package lithium.service.user.controllers;

import lithium.service.user.data.entities.User;
import lithium.service.user.enums.TokenType;
import lithium.service.user.enums.TokenTypeConverter;
import lithium.service.user.enums.Type;
import lithium.service.user.enums.TypeConverter;
import lithium.exceptions.Status404UserNotFoundException;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import lithium.service.user.services.PasswordResetService;
import lithium.service.user.services.UserService;
import lithium.service.user.services.UserValidationBaseService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/{domainName}/profile/password-reset")
public class BackofficePasswordResetController extends UserValidationBaseService {
    @Autowired
    UserService userService;
    @Autowired
    PasswordResetService passwordResetService;
    @Autowired
    MessageSource messageSource;

    @PostMapping
    public ResponseEntity passwordReset(
            @PathVariable("domainName") String domainName,
            @RequestParam String playerGuid,
            @RequestParam(defaultValue = "email") Type type,
            @RequestParam(name = "token", defaultValue = "n") TokenType tokenType,
            @RequestParam(defaultValue = "5") Integer tokenLength,
            LithiumTokenUtil token
    ) throws Status404UserNotFoundException, Status500InternalServerErrorException {

        if (token == null) throw  new Status500InternalServerErrorException("Invalid Token");
        User user = userService.findFromGuid(playerGuid);
        if (user == null) throw new Status404UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.PASSWORD.USER_NOT_FOUND", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "User not found or invalid user guid.", LocaleContextHolder.getLocale()));

        if (type == null) type = Type.EMAIL;
        if (tokenType == null) tokenType = TokenType.NUMERIC;

        try {
            String dateOfBirth = user.getDobDay()+"/"+user.getDobMonth()+"/"+user.getDobYear();
            passwordResetService.step1(
              user,
              token.guid(),
              domainName,
              user.getEmail(),
              user.getUsername(),
              user.getCellphoneNumber(),
              type,
              tokenType,
              tokenLength,
              dateOfBirth,
              token
            );
        } catch (Exception e) {
            log.error("BackofficePasswordReset " + ExceptionMessageUtil.allMessages(e), e);
            throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e));
        }
        return ResponseEntity.ok().build();
    }

    @InitBinder
    public void initBinder(final WebDataBinder webdataBinder) {
        webdataBinder.registerCustomEditor(Type.class, new TypeConverter());
        webdataBinder.registerCustomEditor(TokenType.class, new TokenTypeConverter());
    }
}
