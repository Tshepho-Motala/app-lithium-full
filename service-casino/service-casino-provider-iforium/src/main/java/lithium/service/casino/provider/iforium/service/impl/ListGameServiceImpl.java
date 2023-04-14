package lithium.service.casino.provider.iforium.service.impl;

import lithium.modules.ModuleInfo;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.config.IforiumProviderConfig;
import lithium.service.casino.provider.iforium.config.ProviderConfigService;
import lithium.service.casino.provider.iforium.constant.ListGameConstant;
import lithium.service.casino.provider.iforium.exception.ErrorParsingListGamesFileException;
import lithium.service.casino.provider.iforium.exception.InvalidListGamesURLException;
import lithium.service.casino.provider.iforium.exception.NotConfiguredListGamesURLException;
import lithium.service.casino.provider.iforium.model.response.IforiumGame;
import lithium.service.casino.provider.iforium.service.ListGameService;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.objects.Label;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListGameServiceImpl implements ListGameService {

    private final ProviderConfigService providerConfigService;
    private final ModuleInfo moduleInfo;

    @Override
    public List<Game> listGames(String domainName) throws Status512ProviderNotConfiguredException {
        IforiumProviderConfig iforiumProviderConfig = providerConfigService.getIforiumConfig(domainName);
        List<IforiumGame> iforiumGames = parseUrlToList(iforiumProviderConfig.getListGameUrl());
        return convertToLithiumGames(iforiumGames, domainName);
    }

    private List<Game> convertToLithiumGames(List<IforiumGame> iforiumGames, String domainName) {
        return iforiumGames.stream().map(ig -> buildGame(ig, moduleInfo, domainName)).collect(Collectors.toList());
    }

    private static Game buildGame(IforiumGame iforiumGame, ModuleInfo moduleInfo, String domainName) {
        return Game.builder()
                   .name(iforiumGame.getTitle())
                   .providerGameId(iforiumGame.getGameId())
                   .guid(format("%s_%s", moduleInfo.getModuleName(), iforiumGame.getGameId()))
                   .providerGuid(moduleInfo.getModuleName())
                   .labels(getLabels(iforiumGame, domainName))
                   .progressiveJackpot(parseBoolean(iforiumGame.getJackPot()))
                   .localJackpotPool(false)
                   .networkedJackpotPool(false)
                   .freeGame(false)
                   .build();
    }

    private static Boolean parseBoolean(String jackPot) {
        return "Yes".equalsIgnoreCase(jackPot);
    }

    private static HashMap<String, Label> getLabels(IforiumGame iforiumGame, String domainName) {
        HashMap<String, Label> labels = new HashMap<>();
        Label contentProvider = buildLabel(ListGameConstant.CONTENT_PROVIDER, iforiumGame.getContentProvider(), domainName);
        labels.put(ListGameConstant.CONTENT_PROVIDER, contentProvider);
        Label os = buildLabel(ListGameConstant.OS, iforiumGame.getChannel(), domainName);
        labels.put(ListGameConstant.OS, os);
        Label gameType = buildLabel(ListGameConstant.GAME_TYPE, iforiumGame.getGameType(), domainName);
        labels.put(ListGameConstant.GAME_TYPE, gameType);
        Label genre = buildLabel(ListGameConstant.GENRE, iforiumGame.getGenre(), domainName);
        labels.put(ListGameConstant.GENRE, genre);
        Label market = buildLabel(ListGameConstant.MARKET, iforiumGame.getMarket(), domainName);
        labels.put(ListGameConstant.MARKET, market);
        Label branded = buildLabel(ListGameConstant.BRANDED, iforiumGame.getBranded(), domainName);
        labels.put(ListGameConstant.BRANDED, branded);
        Label volatility = buildLabel(ListGameConstant.VOLATILITY, iforiumGame.getVolatility(), domainName);
        labels.put(ListGameConstant.VOLATILITY, volatility);
        Label rtp = buildLabel(ListGameConstant.RTP, iforiumGame.getRtp(), domainName);
        labels.put(ListGameConstant.RTP, rtp);
        Label releaseDate = buildLabel(ListGameConstant.RELEASE_DATE, iforiumGame.getReleaseDate(), domainName);
        labels.put(ListGameConstant.RELEASE_DATE, releaseDate);
        return labels;
    }

    private static Label buildLabel(String labelName, String labelValue, String domainName) {
        return new Label(labelName, labelValue, domainName, true, false);
    }

    private static List<IforiumGame> parseUrlToList(String url) {
        if (url.trim().isEmpty()) {
            throw new NotConfiguredListGamesURLException();
        }

        try {
            Reader reader = new InputStreamReader(new URL(url).openStream(), Charset.defaultCharset());
            Iterable<CSVRecord> records = CSVFormat.Builder.create().setHeader().setSkipHeaderRecord(true).build().parse(reader);
            return StreamSupport.stream(records.spliterator(), false).map(ListGameServiceImpl::buildIforiumGame)
                                .collect(Collectors.toList());
        } catch (MalformedURLException e){
            throw new InvalidListGamesURLException(url, e);
        } catch (IOException | IllegalArgumentException e) {
            throw new ErrorParsingListGamesFileException(e);
        }
    }

    private static IforiumGame buildIforiumGame(CSVRecord csvRecord) {
        return IforiumGame.builder()
                          .gameId(csvRecord.get(0))
                          .title(csvRecord.get(ListGameConstant.TITLE))
                          .integrationProvider(csvRecord.get(ListGameConstant.INTEGRATION_PROVIDER))
                          .contentProvider(csvRecord.get(ListGameConstant.CONTENT_PROVIDER))
                          .channel(csvRecord.get(ListGameConstant.CHANNEL))
                          .gameType(csvRecord.get(ListGameConstant.GAME_TYPE))
                          .genre(csvRecord.get(ListGameConstant.GENRE))
                          .market(csvRecord.get(ListGameConstant.MARKET))
                          .branded(csvRecord.get(ListGameConstant.BRANDED))
                          .jackPot(csvRecord.get(ListGameConstant.JACKPOT))
                          .volatility(csvRecord.get(ListGameConstant.VOLATILITY))
                          .rtp(csvRecord.get(ListGameConstant.RTP))
                          .releaseDate(csvRecord.get(ListGameConstant.RELEASE_DATE))
                          .build();
    }
}
