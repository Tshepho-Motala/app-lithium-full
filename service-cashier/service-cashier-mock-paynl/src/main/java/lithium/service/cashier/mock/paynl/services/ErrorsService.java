package lithium.service.cashier.mock.paynl.services;

import lithium.service.cashier.mock.paynl.data.enums.Scenario;
import lithium.service.cashier.mock.paynl.data.errors.Amount;
import lithium.service.cashier.mock.paynl.data.errors.Errors;
import lithium.service.cashier.mock.paynl.data.errors.IBan;
import lithium.service.cashier.mock.paynl.data.errors.Payment;
import lithium.service.cashier.mock.paynl.data.errors.Transaction;
import lithium.service.cashier.processor.paynl.data.enums.ErrorCodes;
import lithium.service.cashier.processor.paynl.data.request.PayoutRequest;
import lithium.service.cashier.processor.paynl.data.response.Links;
import lithium.service.cashier.processor.paynl.exceptions.Error;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
public class ErrorsService {

    public Errors handleErrors(PayoutRequest request) {
        Errors errors = new Errors();
        checkForTransactionTypeError(request, errors);
        checkForTransactionServiceIdError(request, errors);
        checkForCurrencyError(request, errors);
        checkForAmountError(request, errors);
        checkForPaymentMethodError(request, errors);
        checkForPaymentIBanNumberError(request, errors);
        checkForPaymentIBanHolderError(request, errors);
        simulateErrors(request, errors);
        if (!ObjectUtils.isEmpty(errors.getGeneral()) || !ObjectUtils.isEmpty(errors.getTransaction()) || !ObjectUtils.isEmpty(errors.getPayment())) {
            errors.setLinks(Arrays.asList(Links.builder().url("/payout").rel("self").type("POST").build()));
            return errors;
        }
        return null;
    }

    private void checkForTransactionTypeError(PayoutRequest request, Errors errors) {
        String type = request.getTransaction().getType();
        if (type == null) {
            Error error = Error.builder().code(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getCode()).message(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getMessage()).build();
            if (errors.getTransaction() != null) {
                errors.getTransaction().setType(error);
            } else {
                errors.setTransaction(Transaction.builder().type(error).build());
            }
        } else if (!type.equals("MIT") && !type.equals("CIT")) {
            Error error = Error.builder().code(ErrorCodes.INVALID_TRANSACTION_TYPE.getCode()).message(ErrorCodes.INVALID_TRANSACTION_TYPE.getMessage()).build();
            if (errors.getTransaction() != null) {
                errors.getTransaction().setType(error);
            } else {
                errors.setTransaction(Transaction.builder().type(error).build());
            }
        }
    }

    private void checkForTransactionServiceIdError(PayoutRequest request, Errors errors) {
        String serviceId = request.getTransaction().getServiceId();
        if (serviceId == null) {
            Error error = Error.builder().code(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getCode()).message(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getMessage()).build();
            if (errors.getTransaction() != null) {
                errors.getTransaction().setServiceId(error);
            } else {
                errors.setTransaction(Transaction.builder().serviceId(error).build());
            }
        }
    }

    private void checkForCurrencyError(PayoutRequest request, Errors errors) {
        String currency = request.getTransaction().getAmount().getCurrency();
        if (currency == null) {
            Error error = Error.builder().code(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getCode()).message(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getMessage()).build();
            if (errors.getTransaction() != null) {
                if (errors.getTransaction().getAmount() != null) {
                    errors.getTransaction().getAmount().setCurrency(error);
                } else {
                    errors.getTransaction().setAmount(Amount.builder().currency(error).build());
                }
            } else {
                Amount amount = Amount.builder().currency(error).build();
                Transaction transaction = Transaction.builder().amount(amount).build();
                errors.setTransaction(transaction);
            }
        } else if (!currency.equals("EUR") && !currency.equals("USD")) {
            Error error = Error.builder().code(ErrorCodes.INVALID_CURRENCY.getCode()).message(ErrorCodes.INVALID_CURRENCY.getMessage()).build();
            if (errors.getTransaction() == null) {
                errors.setTransaction(Transaction.builder().amount(Amount.builder().currency(error).build()).build());
            } else if (errors.getTransaction().getAmount() == null) {
                errors.getTransaction().setAmount(Amount.builder().currency(error).build());
            } else {
                errors.getTransaction().getAmount().setCurrency(error);
            }
        }
    }

