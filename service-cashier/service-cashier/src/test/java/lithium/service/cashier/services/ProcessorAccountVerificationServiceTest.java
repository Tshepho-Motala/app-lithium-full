package lithium.service.cashier.services;

import lithium.service.cashier.client.internal.VerifyProcessorAccountRequest;
import lithium.service.cashier.client.internal.VerifyProcessorAccountResponse;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.data.entities.User;

import lithium.service.cashier.verifiers.BankAccountNameExternalSphonicVerifier;
import lithium.service.cashier.verifiers.DuplicateAccountVerifier;
import lithium.service.cashier.verifiers.ProcessorAccountVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith({MockitoExtension.class})
public class ProcessorAccountVerificationServiceTest {
    private ProcessorAccountVerificationService processorAccountVerificationService;
    @Mock
    private ProcessorAccountService processorAccountService;
    @Mock
    private UserService userService;
    @Mock
    private DomainMethodProcessorService dmpService;
    @Mock
    private BankAccountNameExternalSphonicVerifier bankAccountNameExternalSphonicVerifier;
    @Mock
    private DuplicateAccountVerifier duplicateAccountVerifier;
    private User user = User.builder().id(1L).guid(TEST_GUID).build();
    private ProcessorAccount processorAccount = ProcessorAccount.builder()
            .type(ProcessorAccountType.BANK)
            .data(Map.of("fingerprint", TEST_FINGERPRINT))
            .build();
    private VerifyProcessorAccountRequest verifyProcessorAccountRequest = VerifyProcessorAccountRequest.builder()
            .userGuid(TEST_GUID)
            .processorAccount(processorAccount)
            .verifications(Arrays.asList(ProcessorAccountVerificationType.BANK_ACCOUNT_NAME_EXTERNAL_SPHONIC,
                    ProcessorAccountVerificationType.DUPLICATE_ACCOUNT))
            .update(true)
            .build();
    private static final String TEST_GUID = "livescore_nigeria/1";
    private static final String TEST_FINGERPRINT = "NDAxMjAwMDAwMDAwMzAxMDEyMjAxOA==";
    
    @BeforeEach
    public void setup() {
        when(userService.findOrCreate(verifyProcessorAccountRequest.getUserGuid())).thenReturn(user);
        when(bankAccountNameExternalSphonicVerifier.getType()).thenReturn(ProcessorAccountVerificationType.BANK_ACCOUNT_NAME_EXTERNAL_SPHONIC);
        when(duplicateAccountVerifier.getType()).thenReturn(ProcessorAccountVerificationType.DUPLICATE_ACCOUNT);
        List<ProcessorAccountVerifier> verifierList = Arrays.asList(bankAccountNameExternalSphonicVerifier, duplicateAccountVerifier);
        processorAccountVerificationService = new ProcessorAccountVerificationService(userService, processorAccountService, dmpService, verifierList);
    }
    
    @Test
    public void shouldVerifyStoredActiveProcessorAccount() throws Exception {
        when(processorAccountService.getProcessorAccount(user, processorAccount.getReference(), processorAccount.getData().get("fingerprint"))).thenReturn(processorAccount);
        when(duplicateAccountVerifier.verify(user, processorAccount)).thenReturn(true);
        when(bankAccountNameExternalSphonicVerifier.verify(user, processorAccount)).thenReturn(true);
        verifyProcessorAccountRequest.getProcessorAccount().setStatus(PaymentMethodStatusType.ACTIVE);
        VerifyProcessorAccountResponse verifyResult = processorAccountVerificationService.verify(verifyProcessorAccountRequest);
        assertTrue(verifyResult.getProcessorAccount().getVerified());
        assertNull(verifyResult.getProcessorAccount().getFailedVerification());
    }
    
