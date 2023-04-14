package lithium.service.casino.provider.iforium.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final BigDecimal ZERO_AMOUNT = new BigDecimal("0.0");

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
}
