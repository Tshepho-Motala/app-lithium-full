package lithium.service.cashier.verifiers;

import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;

import lithium.service.cashier.data.entities.ProcessorAccountStatus;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.repositories.ProcessorUserCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith({MockitoExtension.class})
public class DuplicateAccountVerifierTest {
    @InjectMocks
    private DuplicateAccountVerifier duplicateAccountVerifier;
    @Mock
    private ProcessorUserCardRepository processorUserCardRepository;
    private ProcessorAccount processorAccount = ProcessorAccount.builder().id(1L).data(Map.of("fingerprint", TEST_FINGERPRINT)).reference(TEST_REFERENCE).build();
    private User cashierUser = User.builder().id(1L).build();
    private ProcessorUserCard savedProcessorAccount = ProcessorUserCard.builder().id(1L).fingerprint(TEST_FINGERPRINT).build();

    private final static String TEST_GUID_1 = "livescore_nigeria/1";
    private final static String TEST_GUID_2 = "livescore_nigeria/2";
    private final static String TEST_FINGERPRINT = "NDAxMjAwMDAwMDAwMzAxMDEyMjAxOA==";
    private final static String TEST_REFERENCE = "src_abv1234567890";
    
    @BeforeEach
    public void setup() {
        Map<String, String> data = new HashMap<>();
        data.put("fingerprint", TEST_FINGERPRINT);
        processorAccount = ProcessorAccount.builder().data(data).reference(TEST_REFERENCE).build();
        savedProcessorAccount = ProcessorUserCard.builder()
                .fingerprint(TEST_FINGERPRINT)
                .build();
    }
    
    @Test
    public void shouldVerifyDuplicateAccountForSameUserWithActivePaymentMethodStatusType() throws Exception {
        savedProcessorAccount.setUser(User.builder().guid(TEST_GUID_1).build());
        savedProcessorAccount.setStatus(ProcessorAccountStatus.builder().name(PaymentMethodStatusType.ACTIVE.getName()).build());
        cashierUser.setGuid(TEST_GUID_1);
        processorAccount.setUserGuid(TEST_GUID_1);
        when(processorUserCardRepository.findByFingerprint(TEST_FINGERPRINT)).thenReturn(Arrays.asList(savedProcessorAccount));
        assertTrue(duplicateAccountVerifier.verify(cashierUser, processorAccount));
    }

    @Test
    public void shouldNotVerifyDuplicateAccountForDifferentUsersWithActivePaymentMethodStatusType() throws Exception {
        savedProcessorAccount.setUser(User.builder().guid(TEST_GUID_1).build());
        savedProcessorAccount.setStatus(ProcessorAccountStatus.builder().name(PaymentMethodStatusType.ACTIVE.getName()).build());
        cashierUser.setGuid(TEST_GUID_2);
        processorAccount.setUserGuid(TEST_GUID_2);
        when(processorUserCardRepository.findByFingerprint(TEST_FINGERPRINT)).thenReturn(Arrays.asList(savedProcessorAccount));
        assertFalse(duplicateAccountVerifier.verify(cashierUser, processorAccount));
    }

    @Test
    public void shouldVerifyDuplicateAccountForDifferentUsersWithDisabledPaymentMethodStatusType() throws Exception {
        savedProcessorAccount.setUser(User.builder().guid(TEST_GUID_1).build());
        savedProcessorAccount.setStatus(ProcessorAccountStatus.builder().name(PaymentMethodStatusType.DISABLED.getName()).build());
        cashierUser.setGuid(TEST_GUID_2);
        processorAccount.setUserGuid(TEST_GUID_2);
        when(processorUserCardRepository.findByFingerprint(TEST_FINGERPRINT)).thenReturn(Arrays.asList(savedProcessorAccount));
        assertTrue(duplicateAccountVerifier.verify(cashierUser, processorAccount));
    }

    @Test
    public void shouldNotVerifyDuplicateAccountForSameUserWithDisabledPaymentMethodStatusType() throws Exception {
        savedProcessorAccount.setUser(User.builder().guid(TEST_GUID_1).build());
        savedProcessorAccount.setStatus(ProcessorAccountStatus.builder().name(PaymentMethodStatusType.DISABLED.getName()).build());
        cashierUser.setGuid(TEST_GUID_1);
        processorAccount.setUserGuid(TEST_GUID_1);
        when(processorUserCardRepository.findByFingerprint(TEST_FINGERPRINT)).thenReturn(Arrays.asList(savedProcessorAccount));
        assertFalse(duplicateAccountVerifier.verify(cashierUser, processorAccount));
    }
    
    @Test
    public void verifyDuplicateAccountWithNoFingerprint() throws Exception {
        savedProcessorAccount.setUser(User.builder().guid(TEST_GUID_1).build());
        savedProcessorAccount.setStatus(ProcessorAccountStatus.builder().name(PaymentMethodStatusType.ACTIVE.getName()).build());
        cashierUser.setGuid(TEST_GUID_1);
        processorAccount.getData().remove("fingerprint");
        when(processorUserCardRepository.findByReference(TEST_REFERENCE)).thenReturn(Arrays.asList(savedProcessorAccount));
        assertTrue(duplicateAccountVerifier.verify(cashierUser, processorAccount));
    }
}
