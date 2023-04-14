package lithium.service.cashier.mock.hexopay.controllers;

import lithium.service.cashier.mock.hexopay.services.Simulator;
import lithium.service.cashier.processor.hexopay.api.gateway.PaymentRequest;
import lithium.service.cashier.processor.hexopay.api.gateway.PayoutRequest;
import lithium.service.cashier.processor.hexopay.api.gateway.TransactionResponse;
import lithium.service.cashier.processor.hexopay.api.gateway.TransactionsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class GatewayController {
    @Autowired
    Simulator simulator;

    @PostMapping("/transactions/payments")
    public TransactionResponse payments(@RequestBody PaymentRequest paymentRequest) throws Exception
    {
        TransactionResponse response = new TransactionResponse();
        response.setTransaction(simulator.simulatePayment(paymentRequest.getRequest()));
        return response;
    }

    @PostMapping("/transactions/payouts")
    public TransactionResponse payouts(@RequestBody PayoutRequest payoutRequest) throws Exception
    {
        TransactionResponse response = new TransactionResponse();
        response.setTransaction(simulator.simulatePayout(payoutRequest.getRequest()));
        return response;
    }

    @GetMapping("/v2/transactions/tracking_id/{trackingId}")
    public TransactionsResponse getTransactions(@PathVariable(name="trackingId") String trackingId)
    {
        TransactionsResponse transactions = new TransactionsResponse();
        transactions.setTransactions(simulator.getTransactionsByTrackingId(trackingId));
        return transactions;
    }

    @GetMapping("/transactions/{uid}")
    public TransactionResponse getTransaction(@PathVariable(name="uid") String uid)
    {
        TransactionResponse transactions = new TransactionResponse();
        transactions.setTransaction(simulator.getTransactionsByUid(uid));
        return transactions;
    }

}
