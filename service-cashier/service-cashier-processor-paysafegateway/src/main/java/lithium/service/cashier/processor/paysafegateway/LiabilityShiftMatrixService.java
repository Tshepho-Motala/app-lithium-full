package lithium.service.cashier.processor.paysafegateway;

import lithium.service.cashier.processor.paysafegateway.data.Advice;
import lithium.service.cashier.processor.paysafegateway.data.Authentication;
import lithium.service.cashier.processor.paysafegateway.data.ThreeDCard;
import lithium.service.cashier.processor.paysafegateway.data.ThreeDCardType;
import lithium.service.cashier.processor.paysafegateway.data.ThreeDEnrollment;
import lithium.service.cashier.processor.paysafegateway.data.ThreeDResult;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

@Service
public class LiabilityShiftMatrixService {

    public Advice consult(ThreeDCard card) {
        if ( card == null && card.getAuthentication() == null ) {
            return Advice.DO_NOT_PROCEED;
        }

        Authentication authentication = card.getAuthentication();
        if (authentication.getThreeDSecureVersion().startsWith("2")) {
            Set<ThreeDResult> set = EnumSet.of(ThreeDResult.Y, ThreeDResult.A);
            if(set.contains(authentication.getThreeDResult())){
                return Advice.PROCEED;
            } else {
                return Advice.DO_NOT_PROCEED;
            }
        } else {

            if (card.getCardType() == ThreeDCardType.VI) {
                Set<ThreeDEnrollment> visaEnrolSet = EnumSet.of(ThreeDEnrollment.Y, ThreeDEnrollment.N);
                if (visaEnrolSet.contains(authentication.getThreeDEnrollment()) &&
                        Arrays.asList(new String[]{"5", "6"}).contains(authentication.getEci())) {
                    return Advice.PROCEED;
                }
            }
            if (card.getCardType() == ThreeDCardType.MC) {
                Set<ThreeDEnrollment> visaEnrolSet = EnumSet.of(ThreeDEnrollment.Y);
                if (visaEnrolSet.contains(authentication.getThreeDEnrollment()) &&
                        Arrays.asList(new String[]{"1", "2"}).contains(authentication.getEci())) {
                    return Advice.PROCEED;
                }
            }
        }
        return Advice.DO_NOT_PROCEED;
    }
}
