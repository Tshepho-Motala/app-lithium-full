package lithium.service.casino.api.frontend.controllers;

import lithium.exceptions.ErrorCodeException;
import lithium.service.Response;
import lithium.service.casino.api.backoffice.schema.ActiveBonusResponse;
import lithium.service.casino.api.frontend.schema.BonusHistoryResponse;
import lithium.service.casino.api.frontend.schema.BonusOptInResponse;
import lithium.service.casino.client.data.BonusOptInRequest;
import lithium.service.casino.exceptions.Status413NoValidBonusFoundForCodeException;
import lithium.service.casino.exceptions.Status414NoValidBonusRevisionFoundException;
import lithium.service.casino.exceptions.Status416BonusUptakeLimitExceededException;
import lithium.service.casino.service.BonusHistoryService;
import lithium.service.casino.service.BonusService;
import lithium.service.casino.service.CasinoTriggerBonusService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status416PlayerPromotionsBlockedException;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/frontend/bonus")
public class FrontendBonusController {

    @Autowired @Setter
    LithiumTokenUtilService tokenService;

    @Autowired
    BonusHistoryService bonusHistoryService;

    @Autowired
    BonusService bonusService;

    @Autowired
    CasinoTriggerBonusService casinoTriggerBonusService;

    @Autowired
    LimitInternalSystemService limitInternalSystemService;

    @GetMapping("/{bonusType}/find/active")
    public List<String> findActiveBonuses(Principal principal) {

        LithiumTokenUtil token = tokenService.getUtil(principal);
        return bonusService.getActiveCashBonuses(token.guid().split("/")[0]).stream().map(bonus -> bonus.getBonusCode()).collect(Collectors.toList());
    }

    @GetMapping("/history")
    public DataTableResponse<BonusHistoryResponse> getBonusesPerPlayerID(
            @RequestParam(name = "page", required = true) int page,
            @RequestParam(name = "sortBy", defaultValue = "startedDate") String sortBy,
            @RequestParam(name = "order", defaultValue = "DESC") String order,
            @RequestParam(name = "pageSize", defaultValue = "25") int pageSize,
            Principal principal) {

        LithiumTokenUtil token = tokenService.getUtil(principal);

        if (pageSize > 100) pageSize = 100;
        DataTableRequest request = new DataTableRequest();
        request.setPageRequest(PageRequest.of(page, pageSize, Sort.by(Direction.fromString(order), sortBy)));

        Page<BonusHistoryResponse> pbhPage = bonusHistoryService.findByPlayerBonusPlayerGuid(token.guid(), request);

        return new DataTableResponse<>(request, pbhPage);
    }

    @GetMapping("/find/active")
    public List<ActiveBonusResponse> findAllActiveBonuses(
            @RequestParam(name = "provider", required = false) String provider,
            @RequestParam(name = "campaignId", required = false) Long campaignId,
            Principal principal) {

        LithiumTokenUtil token = tokenService.getUtil(principal);

        return bonusService.getAllActiveBonuses(token.guid(), provider, campaignId);
    }

    @PostMapping("/complete")
    public Response<Boolean> completeBonus (
            @RequestParam(name = "playerBonusHistoryId") Long playerBonusHistoryId,
            Principal principal) {
        LithiumTokenUtil token = tokenService.getUtil(principal);
        try {
            return Response.<Boolean>builder()
                    .data(bonusService.markBonusComplete(playerBonusHistoryId, token))
                    .status(Response.Status.OK)
                    .build();
        } catch (Exception e) {
            log.error("Failed to mark Bonus as complete [id="+playerBonusHistoryId+" for userguid: "+token.guid()+"]" + e.getMessage(), e);
            return Response.<Boolean>builder()
                    .data(false)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }

    }

    @PostMapping("/opt-in")
    public ResponseEntity<String> registerForTriggerBonus(
            @RequestBody BonusOptInRequest bonusOptInRequest,
            LithiumTokenUtil tokenUtil) {
        log.info("/frontend/bonus/opt-in request (" + tokenUtil.guid() + ") :: " + bonusOptInRequest);

        //Check if the player can particapate in promotions
        try {
            limitInternalSystemService.checkPromotionsAllowed(tokenUtil.guid());
        } catch (Status416PlayerPromotionsBlockedException e) {
            log.debug("Player blocked from receiving promotions | userGuid: " + tokenUtil.guid());
            return ResponseEntity.status(414).body("User not eligible for bonus opt-in.");
        }

        try {
            casinoTriggerBonusService.processTriggerBonusOptIn(bonusOptInRequest.getBonusCode(), tokenUtil);
        } catch (Exception e) {

            if (e instanceof Status416BonusUptakeLimitExceededException) {
                log.debug("Player already opted in to bonusCode: " + bonusOptInRequest.getBonusCode() + ", userGuid: " + tokenUtil.guid());
                return ResponseEntity.status(HttpStatus.OK).body("The bonus was previously triggered and indicates the player is opted in.");
            }

            if (e instanceof Status413NoValidBonusFoundForCodeException || e instanceof Status414NoValidBonusRevisionFoundException) {
                log.debug("Bonus code not found for bonusCode: " + bonusOptInRequest.getBonusCode() + ", userGuid: " + tokenUtil.guid());
                return ResponseEntity.status(413).body("An invalid bonus code was used, please check that you are using the correct bonus code.");
            }

            ErrorCodeException errorCodeException = (ErrorCodeException) e;
            if (errorCodeException.getCode() >= 400 && errorCodeException.getCode() < 500) {
                log.debug("Player not eligible for bonus opt-in bonusCode: " + bonusOptInRequest.getBonusCode() + ", userGuid: " + tokenUtil.guid(), e);
                return ResponseEntity.status(414).body("User not eligible for bonus opt-in.");
            }

            log.error("Internal Server Error: Was unable to opt player into bonusCode: " + bonusOptInRequest.getBonusCode() + ", userGuid: " + tokenUtil.guid(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        log.debug("Player was opted in to bonusCode: " + bonusOptInRequest.getBonusCode() + ", userGuid: " + tokenUtil.guid());
        return ResponseEntity.status(HttpStatus.CREATED).body("The player was opted into the bonus.");
    }

    @PostMapping("/check-opt-in")
    public ResponseEntity<BonusOptInResponse> checkTrigerBonusExist(
            @RequestBody BonusOptInRequest bonusOptInRequest,
            LithiumTokenUtil tokenUtil) {
        log.info("/frontend/bonus/check-opt-in request (" + tokenUtil.guid() + ") :: " + bonusOptInRequest);

        //Check if the player can particapate in promotions
        try {
            limitInternalSystemService.checkPromotionsAllowed(tokenUtil.guid());
        } catch (Status416PlayerPromotionsBlockedException e) {
            log.debug("Player blocked from receiving promotions | userGuid: " + tokenUtil.guid());
            return ResponseEntity.status(414).body(BonusOptInResponse.builder().message("User not eligible for bonus opt-in.").build());
        }

        try {
            return ResponseEntity.status(200).body(BonusOptInResponse.builder()
                        .bonusOptIn(casinoTriggerBonusService.isTriggerBonusOptIn(bonusOptInRequest.getBonusCode(), tokenUtil.guid()))
                        .build());
        } catch (Status413NoValidBonusFoundForCodeException e) {
            log.debug("Bonus code not found for bonusCode: " + bonusOptInRequest.getBonusCode() + ", userGuid: " + tokenUtil.guid());
            return ResponseEntity.status(413).body(BonusOptInResponse.builder().message("An invalid bonus code was used, please check that you are using the correct bonus code.").build());
        }
    }
}
