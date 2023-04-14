package lithium.service.cashier.mock.paynl.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.mock.paynl.data.errors.Errors;
import lithium.service.cashier.mock.paynl.data.exceptions.Status401UnauthorizedException;
import lithium.service.cashier.mock.paynl.repositories.TransactionRepository;
import lithium.service.cashier.processor.paynl.data.request.PayoutRequest;
import lithium.service.cashier.processor.paynl.data.response.PayoutResponse;
import lithium.service.cashier.processor.paynl.data.response.PayoutStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PayoutService {
    @Value("${lithium.service.cashier.mock.paynl.Authorization}")
    private String authorization;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ErrorsService errorsService;

    @Autowired
    private SimulatorService simulatorService;
    
    @Autowired
    private ObjectMapper mapper;

    public ResponseEntity<String> createPayout(String authorization, PayoutRequest request) {
        try {
            checkAuth(authorization);

            Errors errors = errorsService.handleErrors(request);
            if (errors != null) {
                log.info("errors: " + errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapper.writeValueAsString(errors));
            }
            PayoutResponse payoutResponse = simulatorService.createPayout(request);
            log.info("payout response: " + payoutResponse);
            return ResponseEntity.status(HttpStatus.OK).body(mapper.writeValueAsString(payoutResponse));
        } catch (Status401UnauthorizedException e) {
            log.error("Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (JsonProcessingException e) {
            log.error("Unable to map Payout object to String");
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<String> verifyPayout(String authorization, String transactionId) {
        try {
            checkAuth(authorization);

            lithium.service.cashier.mock.paynl.data.entities.Transaction transaction = transactionRepository.findTransactionById(transactionId);
            if (transaction == null) {
                Errors errors = errorsService.simulateIncorrectTransactionIdError(transactionId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapper.writeValueAsString(errors));
            }
            PayoutStatusResponse payoutStatusResponse = simulatorService.createPayoutResponse(transaction);
            return ResponseEntity.ok().body(mapper.writeValueAsString(payoutStatusResponse));
        } catch (Status401UnauthorizedException e) {
            log.error("Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (JsonProcessingException e) {
            log.error("Unable to map Payout Status object to String");
            return ResponseEntity.badRequest().build();
        }
    }

    private void checkAuth(String basicAuth) throws Status401UnauthorizedException {
        log.info("Authorization with basic auth token: " + basicAuth);
        if (!basicAuth.equals(authorization)) {
            log.error("Invalid basic auth token.");
            throw new Status401UnauthorizedException("unauthorized");
        }
        try {
            String decoded = new String(Base64.decodeBase64(basicAuth.substring(5)));
            String[] pair = decoded.split(":");
            String username = pair[0];
            log.info("Username " + username + " has authorized.");
        } catch (Exception e) {
            log.error("Unable to decode basic auth token");
            throw new Status401UnauthorizedException("unauthorized");
        }
    }

}