    @Test
    public void shouldNotVerifyStoredDisabledProcessorAccountAndFailedWithNotActiveAccountStatus() throws Exception {
        when(processorAccountService.getProcessorAccount(user, processorAccount.getReference(), processorAccount.getData().get("fingerprint"))).thenReturn(processorAccount);
        verifyProcessorAccountRequest.getProcessorAccount().setStatus(PaymentMethodStatusType.DISABLED);
        VerifyProcessorAccountResponse verifyResult = processorAccountVerificationService.verify(verifyProcessorAccountRequest);
        assertFalse(verifyResult.getProcessorAccount().getVerified());
        assertEquals(ProcessorAccountVerificationType.ACTIVE_ACCOUNT, verifyResult.getProcessorAccount().getFailedVerification());
    }

    @Test
    public void shouldNotVerifyStoredBlockedProcessorAccountAndFailedWithNotActiveAccountStatus() throws Exception {
        when(processorAccountService.getProcessorAccount(user, processorAccount.getReference(), processorAccount.getData().get("fingerprint"))).thenReturn(processorAccount);
        verifyProcessorAccountRequest.getProcessorAccount().setStatus(PaymentMethodStatusType.BLOCKED);
        VerifyProcessorAccountResponse verifyResult = processorAccountVerificationService.verify(verifyProcessorAccountRequest);
        assertFalse(verifyResult.getProcessorAccount().getVerified());
        assertEquals(ProcessorAccountVerificationType.ACTIVE_ACCOUNT, verifyResult.getProcessorAccount().getFailedVerification());
    }

    @Test
    public void shouldVerifyStoredDepositOnlyProcessorAccount() throws Exception {
        when(processorAccountService.getProcessorAccount(user, processorAccount.getReference(), processorAccount.getData().get("fingerprint"))).thenReturn(processorAccount);
        when(bankAccountNameExternalSphonicVerifier.verify(user, processorAccount)).thenReturn(true);
        when(duplicateAccountVerifier.verify(user, processorAccount)).thenReturn(true);
        verifyProcessorAccountRequest.getProcessorAccount().setStatus(PaymentMethodStatusType.DEPOSIT_ONLY);
        VerifyProcessorAccountResponse verifyResult = processorAccountVerificationService.verify(verifyProcessorAccountRequest);
        assertTrue(verifyResult.getProcessorAccount().getVerified());
        assertNull(verifyResult.getProcessorAccount().getFailedVerification());
    }

    @Test
    public void shouldVerifyStoredWithdrawalOnlyProcessorAccount() throws Exception {
        when(processorAccountService.getProcessorAccount(user, processorAccount.getReference(), processorAccount.getData().get("fingerprint"))).thenReturn(processorAccount);
        when(bankAccountNameExternalSphonicVerifier.verify(user, processorAccount)).thenReturn(true);
        when(duplicateAccountVerifier.verify(user, processorAccount)).thenReturn(true);
        verifyProcessorAccountRequest.getProcessorAccount().setStatus(PaymentMethodStatusType.WITHDRAWAL_ONLY);
        VerifyProcessorAccountResponse verifyResult = processorAccountVerificationService.verify(verifyProcessorAccountRequest);
        assertTrue(verifyResult.getProcessorAccount().getVerified());
        assertNull(verifyResult.getProcessorAccount().getFailedVerification());
    }

    @Test
    public void shouldVerifyStoredHistoricProcessorAccount() throws Exception {
        when(processorAccountService.getProcessorAccount(user, processorAccount.getReference(), processorAccount.getData().get("fingerprint"))).thenReturn(processorAccount);
        when(bankAccountNameExternalSphonicVerifier.verify(user, processorAccount)).thenReturn(true);
        when(duplicateAccountVerifier.verify(user, processorAccount)).thenReturn(true);
        verifyProcessorAccountRequest.getProcessorAccount().setStatus(PaymentMethodStatusType.HISTORIC);
        VerifyProcessorAccountResponse verifyResult = processorAccountVerificationService.verify(verifyProcessorAccountRequest);
        assertTrue(verifyResult.getProcessorAccount().getVerified());
        assertNull(verifyResult.getProcessorAccount().getFailedVerification());
    }

