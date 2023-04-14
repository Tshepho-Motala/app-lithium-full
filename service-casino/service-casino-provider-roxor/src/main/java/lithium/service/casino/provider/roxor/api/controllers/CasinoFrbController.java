package lithium.service.casino.provider.roxor.api.controllers;

import lithium.service.casino.client.CasinoFrbClient;
import lithium.service.casino.client.objects.request.AwardBonusRequest;
import lithium.service.casino.client.objects.request.CancelBonusRequest;
import lithium.service.casino.client.objects.request.CheckBonusRequest;
import lithium.service.casino.client.objects.request.GameBetConfigRequest;
import lithium.service.casino.client.objects.request.GetBonusInfoRequest;
import lithium.service.casino.client.objects.response.AwardBonusResponse;
import lithium.service.casino.client.objects.response.CancelBonusResponse;
import lithium.service.casino.client.objects.response.CheckBonusResponse;
import lithium.service.casino.client.objects.response.GameBetConfigResponse;
import lithium.service.casino.client.objects.response.GetBonusInfoResponse;
import lithium.service.casino.client.objects.response.UpdateBonusIdResponse;
import lithium.service.casino.provider.roxor.services.GrantRewardService;
import lithium.service.limit.client.LimitInternalSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
public class CasinoFrbController implements CasinoFrbClient {
    @Autowired GrantRewardService grantRewardService;
    @Autowired LimitInternalSystemService limitInternalSystemService;

    @Override
    @RequestMapping(value = "/casino/frb/awardbonus", method = POST)
    public AwardBonusResponse handleAwardBonus(@RequestBody AwardBonusRequest request) throws Exception {
        log.info("awardBonus request : " + request);

        //Checking for promotions eligibility before proceeding
        limitInternalSystemService.checkPromotionsAllowed(request.getUserId());

        AwardBonusResponse awardBonusResponse =  grantRewardService.grantReward(
                request
        );

        log.info("awardBonus response for request  : " + request + " result : " + awardBonusResponse);
        return awardBonusResponse;
    }

    @Override
    public CheckBonusResponse handleCheckBonus(CheckBonusRequest request) throws Exception {
        return null;
    }

    @Override
    public CancelBonusResponse handleCancelBonus(CancelBonusRequest request) throws Exception {
        return null;
    }

    @Override
    public GetBonusInfoResponse handleGetBonusInfo(GetBonusInfoRequest request) throws Exception {
        return null;
    }

    @Override
    public GameBetConfigResponse handleGetGameBetConfig(GameBetConfigRequest request) throws Exception {
        return null;
    }

    @Override
    public void updateExternalBonusId(UpdateBonusIdResponse request) {

    }
}
