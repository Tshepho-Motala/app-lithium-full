package lithium.service.reward.provider.casino.blueprint.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lithium.service.reward.client.dto.RewardRevisionTypeGame;
import lithium.service.reward.provider.casino.blueprint.config.ProviderConfig;
import lithium.service.reward.provider.casino.blueprint.dto.BlueprintRewardCancelRequest;
import lithium.service.reward.provider.casino.blueprint.dto.BlueprintRewardRequest;
import lithium.service.reward.provider.casino.blueprint.dto.BlueprintRewardResponse;
import lithium.service.reward.provider.casino.blueprint.enums.BlueprintErrorCode;
import lithium.service.reward.provider.casino.blueprint.enums.RewardTypeFieldName;
import lithium.service.reward.provider.casino.blueprint.services.BlueprintRewardService;
import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardStatus;
import lithium.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlueprintRewardServiceImpl implements BlueprintRewardService {
    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public ProcessRewardResponse awardFreeSpins(ProcessRewardRequest processRewardRequest, ProviderConfig providerConfig) {

        ProcessRewardResponse processRewardResponse = ProcessRewardResponse.builder()
                .build();

        String errorFormattedString = "Failed to award external blueprint freespins to user:{}, numbersOfFreeSpins:{}, coinValue:{}, reason: {}";
        String url;

        BlueprintRewardRequest awardRequest = BlueprintRewardRequest.builder().build();
        BlueprintRewardResponse blueprintRewardResponse;
        BlueprintErrorCode blueprintErrorCode;

        long amountInCents = processRewardRequest.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class);
        long numberOfSpins = processRewardRequest.findRewardTypeValue(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName(), Long.class);
        long affectedAmounts = numberOfSpins * amountInCents;

        try {

            awardRequest = buildAwardFreeSpinRewardRequest(processRewardRequest, providerConfig);
            String requestBody  = xmlMapper.writeValueAsString(awardRequest);

            log.debug("Blueprint award request {}", requestBody);
            url = MessageFormat.format("{0}/BPAddSingleUserPromo", providerConfig.getRewardsBaseUrl());

            blueprintRewardResponse =  execute(url, requestBody);
            blueprintErrorCode = BlueprintErrorCode.fromErrorCode(blueprintRewardResponse.getError());

            if (blueprintRewardResponse.isSuccess() && blueprintErrorCode == null) {

                log.debug("Blueprint free spins were successfully granted to user:{}, numberOfSpins:{}, coinValue:{}, reference: {}"
                        , processRewardRequest.getPlayer().getGuid(), awardRequest.getNumberOfFreeSpins(), awardRequest.getCoinValue(),blueprintRewardResponse.getPromotionId());

                processRewardResponse.setStatus(ProcessRewardStatus.SUCCESS);
                processRewardResponse.setAmountAffected(affectedAmounts);
                processRewardResponse.setValueUsed(0L);
                processRewardResponse.setValueGiven(numberOfSpins);
                processRewardResponse.setValueInCents(amountInCents);
                processRewardResponse.setExternalReferenceId(blueprintRewardResponse.getPromotionId());
                return processRewardResponse;
            }

            log.error(errorFormattedString,processRewardRequest.getPlayer().getGuid(),
                    awardRequest.getNumberOfFreeSpins(), awardRequest.getCoinValue(), blueprintErrorCode.message());
        } catch (Exception e) {
            log.error(errorFormattedString, processRewardRequest.getPlayer().getGuid(),
                    awardRequest.getNumberOfFreeSpins(), awardRequest.getCoinValue(), e);
        };

        return processRewardResponse;
    }

    @Override
    public CancelRewardResponse cancelFreeSpins(CancelRewardRequest cancelRequest, ProviderConfig providerConfig) {

        BlueprintRewardCancelRequest blueprintRewardCancelRequest = BlueprintRewardCancelRequest.builder()
                .apiToken(providerConfig.getRewardApiToken())
                .promotionId(cancelRequest.getReferenceId())
                .build();

        CancelRewardResponse cancelRewardResponse = CancelRewardResponse.builder().build();
        String errorFormattedString = "Failed to cancel reward component {} externally on Blueprint, free spins for user:{}, reference:{}, reason: {}";
        String url;

        BlueprintRewardResponse blueprintRewardResponse = null;
        BlueprintErrorCode blueprintErrorCode;

        try {

            String requestBody  = xmlMapper.writeValueAsString(blueprintRewardCancelRequest);

            log.debug("Blueprint cancel request {}", requestBody);
            url = MessageFormat.format("{0}/BPRemoveSingleUserPromo", providerConfig.getRewardsBaseUrl());

            log.debug(url);

            blueprintRewardResponse = execute(url, requestBody);
            blueprintErrorCode = BlueprintErrorCode.fromErrorCode(blueprintRewardResponse.getError());

            if (blueprintRewardResponse.isSuccess() && blueprintErrorCode == null) {
                log.debug("Blueprint free spins with reference:{} for user:{} were cancelled successfully on Blueprint", cancelRequest.getReferenceId(), cancelRequest.getPlayerGuid());
                cancelRewardResponse.setCode("0");
                return cancelRewardResponse;
            }

            //Logging known errors from Blueprint
            log.error(errorFormattedString, cancelRequest.getPlayerRewardTypeHistoryId(),cancelRequest.getPlayerGuid(),
                    cancelRequest.getReferenceId(), blueprintRewardResponse);

            cancelRewardResponse.setCode("-1");

        } catch (Throwable e) {
            cancelRewardResponse.setCode("-1");
           log.error(errorFormattedString, cancelRequest.getPlayerRewardTypeHistoryId(),cancelRequest.getPlayerGuid(),
                   cancelRequest.getReferenceId(), e);
        }


        return cancelRewardResponse;
    }


    BlueprintRewardResponse execute(String url, String requestBody) throws JsonProcessingException {
        HttpHeaders headers = getCommonHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response =  restTemplate.postForEntity(url, requestEntity, String.class);
        return xmlMapper.readValue(response.getBody(), BlueprintRewardResponse.class);

    }

    public BlueprintRewardRequest buildAwardFreeSpinRewardRequest(ProcessRewardRequest request, ProviderConfig config) {

        long amountInCents = request.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class);
        int numberOfSpins = request.findRewardTypeValue(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName(), Integer.class);

        final List<String> gameList = request.getRewardRevisionTypeGames().stream()
                .map(RewardRevisionTypeGame::getGameId)
                .collect(Collectors.toList());

        String identifier = generateExternalPlayerIdentifier(config, request.domainName(), Long.parseLong(request.getPlayer().getOriginalId()));
        String promotionId = String.valueOf(request.getPlayerRewardTypeHistoryId());

        return BlueprintRewardRequest.builder()
                .coinValue(amountInCents)
                .numberOfFreeSpins(numberOfSpins)
                .apiToken(config.getRewardApiToken())
                .brandId(config.getBrandId())
                .countryCode(config.getCountryCode())
                .currencyCode(request.domainCurrency())
                .isTestPlayer(request.getPlayer().isTestAccount())
                .jurisdiction(config.getJurisdiction())
                .promotionId(promotionId)
                .games(gameList)
                .playerId(identifier)
                .build();
    }

    public String generateExternalPlayerIdentifier(ProviderConfig config, String domainName, Long playerId) {

        long id = playerId;

        if (!StringUtil.isEmpty(config.getPlayerOffset())) {
            id = id + Long.parseLong(config.getPlayerOffset());
        }

        return MessageFormat.format("{0}-{1}/{2}", config.getPlayerGuidPrefix(), domainName, String.valueOf(id));
    }

    public HttpHeaders getCommonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_XML_VALUE);
        headers.add("Accept", MediaType.APPLICATION_XML_VALUE);
        return headers;
    }
}
