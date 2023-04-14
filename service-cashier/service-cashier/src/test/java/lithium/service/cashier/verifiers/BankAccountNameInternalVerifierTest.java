package lithium.service.cashier.verifiers;

import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith({MockitoExtension.class})
public class BankAccountNameInternalVerifierTest {

    @InjectMocks
    private BankAccountNameInternalVerifier bankAccountNameInternalVerifier;
    
    @Mock
    private UserService userService;

    private User user = User.builder().id(1L).build();
    
    private ProcessorAccount processorAccount = ProcessorAccount.builder().id(1L).name(TEST_BANK_NAME).build();

    private static final String TEST_BANK_NAME = "Hr E G H Küppers en/of MW M.J. Küppers-Veeneman";

    @Test
    public void shouldVerifyUserFirstNameInitialBeforeLastNameWithBankName() throws Exception {
        when(userService.retrieveUserFromUserService(user)).thenReturn(lithium.service.user.client.objects.User.builder().firstName("Greg").lastName("Küppers").build());
        assertTrue(bankAccountNameInternalVerifier.verify(user, processorAccount));
    }

    @Test
    public void shouldVerifyUserFirstNameInitialAfterLastNameWithBankName() throws Exception {
        when(userService.retrieveUserFromUserService(user)).thenReturn(lithium.service.user.client.objects.User.builder().firstName("Michael").lastName("Küppers").build());
        assertTrue(bankAccountNameInternalVerifier.verify(user, processorAccount));
    }

    @Test
    public void shouldNotVerifyUserFirstNameInitialWithBankNameIfUserFirstNameInitialDoesntMatch() throws Exception {
        when(userService.retrieveUserFromUserService(user)).thenReturn(lithium.service.user.client.objects.User.builder().firstName("Robert").lastName("Küppers").build());
        assertFalse(bankAccountNameInternalVerifier.verify(user, processorAccount));
    }

    @Test
    public void shouldNotVerifyUserNameWithBankNameIfUserLastNameDoesntMatch() throws Exception {
        when(userService.retrieveUserFromUserService(user)).thenReturn(lithium.service.user.client.objects.User.builder().firstName("John").lastName("Doe").build());
        assertFalse(bankAccountNameInternalVerifier.verify(user, processorAccount));
    }
}
