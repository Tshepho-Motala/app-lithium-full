package lithium.service.cashier.controllers.backoffice;

import lithium.service.Response;
import lithium.service.cashier.client.objects.enums.TransactionTagType;
import lithium.service.cashier.services.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/backoffice/cashier")
public class TransactionTagsController {

    private final TransactionService transactionService;

    @GetMapping("/transaction-tags-list")
    public Response<List<String>> getTransactionTagsFilterNames() {

        List<String> result = new ArrayList<>();
        for (TransactionTagType key : TransactionTagType.values()) {
            result.add(key.getName());
        }
        return Response.<List<String>>builder().data(result).build();
    }

    @PostMapping("/transactions/{transactionId}/tags/{tag}")
    public Response<Void> setTransactionTag (
            @PathVariable("transactionId") Long transactionId,
            @PathVariable(name = "tag") String tagName
    ) {
        transactionService.addTransactionTagByName(transactionId, tagName);
        return Response.<Void>builder().status(Response.Status.OK).build();
    }

    @DeleteMapping("/transactions/{transactionId}/tags/{tag}")
    public Response<Void> removeTransactionTag (
            @PathVariable("transactionId") Long transactionId,
            @PathVariable(name = "tag") String tagName
    ) {
        transactionService.removeTransactionTagByName(transactionId, tagName);
        return Response.<Void>builder().status(Response.Status.OK).build();
    }
}
