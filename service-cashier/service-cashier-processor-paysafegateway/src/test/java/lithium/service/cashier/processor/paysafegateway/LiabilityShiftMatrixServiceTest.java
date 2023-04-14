package lithium.service.cashier.processor.paysafegateway;

import lithium.service.cashier.processor.paysafegateway.data.Advice;
import lithium.service.cashier.processor.paysafegateway.data.Authentication;
import lithium.service.cashier.processor.paysafegateway.data.ThreeDCard;
import lithium.service.cashier.processor.paysafegateway.data.ThreeDCardType;
import lithium.service.cashier.processor.paysafegateway.data.ThreeDEnrollment;
import lithium.service.cashier.processor.paysafegateway.data.ThreeDResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class LiabilityShiftMatrixServiceTest {

    @InjectMocks
    private LiabilityShiftMatrixService liabilityShiftMatrix;

    @Mock
    private ThreeDCard card;

    @Test
    public void shouldProceedIf3DSecure2AndCardIsMasterCardAndThreeDResultIsY() {
        Authentication authentication = new Authentication();
        authentication.setThreeDSecureVersion("2.1.0");
        authentication.setThreeDResult(ThreeDResult.Y);
        when(card.getAuthentication()).thenReturn(authentication);
        Advice advice = liabilityShiftMatrix.consult(card);
        assertEquals(Advice.PROCEED, advice);
    }

    @Test
    public void shouldProceedIf3DSecure2AndCardIsMasterCardAndThreeDResultIsA() {
        Authentication authentication = new Authentication();
        authentication.setThreeDSecureVersion("2.1.0");
        authentication.setThreeDResult(ThreeDResult.A);
        when(card.getAuthentication()).thenReturn(authentication);
        Advice advice = liabilityShiftMatrix.consult(card);
        assertEquals(Advice.PROCEED, advice);
    }

    @Test
    public void shouldProceedIf3DVersion1AndVisaAnd3DEnrollmentIsYAndEciIs6() {
        Authentication authentication = new Authentication();
        authentication.setThreeDSecureVersion("1.0.0");
        authentication.setThreeDEnrollment(ThreeDEnrollment.Y);
        authentication.setEci("6");
        when(card.getCardType()).thenReturn(ThreeDCardType.VI);
        when(card.getAuthentication()).thenReturn(authentication);
        Advice advice = liabilityShiftMatrix.consult(card);
        assertEquals(Advice.PROCEED, advice);
    }

    @Test
    public void shouldProceedIf3DVersion1AndVisaAnd3DEnrollmentIsYAndEciIs5() {
        Authentication authentication = new Authentication();
        authentication.setThreeDSecureVersion("1.0.0");
        authentication.setThreeDEnrollment(ThreeDEnrollment.Y);
        authentication.setEci("5");
        when(card.getCardType()).thenReturn(ThreeDCardType.VI);
        when(card.getAuthentication()).thenReturn(authentication);
        Advice advice = liabilityShiftMatrix.consult(card);
        assertEquals(Advice.PROCEED, advice);
    }

    @Test
    public void shouldProceedIf3DVersion1AndVisaAnd3DEnrollmentIsNAndEciIs6() {
        Authentication authentication = new Authentication();
        authentication.setThreeDSecureVersion("1.0.0");
        authentication.setThreeDEnrollment(ThreeDEnrollment.N);
        authentication.setEci("6");
        when(card.getCardType()).thenReturn(ThreeDCardType.VI);
        when(card.getAuthentication()).thenReturn(authentication);
        Advice advice = liabilityShiftMatrix.consult(card);
        assertEquals(Advice.PROCEED, advice);
    }

    @Test
    public void shouldProceedIf3DVersion1AndMCAnd3DEnrollmentIsYAndEciIs1() {
        Authentication authentication = new Authentication();
        authentication.setThreeDSecureVersion("1.0.0");
        authentication.setThreeDEnrollment(ThreeDEnrollment.Y);
        authentication.setEci("1");
        when(card.getCardType()).thenReturn(ThreeDCardType.MC);
        when(card.getAuthentication()).thenReturn(authentication);
        Advice advice = liabilityShiftMatrix.consult(card);
        assertEquals(Advice.PROCEED, advice);
    }

    @Test
    public void shouldProceedIf3DVersion1AndMCAnd3DEnrollmentIsYAndEciIs2() {
        Authentication authentication = new Authentication();
        authentication.setThreeDSecureVersion("1.0.0");
        authentication.setThreeDEnrollment(ThreeDEnrollment.Y);
        authentication.setEci("2");
        when(card.getCardType()).thenReturn(ThreeDCardType.MC);
        when(card.getAuthentication()).thenReturn(authentication);
        Advice advice = liabilityShiftMatrix.consult(card);
        assertEquals(Advice.PROCEED, advice);
    }

}
