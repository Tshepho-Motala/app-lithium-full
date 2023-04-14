package lithium.service.cashier.controllers.internal;

import lithium.exceptions.Status400BadRequestException;
import lithium.service.Response;
import lithium.service.cashier.client.CashierProcessorAccountInternalClient;
import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import lithium.service.cashier.client.internal.AccountProcessorRequest;
import lithium.service.cashier.client.internal.VerifyProcessorAccountRequest;
import lithium.service.cashier.client.internal.VerifyProcessorAccountResponse;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.client.objects.UserCard;
import lithium.service.cashier.data.entities.ProcessorAccountTransaction;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.services.DomainMethodProcessorService;
import lithium.service.cashier.services.DomainMethodService;
import lithium.service.cashier.services.ProcessorAccountAddService;
import lithium.service.cashier.services.ProcessorAccountService;
import lithium.service.cashier.services.ProcessorAccountServiceOld;
import lithium.service.cashier.services.TransactionService;
import lithium.service.cashier.services.UserService;
import lithium.service.cashier.verifiers.DuplicateAccountVerifier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/internal")
public class ProcessorUserController implements CashierProcessorAccountInternalClient {
    private final TransactionService transactionService;
    private final UserService userService;
    private final ProcessorAccountService processorAccountService;
    private final ProcessorAccountServiceOld processorAccountServiceOld;
    private final ProcessorAccountAddService processorAccountAddService;
    private final DomainMethodService dmService;
    private final DomainMethodProcessorService dmpService;
    private final DuplicateAccountVerifier duplicateAccountVerifier;