    @Test
    public void shouldNotVerifyExpiredProcessorAccountAndFailedWithNotActiveAccountStatus() throws Exception {
        when(processorAccountService.getProcessorAccount(user, processorAccount.getReference(), processorAccount.getData().get("fingerprint"))).thenReturn(processorAccount);
        verifyProcessorAccountRequest.getProcessorAccount().setStatus(PaymentMethodStatusType.EXPIRED);
        VerifyProcessorAccountResponse verifyResult = processorAccountVerificationService.verify(verifyProcessorAccountRequest);
        assertFalse(verifyResult.getProcessorAccount().getVerified());
        assertEquals(ProcessorAccountVerificationType.ACTIVE_ACCOUNT, verifyResult.getProcessorAccount().getFailedVerification());
    }
    
    @Test
    public void shouldVerifyNewProcessorAccount() throws Exception {
        when(processorAccountService.getProcessorAccount(user, processorAccount.getReference(), processorAccount.getData().get("fingerprint"))).thenReturn(null);
        when(bankAccountNameExternalSphonicVerifier.verify(user, processorAccount)).thenReturn(true);
        when(duplicateAccountVerifier.verify(user, processorAccount)).thenReturn(true);
        VerifyProcessorAccountResponse verifyResult = processorAccountVerificationService.verify(verifyProcessorAccountRequest);
        assertTrue(verifyResult.getProcessorAccount().getVerified());
        assertNull(verifyResult.getProcessorAccount().getFailedVerification());
    }

    @Test
    public void shouldNotCallProcessorAccountVerificationWithEmptyVerificationsList() throws Exception {
        verifyProcessorAccountRequest.setVerifications(Collections.emptyList());
        VerifyProcessorAccountResponse verifyResult = processorAccountVerificationService.verify(verifyProcessorAccountRequest);
        assertNull(verifyResult.getProcessorAccount().getVerified());
        assertNull(verifyResult.getProcessorAccount().getFailedVerification());
        verify(duplicateAccountVerifier, never()).verify(user, processorAccount);
        verify(bankAccountNameExternalSphonicVerifier, never()).verify(user, processorAccount);
    }
    @Test
    public void shouldCatchExceptionAsFailedProcessorAccountVerification() throws Exception {
        when(bankAccountNameExternalSphonicVerifier.verify(user, processorAccount)).thenThrow(Exception.class);
        VerifyProcessorAccountResponse verifyResult = processorAccountVerificationService.verify(verifyProcessorAccountRequest);
        assertFalse(verifyResult.getProcessorAccount().getVerified());
        assertEquals(ProcessorAccountVerificationType.BANK_ACCOUNT_NAME_EXTERNAL_SPHONIC, verifyResult.getProcessorAccount().getFailedVerification());
    }
    
    @Test
    public void shouldVerifyProcessorAccountWithMultipleVerifiers() throws Exception {
        when(bankAccountNameExternalSphonicVerifier.verify(user, processorAccount)).thenReturn(true);
        when(duplicateAccountVerifier.verify(user, processorAccount)).thenReturn(true);
        VerifyProcessorAccountResponse verifyResult = processorAccountVerificationService.verify(verifyProcessorAccountRequest);
        assertTrue(verifyResult.getProcessorAccount().getVerified());
        assertNull(verifyResult.getProcessorAccount().getFailedVerification());
        verify(bankAccountNameExternalSphonicVerifier, times(1)).verify(user, processorAccount);
        verify(duplicateAccountVerifier, times(1)).verify(user, processorAccount);
    }

    @Test
    public void shouldNotVerifyProcessorAccountWithMultipleVerifiersAndNotCallNextAfterFirstFailed() throws Exception {
        when(bankAccountNameExternalSphonicVerifier.verify(user, processorAccount)).thenReturn(false);
        VerifyProcessorAccountResponse verifyResult = processorAccountVerificationService.verify(verifyProcessorAccountRequest);
        assertFalse(verifyResult.getProcessorAccount().getVerified());
        assertEquals(ProcessorAccountVerificationType.BANK_ACCOUNT_NAME_EXTERNAL_SPHONIC, verifyResult.getProcessorAccount().getFailedVerification());
        verify(bankAccountNameExternalSphonicVerifier, times(1)).verify(user, processorAccount);
        verify(duplicateAccountVerifier, never()).verify(user, processorAccount);
    }
    
}
