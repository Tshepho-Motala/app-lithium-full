package lithium.service.cashier.controllers.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.TransactionFilterRequest;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.transaction.dto.CashierClientTransactionDTO;
import lithium.service.cashier.client.system.TransactionClient;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.services.TransactionService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.service.UserApiInternalClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/transaction")
public class SystemTransactionController implements TransactionClient {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserApiInternalClientService userApiInternalClientService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/first-deposit")
    public CashierClientTransactionDTO findFirstDeposit(String userGuid) {
        return objectMapper.convertValue(
                transactionService.findFirstTransaction(userGuid, TransactionType.DEPOSIT, DoMachineState.SUCCESS.name()),
                CashierClientTransactionDTO.class);
    }

    @Override
    public void findFirstDomain(String domainName) {

    }

    @GetMapping("/last-deposit")
    public CashierClientTransactionDTO findLastDeposit(String userGuid) {
        return objectMapper.convertValue(
            transactionService.findLastTransaction(userGuid, TransactionType.DEPOSIT, DoMachineState.SUCCESS.name()),
            CashierClientTransactionDTO.class);
    }

    @PostMapping("/search")
    public DataTableResponse<CashierClientTransactionDTO> searchTransactionsByFilter(
            @RequestBody TransactionFilterRequest filterRequest, @RequestParam Integer page, @RequestParam Integer size
    ) {
        DataTableRequest dtRequest = new DataTableRequest();
        PageRequest pageRequest = PageRequest.of(page, size);
        dtRequest.setSearchValue(filterRequest.getSearchValue());
        dtRequest.setPageRequest(pageRequest);

        Page<Transaction> transactionsPage = transactionService.findByFilter(dtRequest, filterRequest);

        Page<CashierClientTransactionDTO> pageTransactionsDTO = transactionsPage.map(this::buildTransactionDTO);

        return new DataTableResponse<>(
                dtRequest,
                pageTransactionsDTO,
                pageTransactionsDTO.getTotalElements(),
                pageTransactionsDTO.getPageable().getPageNumber(),
                pageTransactionsDTO.getTotalPages()
        );
    }

    private CashierClientTransactionDTO buildTransactionDTO(Transaction transaction) {

        CashierClientTransactionDTO transactionDTO = objectMapper.convertValue(transaction, CashierClientTransactionDTO.class);

        User reviewedBy = transaction.getReviewedBy();
        String reviewedByName = null;
        if (reviewedBy != null) {
            reviewedByName = userApiInternalClientService.getUserName(transaction.getReviewedBy().guid());
        }

        transactionDTO.setReviewedByFullName(reviewedByName);

        return transactionDTO;
    }
}
