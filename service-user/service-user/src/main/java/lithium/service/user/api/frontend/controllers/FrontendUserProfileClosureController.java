package lithium.service.user.api.frontend.controllers;

import static java.util.Objects.isNull;
import static lithium.service.Response.Status.OK;

import java.util.List;
import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
import lithium.exceptions.Status404UserNotFoundException;
import lithium.service.Response;
import lithium.service.limit.client.objects.AutoRestrictionTriggerData;
import lithium.service.limit.client.stream.AutoRestrictionTriggerStream;
import lithium.service.user.client.objects.UserAccountStatusUpdate;
import lithium.service.user.controllers.UserController;
import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.StatusReason;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.projections.ClosureReasonProjection;
import lithium.service.user.data.repositories.ClosureReasonRepository;
import lithium.service.user.data.repositories.StatusReasonRepository;
import lithium.service.user.data.repositories.StatusRepository;
import lithium.service.user.exceptions.Status458AccountAlreadyClosedException;
import lithium.service.user.exceptions.Status459InvalidReasonIdException;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FrontendUserProfileClosureController {
    @Autowired
    private ClosureReasonRepository closureReasonRepository;
    @Autowired
    private UserController userController;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private StatusReasonRepository statusReasonRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AutoRestrictionTriggerStream autoRestrictionTriggerStream;
    @Autowired
    private MessageSource messageSource;

    @GetMapping("/profile/closure-reasons") //FIXME: should be /frontend/profile/closure-reasons since this is only used by frontend
    public List<ClosureReasonProjection> closureReasons(LithiumTokenUtil tokenUtil) {
        return closureReasonRepository.findAllByDomainNameAndDeleted(tokenUtil.domainName(), false);
    }

    //FIXME: Move business logic to service
    @PostMapping("/profile/close") //FIXME: should be /frontend/profile/close since this is only used by frontend
    private Response close(@RequestParam Long reasonId, LithiumTokenUtil tokenUtil) throws Exception {
        ClosureReasonProjection closureReason = closureReasonRepository.findByIdAndDomainNameAndDeletedFalse(reasonId, tokenUtil.domainName());
        if (isNull(closureReason)) {
            log.warn("Invalid closure reason id (" + reasonId + ", " + tokenUtil.domainName() + ")");
            throw new Status459InvalidReasonIdException("Invalid closure reason id (" + reasonId + ", " + tokenUtil.domainName() + ")");
        }
        Long userId = tokenUtil.getJwtUser().getId();
        User user = userService.findOne(userId);
        if (isNull(user)) {
            throw new Status404UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_ID_NOT_FOUND", new Object[] {new lithium.service.translate.client.objects.Domain(tokenUtil.domainName()), userId}, "User with id={0} not found.", LocaleContextHolder.getLocale()));
        }
        if (!user.getStatus().getUserEnabled()) {
            log.warn("Account already closed (" + user.getGuid() + "," + user.getStatus());
            throw new Status458AccountAlreadyClosedException("Account already closed (" + user.getGuid() + "," + user.getStatus());
        }
        Status status = statusRepository.findByName(lithium.service.user.client.enums.Status.BLOCKED.statusName());
        StatusReason reason = statusReasonRepository.findByName(lithium.service.user.client.enums.StatusReason.PLAYER_REQUEST.statusReasonName());
        userService.changeUserStatus(
            UserAccountStatusUpdate.builder()
            .userGuid(user.guid())
            .authorGuid(tokenUtil.guid())
            .statusName(status.getName())
            .statusReasonName((reason == null)? "": reason.getName())
            .comment("Account was closed by player with the following reason:" + closureReason.getText() + "(" + closureReason.getId() + ")")
            .noteCategoryName(Category.ACCOUNT.getName())
            .noteSubCategoryName(SubCategory.CLOSURE.getName())
            .notePriority(70)
            .build(),
            tokenUtil
        );

        // Triggering auto-restrictions on user status changes
        autoRestrictionTriggerStream.trigger(AutoRestrictionTriggerData.builder().userGuid(user.guid()).build());

        return Response.builder().status(OK).build();
    }

}
