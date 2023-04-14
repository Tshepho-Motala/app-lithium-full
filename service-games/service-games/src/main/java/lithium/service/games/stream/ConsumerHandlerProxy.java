package lithium.service.games.stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.casino.CasinoTransactionLabels;
import lithium.service.accounting.client.stream.event.CompletedTransactionEventService;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.games.data.entities.Game;
import lithium.service.games.services.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Service
@Slf4j
public class ConsumerHandlerProxy {
    @Autowired private GameService gameService;
    @Autowired private RabbitTemplate rabbitTemplate;

    @Transactional
    public void handleMessage(CompleteTransaction completeTransaction)
            throws JsonProcessingException
    {
        log.info("Received CompleteTransaction to be enhanced: " + completeTransaction);

        String transactionTypeCode = completeTransaction.getTransactionType();
        String userGuid = completeTransaction.getTransactionEntryList().get(0).getAccount().getOwner().getGuid();
        String domainName = completeTransaction.getTransactionEntryList().get(0).getAccount().getDomain().getName();
        String gameGuid = completeTransaction.getTransactionLabelList().stream()
                .filter(label -> label.getLabelName().contentEquals(CasinoTransactionLabels.GAME_GUID_LABEL))
                .map(TransactionLabelBasic::getLabelValue)
                .findFirst()
                .orElse(null);
        String gameName = (gameGuid!=null)?gameGuid.split("/")[1]:"";
        Game game = gameService.findByGameAndDomainName(gameName ,domainName);
        if (game == null) {
            log.warn("Game not found, could not enhance game data.." +
                    " [userGuid="+userGuid+", gameGuid="+gameGuid+", transactionTypeCode="+transactionTypeCode+"]");
        } else {
            log.info("Adding game-data: "+game);
            ObjectMapper jsonMapper = new ObjectMapper();
            String result = jsonMapper.writeValueAsString(game);
            log.info("createPayloadFromObject " + result);
            result = Base64.getEncoder().encodeToString(result.getBytes());

            completeTransaction.getTransactionLabelList().add(
                    TransactionLabelBasic.builder()
                            .labelName("game-data")
                            .labelValue(result)
                            .build()
            );
        }


        rabbitTemplate.convertAndSend(
                CompletedTransactionEventService.FANOUT_EXCHANGE_GAMES_ENHANCED, CompletedTransactionEventService.ROUTING_KEY_PRE +"enhanced."+transactionTypeCode.toLowerCase().replaceAll("_", "."), completeTransaction);
    }
}
