package lithium.service.accounting.client.transactiontyperegister;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@Slf4j
public class TransactionTypeRegisterServiceTest {

    private TransactionTypeRegisterStream stream = Mockito.mock(TransactionTypeRegisterStream.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testRegister() {

        Mockito.doAnswer(invocation -> {
            log.info("Stream register " + invocation.toString());
            return null;
        }).when(stream).register(any());

        TransactionTypeRegisterService transactionTypeService = new TransactionTypeRegisterService(stream);
        {
            Long ttid = transactionTypeService.create("CASINO_BET").getData().getId();
            transactionTypeService.addAccount(ttid, "PLAYER_BALANCE", true, false); //plb is debited (so positive is added to plb since reduces liability to company)
            transactionTypeService.addAccount(ttid, "CASINO_BET", false, true);
            transactionTypeService.addUniqueLabel(ttid, "transaction_id", false, "CASINO_BET");
            transactionTypeService.addLabel(ttid, "player_bonus_history_id", true);
        }
        transactionTypeService.register();
    }
}