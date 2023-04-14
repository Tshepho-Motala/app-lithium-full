package lithium.service.cashier.processor.premierpay;

import lithium.service.cashier.method.premierpay.util.SignatureCalculator;
import lithium.service.cashier.method.premierpay.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class DoProcessorCallbackTest {

    @Before
    public void setUp() throws Exception {
        log.info("setUp.");
    }

    @After
    public void tearDown() throws Exception {
        log.info("tearDown.");
    }

    @Test
    public void signatureValid() {
        log.info("signatureValid.");
        String signature = signatureValid("c2b7f9", "45", "1573468341042", Status.APPROVED, "Test.com");
        log.info("signature :: "+signature);
    }

    protected String signatureValid(String rcode, String sid, String extTransactionId, Status status, String descriptor) {
        SignatureCalculator sc = SignatureCalculator.builder().build();
        return sc.signature(rcode, sid, extTransactionId, status.code(), descriptor);
    }

    @Test
    public void amount() {
        String amount = "CA$ 11.00 CAD";
        amount = amount.substring(amount.indexOf(" "), amount.lastIndexOf(" ")).trim();
        log.info("amount: "+amount);
    }
}