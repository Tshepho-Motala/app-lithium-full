package lithium.service.cashier.verifiers;

import lithium.service.access.client.AccessService;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.services.UserService;
import lithium.service.user.client.objects.Domain;
import lithium.service.user.client.objects.PlayerBasic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class BankAccountNameExternalSphonicVerifierTest {
    @InjectMocks
    private BankAccountNameExternalSphonicVerifier bankAccountNameExternalSphonicVerifier;
    @Mock
    private BankAccountNameInternalVerifier bankAccountNameInternalVerifier;
    @Mock
    private AccessService accessService;
    @Mock
    private UserService userService;
    private User cashierUser = User.builder().id(1L).guid(TEST_GUID).build();
    private Domain domain = Domain.builder().id(1L).name(TEST_DOMAIN_NAME).build();
    private lithium.service.user.client.objects.User user = lithium.service.user.client.objects.User.builder().id(1L).domain(domain).build();
    private ProcessorAccount processorAccount = createProcessorAccount();
    private PlayerBasic playerBasic = createPlayerBasic();
    private static final String BANK_ACCOUNT_NAME_EXTERNAL_ACCESS_RULE = "ibanAccessRule";
    private static final String TEST_GUID = "livescore_nigeria/1";
    private static final String TEST_IBAN = "GB33BUKB20201555555555";
    private static final String TEST_DOMAIN_NAME = "livescore_nigeria";

    private static PlayerBasic createPlayerBasic() {
        Map<String, String> additionalData = Map.of("iban", TEST_IBAN, "guid", TEST_GUID);
        return PlayerBasic.builder().additionalData(additionalData).build();
    }

    private static ProcessorAccount createProcessorAccount() {
        Map<String, String> data = Map.of("iban", TEST_IBAN);
        return ProcessorAccount.builder().id(1L).data(data).build();
    }

    @BeforeEach
    public void setup() throws Exception {
        when(userService.retrieveUserFromUserService(cashierUser)).thenReturn(user);
    }
    
    @Test
    public void shouldNotInvocateExternalVerificationAfterSuccessfulInternalVerification() throws Exception {
        when(bankAccountNameInternalVerifier.verify(cashierUser, processorAccount)).thenReturn(true);
        assertTrue(bankAccountNameExternalSphonicVerifier.verify(cashierUser, processorAccount));
        verify(accessService, never())
                .checkAuthorization(user.getDomain().getName(), BANK_ACCOUNT_NAME_EXTERNAL_ACCESS_RULE, null, Collections.emptyMap(), null, null, false, playerBasic, playerBasic.getAdditionalData());
    }

    @Test
    public void shouldVerifyExternalAuthorizationAfterFailedInternalVerification() throws Exception {
        when(bankAccountNameInternalVerifier.verify(cashierUser, processorAccount)).thenReturn(false);
        when(accessService.checkAuthorization(user.getDomain().getName(), BANK_ACCOUNT_NAME_EXTERNAL_ACCESS_RULE, null, Collections.emptyMap(), null, null, false, playerBasic, playerBasic.getAdditionalData()))
                .thenReturn(AuthorizationResult.builder().successful(true).build());
        assertTrue(bankAccountNameExternalSphonicVerifier.verify(cashierUser, processorAccount));
        verify(accessService, times(1))
                .checkAuthorization(user.getDomain().getName(), BANK_ACCOUNT_NAME_EXTERNAL_ACCESS_RULE, null, Collections.emptyMap(), null, null, false, playerBasic, playerBasic.getAdditionalData());
    }

    @Test
    public void shouldNotVerifyExternalAuthorizationAfterFailedInternalVerification() throws Exception {
        when(bankAccountNameInternalVerifier.verify(cashierUser, processorAccount)).thenReturn(false);
        when(accessService.checkAuthorization(user.getDomain().getName(), BANK_ACCOUNT_NAME_EXTERNAL_ACCESS_RULE, null, Collections.emptyMap(), null, null, false, playerBasic, playerBasic.getAdditionalData()))
                .thenReturn(AuthorizationResult.builder().successful(false).build());
        assertFalse(bankAccountNameExternalSphonicVerifier.verify(cashierUser, processorAccount));
        verify(accessService, times(1))
                .checkAuthorization(user.getDomain().getName(), BANK_ACCOUNT_NAME_EXTERNAL_ACCESS_RULE, null, Collections.emptyMap(), null, null, false, playerBasic, playerBasic.getAdditionalData());
    }
}