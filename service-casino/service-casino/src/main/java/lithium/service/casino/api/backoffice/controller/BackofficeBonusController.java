package lithium.service.casino.api.backoffice.controller;

import lithium.service.Response;
import lithium.service.casino.api.backoffice.schema.ActiveBonusResponse;
import lithium.service.casino.client.data.BonusAllocatev2;
import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.service.BonusService;
import lithium.service.casino.service.CasinoTriggerBonusService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static lithium.service.Response.Status.OK;

@Slf4j
@RestController
@RequestMapping("/backoffice/{domainName}/bonus/{bonusType}")
public class BackofficeBonusController {

    @Autowired
    BonusService bonusService;

    @Autowired
    CasinoTriggerBonusService casinoTriggerBonusService;

    @Autowired
    LimitInternalSystemService limitInternalSystemService;

    @Autowired
    LocaleContextProcessor localeContextProcessor;

    @GetMapping("/find/active")
    public List<ActiveBonusResponse> findActiveBonuses(@PathVariable("domainName") String domainName) {
        return bonusService.getActiveCashBonuses(domainName);
    }

    @PostMapping("/history")
    public DataTableResponse<PlayerBonusHistory> getBonusesTable(
            @PathVariable("bonusType") String bonusType,
            @RequestParam(name="playerGuid", required=true) String playerGuid,
            @RequestParam(name="dateRangeStart", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date dateRangeStart,
            @RequestParam(name="dateRangeEnd", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date dateRangeEnd,
            @RequestParam(name="bonusCodes[]", required = false) String bonusCodes,
            DataTableRequest request
    ) {
        if(request == null) {
            PageRequest pageRequest = PageRequest.of(0, 25, Sort.Direction.DESC, "startedDate");
            request = new DataTableRequest();
            request.setPageRequest(pageRequest);
        }

        request.setPageRequest(PageRequest.of(request.getPageRequest().getPageNumber(),
                request.getPageRequest().getPageSize() > 100 ? 100 : request.getPageRequest().getPageSize(),
                request.getPageRequest().getSort()));

        Page<PlayerBonusHistory> pbhPage;
        if (bonusCodes != null && bonusCodes != "") { //If there are no bonus codes selected, return an empty page
            pbhPage= bonusService.find(playerGuid, bonusCodes.split(","), dateRangeStart, dateRangeEnd, request);
        } else {
            pbhPage = new SimplePageImpl<>(new ArrayList<>(), 0, 1, 0);
        }

        return new DataTableResponse<>(request, pbhPage);
    }

    @PostMapping("/manual/register/trigger/v3")
    public Response<Long> registerForTriggerBonusv3(
            @RequestBody BonusAllocatev2 bonusAllocatev2,
            LithiumTokenUtil tokenUtil,
            @RequestParam(value = "locale", required = false) String locale) throws Exception {
        log.debug("/manual/register/trigger/v3 (post) request ("+bonusAllocatev2.getPlayerGuid()+") :: "+bonusAllocatev2);

        localeContextProcessor.setLocaleContextHolder(locale, tokenUtil.domainName());

        //Check if the player can particapate in promotions
        limitInternalSystemService.checkPromotionsAllowed(bonusAllocatev2.getPlayerGuid());

        casinoTriggerBonusService.processTriggerOrTokenBonusWithCustomMoney(bonusAllocatev2, BonusRevision.BONUS_TYPE_TRIGGER, tokenUtil);
        return Response.<Long>builder().status(OK).build();
    }
}
