package lithium.service.cashier.verifiers;

import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.data.entities.ProcessorAccountStatus;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.repositories.ProcessorUserCardRepository;
import lithium.service.cashier.data.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith({MockitoExtension.class})
public class OneAccountPerUserVerifierTest {
    
    @InjectMocks
    private OneAccountPerUserVerifier oneAccountPerUserVerifier;
    @Mock
    private ProcessorUserCardRepository processorUserCardRepository;
    private User user = User.builder().id(1L).guid(TEST_GUID).build();;
    private ProcessorAccount processorAccount;
    private ProcessorUserCard savedProcessorAccount;

    private final static String TEST_GUID = "livescore_nigeria/1";
    private final static String TEST_REFERENCE_1 = "src_abv1234567890";
    private final static String TEST_REFERENCE_2 = "src_abv0987654321";
    
    @Test
    public void shouldVerifyOneAccountPerUserWithActiveStatusWithSameReference() throws Exception {
        processorAccount = ProcessorAccount.builder().type(ProcessorAccountType.BANK).reference(TEST_REFERENCE_1).build();
        savedProcessorAccount = ProcessorUserCard.builder().status(ProcessorAccountStatus.builder().name(PaymentMethodStatusType.ACTIVE.getName()).build()).reference(TEST_REFERENCE_1).build();
        when(processorUserCardRepository.findByUserGuidAndTypeName(user.guid(), processorAccount.getType().name())).thenReturn(Arrays.asList(savedProcessorAccount));
        assertTrue(oneAccountPerUserVerifier.verify(user, processorAccount));
    }
    
    @Test
    public void shouldNotVerifyOneAccountPerUserWithActiveStatusWithDifferentReference() throws Exception {
        processorAccount = ProcessorAccount.builder().type(ProcessorAccountType.BANK).reference(TEST_REFERENCE_2).build();
        savedProcessorAccount = ProcessorUserCard.builder().status(ProcessorAccountStatus.builder().name(PaymentMethodStatusType.ACTIVE.getName()).build()).reference(TEST_REFERENCE_1).build();
        when(processorUserCardRepository.findByUserGuidAndTypeName(user.guid(), processorAccount.getType().name())).thenReturn(Arrays.asList(savedProcessorAccount));
        assertFalse(oneAccountPerUserVerifier.verify(user, processorAccount));
    }

    @Test
    public void shouldVerifyOneAccountPerUserWithNotActiveStatusWithSameReference() throws Exception {
        processorAccount = ProcessorAccount.builder().type(ProcessorAccountType.BANK).reference(TEST_REFERENCE_1).build();
        savedProcessorAccount = ProcessorUserCard.builder().status(ProcessorAccountStatus.builder().name(PaymentMethodStatusType.DISABLED.getName()).build()).reference(TEST_REFERENCE_1).build();
        when(processorUserCardRepository.findByUserGuidAndTypeName(user.guid(), processorAccount.getType().name())).thenReturn(Arrays.asList(savedProcessorAccount));
        assertTrue(oneAccountPerUserVerifier.verify(user, processorAccount));
    }

    @Test
    public void shouldVerifyOneAccountPerUserWithNotActiveStatusWithDifferentReferences() throws Exception {
        processorAccount = ProcessorAccount.builder().type(ProcessorAccountType.BANK).reference(TEST_REFERENCE_2).build();
        savedProcessorAccount = ProcessorUserCard.builder().status(ProcessorAccountStatus.builder().name(PaymentMethodStatusType.DISABLED.getName()).build()).reference(TEST_REFERENCE_1).build();
        when(processorUserCardRepository.findByUserGuidAndTypeName(user.guid(), processorAccount.getType().name())).thenReturn(Arrays.asList(savedProcessorAccount));
        assertTrue(oneAccountPerUserVerifier.verify(user, processorAccount));
    }
    
}
