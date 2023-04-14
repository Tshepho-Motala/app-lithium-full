package lithium.service.casino.provider.sportsbook.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lithium.exceptions.Status400BadRequestException;
import lithium.modules.ModuleInfo;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.provider.sportsbook.data.PlayerBonuses;
import lithium.service.casino.provider.sportsbook.data.SportsBetBonusResponse;
import lithium.service.casino.provider.sportsbook.data.SportsBetPlayerBonus;
import lithium.service.client.datatable.DataTableRequest;
import lithium.util.HmacSha256HashCalculator;
import lithium.util.StringUtil;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Data
@Slf4j
public class FreeBetServices {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired ProviderConfigService providerConfigService;
    @Autowired
    @Setter ModuleInfo moduleInfo;

    public TreeMap<Integer, List<SportsBetPlayerBonus>> getSportsBetBonuses(String playerId, String dateFrom, String dateTo, String status,
                                                                            DataTableRequest dataTableRequest, String domainName) throws Status400BadRequestException, Status512ProviderNotConfiguredException {
        ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

        ResponseEntity<SportsBetBonusResponse> responseObject = sportsBetGetFromExternalResource(config.getSportsFreeBetsUrl(), playerId, config.getBonusRestrictionKey());
        if(responseObject == null) {
            throw new Status400BadRequestException("Failed to get player bonuses");
        }

        TreeMap<Integer, List<SportsBetPlayerBonus>> mapData = new TreeMap<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<SportsBetPlayerBonus> sportsBetPlayerBonusesList = new ArrayList<>();
        List<PlayerBonuses> playerBonuses = objectMapper.convertValue(responseObject.getBody(), SportsBetBonusResponse.class).getPlayerBonuses();

        if(status != null && !status.isEmpty()) {
            playerBonuses =filterByDateFromAndDateTo(playerBonuses, dateFrom, dateTo, dateTimeFormatter)
                    .filter(f -> f.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
        } else {
            playerBonuses = filterByDateFromAndDateTo(playerBonuses, dateFrom, dateTo, dateTimeFormatter).collect(Collectors.toList());
        }
        SportsBetPlayerBonus sportsBetPlayerBonus = null;
        for (PlayerBonuses pb : playerBonuses) {
            sportsBetPlayerBonus = SportsBetPlayerBonus.builder()
                    .bonusId(pb.getBonus().getBonusId())
                    .playerBonusId(pb.getPlayerBonusId())
                    .status(pb.getStatus())
                    .dateGiven(pb.getDateGiven())
                    .dateExpiration(pb.getDateExpiration())
                    .dateStatusChanged(pb.getDateStatusChanged())
                    .amountGiven(pb.getAmountGiven())
                    .bonusName(pb.getBonus().getBonusName())
                    .bonusGroup(pb.getBonus().getBonusGroup())
                    .bonusCode(StringUtil.replaceUnderScoresInStringWithSpace(pb.getBonus().getBonusName()))
                    .build();
            sportsBetPlayerBonusesList.add(sportsBetPlayerBonus);
        }

        List<SportsBetPlayerBonus> paginatedData = paginateData(sportsBetPlayerBonusesList, dataTableRequest.getPageRequest().getPageNumber(), dataTableRequest.getPageRequest().getPageSize());
        mapData.put(sportsBetPlayerBonusesList.size(), paginatedData);
        return mapData;
    }

    private Stream<PlayerBonuses> filterByDateFromAndDateTo(List<PlayerBonuses> playerBonuses, String dateFrom, String dateTo, DateTimeFormatter dateTimeFormatter) {
        Stream<PlayerBonuses> playerBonusesStream = playerBonuses.stream().filter(s -> LocalDateTime.parse(s.getDateGiven()).toLocalDate().isAfter(
                LocalDate.parse(dateFrom, dateTimeFormatter).minusDays(1)))
                .filter(m -> LocalDateTime.parse(m.getDateGiven()).toLocalDate().isBefore(LocalDate.parse(dateTo, dateTimeFormatter).plusDays(1)));
        return playerBonusesStream;
    }

    private ResponseEntity<SportsBetBonusResponse> sportsBetGetFromExternalResource(String url, String playerId, String hashPassword) {
        ResponseEntity<SportsBetBonusResponse> responseObject = null;
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Accept", "*/*");
        HttpEntity<?> entity = new HttpEntity<>(SportsBetBonusResponse.class,headers);

        try {
            long timestamp = new Date().getTime();
            String sha = calculateSha(playerId, timestamp, hashPassword);
            url = String.format("%s?playerId=%s&sha=%s&timestamp=%s", url, playerId,sha, timestamp);

            log.debug("Making a request to " + url);

            responseObject = restTemplate.exchange(url, HttpMethod.GET, entity, SportsBetBonusResponse.class);
        } catch(Exception ex) {
            log.error("sportsBetGetFromExternalResource : ", ex.getMessage(), ex.getStackTrace().toString());
        }
        return responseObject;
    }

    private List<SportsBetPlayerBonus> paginateData(List<SportsBetPlayerBonus> sportsBetPlayerBonuses, int pageNumber, int resultsPerPage) {
        int skipCount = pageNumber * resultsPerPage;
        List<SportsBetPlayerBonus> pagedData =
                sportsBetPlayerBonuses.stream().skip(skipCount).limit(resultsPerPage).collect(Collectors.toList());
        return pagedData;
    }

    public String calculateSha(String playerId, long timestamp,  String key) {
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(key);
        hasher.addItem(playerId);
        hasher.addItem(timestamp);

        return hasher.calculateHash();
    }
}