    @RequestMapping(path="/saveUserCardByDomainProcessorId", method=RequestMethod.POST)
    public Response<String> saveUserCardByDomainProcessorId(
            @RequestParam String userGuid,
            @RequestParam("domainMethodProcessorId") Long domainMethodProcessorId,
            @RequestBody UserCard userCard)
    {
        try {

            processorAccountServiceOld.saveProcessorUserCard(userGuid, dmpService.find(domainMethodProcessorId), userCard);
        } catch (Exception e) {
            log.error("Failed to save user cards. (" +
                    "userGuid " + userGuid +
                    " domainMethodProcessorId " + domainMethodProcessorId, e);
            return Response.<String>builder()
                    .data(null)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
        return Response.<String>builder()
                .data("OK")
                .status(Response.Status.OK)
                .message("OK")
                .build();
    }

    @RequestMapping(path="/addcardremark", method = RequestMethod.POST)
    public Response<String> addCardRemark(
            @RequestParam("transactionId") Long transactionId,
            @RequestParam(value = "cardReference",required = false) String cardReference,
            @RequestParam("remarkType") TransactionRemarkType remarkType,
            @RequestBody UserCard userCard) {

        if (!processorAccountServiceOld.addUserCardTransactionRemark(transactionId, cardReference, userCard, remarkType)) {
            return Response.<String>builder()
                    .data(null)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message("Failed to add user card transaction remark.")
                    .build();
        }
        return Response.<String>builder()
                .data("OK")
                .status(Response.Status.OK)
                .message("OK")
                .build();

    }

    @RequestMapping(path="/saveusercard", method = RequestMethod.POST)
    public Response<Long> saveUserCard(
            @RequestParam("transactionId") Long transactionId,
            @RequestBody UserCard userCard)
    {
        try {
            Transaction transaction = transactionService.findById(transactionId);
            if (transaction == null) throw new Exception("Invalid transaction ID "+transactionId);

            ProcessorUserCard paymentMethod = processorAccountServiceOld.saveProcessorUserCard(transaction.getUser(), transaction.getCurrent().getProcessor(), userCard);
            return Response.<Long>builder()
                    .data(paymentMethod.getId())
                    .status(Response.Status.OK)
                    .message("OK")
                    .build();
        } catch (Exception e) {
            log.error("Failed to save user card. TransactionId: " + transactionId + "Card reference:" +userCard.getReference() + " Exception " + e.getMessage(), e);
            return Response.<Long>builder()
                    .data(null)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @RequestMapping(path="/processor-account-request", method = RequestMethod.GET)
    public Response<AccountProcessorRequest> getAccountProcessorRequest(
            @RequestParam("patx_id") Long transactionId)
    {
        try {
            return Response.<AccountProcessorRequest>builder()
                    .data(processorAccountAddService.createAccountProcessorRequest(transactionId))
                    .status(Response.Status.OK)
                    .message("OK")
                    .build();
        } catch (Exception e) {
            log.error("Failed get account processor request. ProcessorAccountTransactionId: " + transactionId + " Exception: " + e.getMessage(), e);
            return Response.<AccountProcessorRequest>builder()
                    .data(null)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @RequestMapping(path="/processor-account/transaction", method = RequestMethod.GET)
    public Response<lithium.service.cashier.client.objects.ProccesorAccountTransaction> getAccountProcessorTransaction(
            @RequestParam("patx_id") Long transactionId)
    {
        try {
            return Response.<lithium.service.cashier.client.objects.ProccesorAccountTransaction>builder()
                    .data(processorAccountAddService.getProcessorAccountTransaction(transactionId))
                    .status(Response.Status.OK)
                    .message("OK")
                    .build();
        } catch (Exception e) {
            log.error("Failed get account processor request. ProcessorAccountTransactionId: " + transactionId + " Exception: " + e.getMessage(), e);
            return Response.<lithium.service.cashier.client.objects.ProccesorAccountTransaction>builder()
                    .data(null)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @RequestMapping(path="/processor-account/save", method = RequestMethod.POST)
    public Response<Long> saveProcessorAccount(@RequestBody ProcessorAccountResponse processorAccountResponse)
    {
        try {
            if (processorAccountResponse == null || processorAccountResponse.getTransactionId() == null) {
                throw new Status400BadRequestException("Incorrect input data.");
            }
            ProcessorUserCard processorAccount = processorAccountAddService.saveProcessorAccount(processorAccountResponse, null);

            return Response.<Long>builder()
                    .data(Optional.ofNullable(processorAccount).map(ProcessorUserCard::getId).orElse(null))
                    .status(Response.Status.OK)
                    .message("OK")
                    .build();
        } catch (Status400BadRequestException e) {
            log.error("Failed to save processor account request: " + processorAccountResponse + " Exception: " + e.getMessage(), e);
            return Response.<Long>builder()
                    .data(null)
                    .status(Response.Status.BAD_REQUEST)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Failed to save processor account request: " + processorAccountResponse + " Exception: " + e.getMessage(), e);
            return Response.<Long>builder()
                    .data(null)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @RequestMapping(path="/usercard/default", method = RequestMethod.POST)
    public Response<String> setDefaultUserCard(
            @RequestParam("userGuid") String userGuid,
            @RequestParam("reference") String reference)
    {
        try {
            processorAccountServiceOld.setDefaultProcessorUserCard(userService.findOrCreate(userGuid), reference);
        } catch (Exception e) {
            log.error("Failed to set default user card. Card reference: " + reference + " Exception " + e.getMessage(), e);
            return Response.<String>builder()
                    .data(null)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
        return Response.<String>builder()
                .data("OK")
                .status(Response.Status.OK)
                .message("OK")
                .build();
    }

    @RequestMapping(path="/usercards", method = RequestMethod.GET)
    public Response<List<UserCard>> getUserCards(
            @RequestParam String methodCode, @RequestParam boolean deposit,
            @RequestParam String userName, @RequestParam String userGuid,
            @RequestParam String domainName, @RequestParam String ipAddress,
            @RequestParam String userAgent)
    {
        List<UserCard> userCards = null;
        try {
            userCards = processorAccountServiceOld.getUserCardsPerMethodCode(methodCode, deposit, userName, userGuid, domainName, ipAddress, userAgent);
        } catch (Exception e) {
            log.error("Failed to get user cards. Exception " + e.getMessage(), e);
            return Response.<List<UserCard>>builder()
                    .data(userCards)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
        return Response.<List<UserCard>>builder()
                .data(userCards)
                .status(Response.Status.OK)
                .message("OK")
                .build();
    }

    @RequestMapping(path="/usercard", method=RequestMethod.GET)
    public Response<UserCard> getUserCard(@RequestParam("cardReference")  String cardReference,
                                          @RequestParam("userGuid") String userGuid
    ) {
        UserCard userCard = null;
        try {
            log.debug("User card is requested for: cardReference: " + cardReference);

            userCard = processorAccountServiceOld.getUserCard(userService.findOrCreate(userGuid), cardReference);
        } catch (Exception e) {
            log.error("Failed to get user cards. Exception " + e.getMessage(), e);
            return Response.<UserCard>builder()
                    .data(null)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
        return Response.<UserCard>builder()
                .data(userCard)
                .status(Response.Status.OK)
                .message("OK")
                .build();

    }

    @RequestMapping(path="/check-card-owner", method=RequestMethod.GET)
    public Response<Boolean> checkCardOwner(
            @RequestParam("userGuid") String userGuid,
            @RequestParam("fingerprint")  String fingerprint,
            @RequestParam("isDeposit")  boolean isDeposit) {
        Boolean isSuccess = false;
        try {
            log.debug("User card is requested for: fingerprint: " + fingerprint);

            isSuccess = duplicateAccountVerifier.verify(userGuid, fingerprint, null, isDeposit);
        } catch (Exception e) {
            log.error("Failed to get user card for fingerprint: " + fingerprint +" Exception " + e.getMessage(), e);
            return Response.<Boolean>builder()
                    .data(null)
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
        return Response.<Boolean>builder()
                .data(isSuccess)
                .status(Response.Status.OK)
                .message("OK")
                .build();

    }

    @RequestMapping(path="/usercurrency", method=RequestMethod.GET)
    public Response<String> getCurrency(
            @RequestParam("domainName") String domainName
    ) {
        try {
            return Response.<String>builder()
                    .data(userService.retrieveDomainFromDomainService(domainName).getCurrency())
                    .status(Response.Status.OK)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get user currency. Exception " + e.getMessage(), e);
            return Response.<String>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @RequestMapping(path="/get-processor-accounts-per-user", method=RequestMethod.GET)
    public Response<List<ProcessorAccount>> getProcessorAccountsPerUser(@RequestParam("domainName") String domainName,
                                                                        @RequestParam("userGuid") String userGuid,	@RequestParam("type") String type) {
        try {
            return Response.<List<ProcessorAccount>>builder()
                    .data(processorAccountService.getProcessorAccountsPerUserAndType(userGuid, type))
                    .status(Response.Status.OK)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get processor account per user (" + domainName + ", " + userGuid + ", " +  type + ") due " + e.getMessage(), e);
            return Response.<List<ProcessorAccount>>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @RequestMapping(path="/get-processor-accounts", method=RequestMethod.GET)
    public Response<List<ProcessorAccount>> getProcessorAccounts(@RequestParam("domainName") String domainName, @RequestParam("reference") String reference,
                                                                           @RequestParam("type") String type) {
        try {
            return Response.<List<ProcessorAccount>>builder()
                    .data(processorAccountService.getProcessorAccounts(domainName, reference, type))
                    .status(Response.Status.OK)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get processor account by reference (" + domainName + ", " + reference + ", " +  type + ") due " + e.getMessage(), e);
            return Response.<List<ProcessorAccount>>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    //this is called in paypal only and should not be reused
    //processor response should be updated with processor account if needed
    @Deprecated
	@RequestMapping(path="/update-processor-account", method=RequestMethod.POST)
    public Response updateProcessorAccount(@RequestBody ProcessorAccount processorAccount) {
        try {
            processorAccountService.updateProcessorAccount(processorAccount.getId(), null,  null,  processorAccount.getProviderData(),  processorAccount.isHideInDeposit(), null, null, null, null, "", null);
            return Response.builder()
                    .status(Response.Status.OK)
                    .build();
        } catch (Exception e) {
            log.error("Failed to update processor account due " + e.getMessage(), e);
            return Response.<ProcessorAccount>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }
    @RequestMapping(path="/get-processor-accounts-by-reference", method=RequestMethod.GET)
    public Response<List<ProcessorAccount>> getProcessorAccountsByReference(@RequestParam("reference") String reference) {
        try {
            return Response.<List<ProcessorAccount>>builder()
                .data(processorAccountService.getProcessorAccountsByReference(reference))
                .status(Response.Status.OK)
                .build();
        } catch (Exception e) {
            log.error("Failed to get processor account by reference (" + reference + ") due " + e.getMessage(), e);
            return Response.<List<ProcessorAccount>>builder()
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
        }
    }

    @RequestMapping(path="/update-expired-user-card", method=RequestMethod.POST)
    public Response updateExpiredUserCard(@RequestBody ProcessorAccount processorAccount) {
        try {
            processorAccountService.updateExpiredUserCard(processorAccount);
            return Response.builder()
                .status(Response.Status.OK)
                .build();
        } catch (Exception e) {
            log.error("Failed to update processor account due " + e.getMessage(), e);
            return Response.<ProcessorAccount>builder()
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
        }
    }

	@RequestMapping(path="/get-processor-account-by-id", method=RequestMethod.GET)
	public Response<ProcessorAccount> getProcessorAccountById(@RequestParam("id") long processorAccountId) {
		try {
			return Response.<ProcessorAccount>builder()
					.data(processorAccountService.getProcessorAccountById(processorAccountId))
					.status(Response.Status.OK)
					.build();
		} catch (Exception e) {
			log.error("Failed to get processor account by Id (" + processorAccountId + ") due " + e.getMessage(), e);
			return Response.<ProcessorAccount>builder()
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage())
					.build();
		}
	}

    @RequestMapping(path="/get-contra-account", method=RequestMethod.GET)
    public Response<ProcessorAccount> getContraAccount(@RequestParam("userGuid") String userGuid) {
        try {
            return Response.<ProcessorAccount>builder()
                .data(processorAccountService.getVerifiedContraProcessorAccount(userGuid))
                .status(Response.Status.OK)
                .build();
        } catch (Exception e) {
            log.error("Failed to get user: " + userGuid + " contra account. Exception: " + e.getMessage(), e);
            return Response.<ProcessorAccount>builder()
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
        }
    }
}