    private void checkForAmountError(PayoutRequest request, Errors errors) {
        if (request.getTransaction().getAmount().getValue() == null) {
            Error error = Error.builder().code(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getCode()).message(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getMessage()).build();
            if (errors.getTransaction() != null) {
                if (errors.getTransaction().getAmount() != null) {
                    errors.getTransaction().getAmount().setValue(error);
                } else {
                    errors.getTransaction().setAmount(Amount.builder().value(error).build());
                }
            } else {
                Amount amount = Amount.builder().value(error).build();
                Transaction transaction = Transaction.builder().amount(amount).build();
                errors.setTransaction(transaction);
            }
        }
    }

    private void checkForPaymentMethodError(PayoutRequest request, Errors errors) {
        if (request.getPayment().getMethod() == null) {
            Error error = Error.builder().code(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getCode()).message(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getMessage()).build();
            if (errors.getPayment() != null) {
                errors.getPayment().setMethod(error);
            } else {
                errors.setPayment(Payment.builder().method(error).build());
            }
        }
    }

    private void checkForPaymentIBanNumberError(PayoutRequest request, Errors errors) {
        if (request.getPayment().getIBan().getNumber() == null) {
            Error error = Error.builder().code(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getCode()).message(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getMessage()).build();
            if (errors.getPayment() != null) {
                if (errors.getPayment().getIBan() != null) {
                    errors.getPayment().getIBan().setNumber(error);
                } else {
                    errors.getPayment().setIBan(IBan.builder().number(error).build());
                }
            } else {
                errors.setPayment(Payment.builder().iBan(IBan.builder().number(error).build()).build());
            }
        }
    }

    private void checkForPaymentIBanHolderError(PayoutRequest request, Errors errors) {
        if (request.getPayment().getIBan().getHolder() == null) {
            Error error = Error.builder().code(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getCode()).message(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getMessage()).build();
            if (errors.getPayment() != null) {
                if (errors.getPayment().getIBan() != null) {
                    errors.getPayment().getIBan().setHolder(error);
                } else {
                    errors.getPayment().setIBan(IBan.builder().holder(error).build());
                }
            } else {
                errors.setPayment(Payment.builder().iBan(IBan.builder().holder(error).build()).build());
            }
        }
    }

    public Errors simulateIncorrectTransactionIdError(String transactionId) {
        Error error = Error.builder().code(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getCode()).message(ErrorCodes.MANDATORY_FIELD_IS_MISSING.getMessage()).build();
        Errors errors = Errors.builder().transactionId(error).build();
        Links links = Links.builder().url("/transactions/" + transactionId).rel("self").type("GET").build();
        errors.setLinks(Arrays.asList(links));
        return errors;
    }

    private void simulateErrors(PayoutRequest request, Errors errors) {
        if (request.getTransaction().getAmount() != null && request.getTransaction().getAmount().getValue() != null) {
            Long value = new BigDecimal(request.getTransaction().getAmount().getValue()).longValue();
            Scenario scenario = Scenario.getByAmount(value);
            switch (scenario) {
                case GENERAL_ERROR:
                    errors.setGeneral(Error.builder().code(ErrorCodes.SERVICE_NOT_FOUND.getCode()).message(ErrorCodes.SERVICE_NOT_FOUND.getMessage()).build());
                    break;
                case MULTIPLE_ERRORS:
                    errors.setGeneral(Error.builder().code(ErrorCodes.TRANSACTION_NOT_FOUND.getCode()).message(ErrorCodes.TRANSACTION_NOT_FOUND.getMessage()).build());
                    errors.setPayment(Payment.builder().method(Error.builder().code(ErrorCodes.HOLDER_NAME_IS_NOT_PROVIDED.getCode()).message(ErrorCodes.HOLDER_NAME_IS_NOT_PROVIDED.getMessage()).build()).build());
                    errors.setTransaction(Transaction.builder().type(Error.builder().code(ErrorCodes.INVALID_TRANSACTION_TYPE.getCode()).message(ErrorCodes.INVALID_TRANSACTION_TYPE.getMessage()).build()).build());
                    break;
                case EMPTY_ERROR:
                    errors.setTransaction(Transaction.builder().build());
                    break;
            }
        }
    }
}