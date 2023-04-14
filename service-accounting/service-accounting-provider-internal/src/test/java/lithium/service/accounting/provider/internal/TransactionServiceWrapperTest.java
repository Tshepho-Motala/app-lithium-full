package lithium.service.accounting.provider.internal;

import lithium.service.accounting.provider.internal.services.TransactionServiceWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

@Slf4j
public class TransactionServiceWrapperTest {

    @Test
    public void testParseLabels() {

        TransactionServiceWrapper.KeyValue label = TransactionServiceWrapper.resolveLabel("transaction_id=1234567890").get();

        assertEquals("transaction_id",label.getKey());
        assertEquals("1234567890",label.getValue());

        label = TransactionServiceWrapper.resolveLabel("comment=Balance adjusted. NGN N1,500 [MANUAL_BALANCE_ADJUST] [comment=BALANCE TRANSFER- NGN N1,500]").get();

        assertEquals("comment",label.getKey());
        assertEquals("Balance adjusted. NGN N1,500 [MANUAL_BALANCE_ADJUST] [comment=BALANCE TRANSFER- NGN N1,500]",label.getValue());

        label = TransactionServiceWrapper.resolveLabel("test=Test 1=1; 2!=3: @#$%^&*/").get();

        assertEquals("test",label.getKey());
        assertEquals("Test 1=1; 2!=3: @#$%^&*/",label.getValue());

        Optional<TransactionServiceWrapper.KeyValue> incorretLabel = TransactionServiceWrapper.resolveLabel("Incorrect value");

        assertEquals(Optional.empty(), incorretLabel);
    }
}
