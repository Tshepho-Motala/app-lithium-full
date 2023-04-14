package lithium.service.reward.controller.backoffice;

import lithium.service.limit.client.exceptions.Status416PlayerPromotionsBlockedException;
import lithium.service.reward.client.dto.GiveRewardContext;
import lithium.service.reward.client.dto.GiveRewardRequest;
import lithium.service.reward.client.dto.GiveRewardResponse;
import lithium.service.reward.client.exception.Status505UnavailableException;
import lithium.service.reward.service.GiveRewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping( "/backoffice/{domainName}" )
public class BackofficeGiveRewardController{

    private final GiveRewardService giveRewardService;
    private final ModelMapper modelMapper;

    @PostMapping( "/reward/v1" )
    public GiveRewardResponse giveReward(
            @PathVariable("domainName") String domainName,
            @RequestParam(name = "locale", defaultValue = "en", required = false) String locale,
            @RequestBody GiveRewardRequest giveRewardRequest) throws Exception {
        GiveRewardContext context = GiveRewardContext.builder()
                .modelMapper(modelMapper)
                .locale(locale)
                .giveRewardRequest(giveRewardRequest)
                .build();

        context.addLog("Processing give reward");

        try {
            giveRewardService.giveReward(context);
        } catch (Exception e) {
            log.error( context.compileLog(MessageFormat.format("An error has occurred while granting reward, {0}", e.getMessage())), e);
            throw  e;
        }

        return context.getGiveRewardResponse();
    }
}
