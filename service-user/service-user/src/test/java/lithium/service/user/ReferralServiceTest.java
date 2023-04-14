package lithium.service.user;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import lithium.service.user.data.entities.UserApiToken;
import lithium.service.user.exceptions.Status400BadRequestException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.ReferralService;
import lithium.service.user.services.UserApiTokenService;
import org.springframework.context.MessageSource;
import java.util.Locale;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class ReferralServiceTest {

    @InjectMocks
    private ReferralService referralService;

    @Mock
    private UserApiTokenService userTokenService;

    @Mock
    private User player;

    @Mock
    private UserApiToken userApiToken;

    @Mock
    private MessageSource messageSource = mock(MessageSource.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowIfReferrerCodeIsNotValid() throws Exception {
        expectedException.expect(Status400BadRequestException.class);
        String invalidReferrerCode="TESTCODE";
        when(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class)))
          .thenReturn("Invalid Referrer Code TESTCODE.");
        referralService.addReferralAfterSignUp(invalidReferrerCode, player);
    }

    @Test
    public void shouldFailToRedeemOwnCode() throws Exception {
        expectedException.expect(Status400BadRequestException.class);
        String referrerGuid="LEGAME";
        when(userTokenService.findByShortGuid(anyString())).thenReturn(userApiToken);
        when(player.getUserApiToken()).thenReturn(userApiToken);
        when(userApiToken.getShortGuid()).thenReturn(referrerGuid);
        when(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class)))
          .thenReturn("Invalid Referrer Code TESTCODE.");
        referralService.addReferralAfterSignUp(referrerGuid, player);
    }
}
