package lithium.service.games.stream;

import lithium.service.games.client.objects.supplier.SupplierGameMetaDataMessage;
import lithium.service.games.data.entities.Game;
import lithium.service.games.services.GameService;
import lithium.service.games.services.SupplierGameMetaDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Map;

@Slf4j
@Component
@EnableBinding(SupplierGameMetaDataQueueSink.class)
public class SupplierGameMetaDataQueueProcessor {

    @Autowired
    private SupplierGameMetaDataService supplierGameMetaDataService;

    @Autowired
    private GameService gameService;

    @Transactional
    @StreamListener(SupplierGameMetaDataQueueSink.INPUT)
    void handle(SupplierGameMetaDataMessage message) {
        if (message == null || message.getSupplierGameMetaData() == null)
            return;
        Map<String, Game> domainGamesMap =  gameService.getDomainGameMap(message.getDomainName());
        for (lithium.service.games.client.objects.supplier.SupplierGameMetaData data : message.getSupplierGameMetaData()) {
            if (data == null)
                return;
            Game game = domainGamesMap.get(data.getSupplierGameGuid());
            if (game != null) {
                try {
                    supplierGameMetaDataService.updateGamesAndSupplierGameMetaData(data, game);
                } catch (Exception e) {
                    log.error("Failed to save SupplierGameMetaData: domain: " + message.getDomainName() + " data: " + data, e);
                }
            }
        }

    }

}
