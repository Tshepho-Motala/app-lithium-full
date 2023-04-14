package lithium.csv.cashier.transactions.provider.test;

import lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams;
import lithium.service.cashier.client.objects.TransactionFilterRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static java.lang.reflect.Modifier.isStatic;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.ACTIVE_PAYMENT_METOD_COUNT;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.ADDITIONAL_REFERENCE;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.CREED;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.CRESD;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.DAYS_SINCE_FIRST_DEPOSIT;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.DECLINE_REASON;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.DEPOSIT_COUNT;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.DM;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.DMP;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.DOMAIN;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.EXCLUDED_TRANSACTION_TAGS_NAMES;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.GUID;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.ID;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.INCLUDED_TRANSACTION_TAGS_NAMES;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.LAST_FOUR_DIGITS;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.PAYMENT_TYPE;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.PROCESSOR_REFERENCE;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.REGISTRATION_END;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.REGISTRATION_START;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.SEARCH;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.STATUS;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.STATUSES;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.TEST_ACCOUNT;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.TRANSACTION_AMOUNT;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.TRANSACTION_RUNTIME_QUERY;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.TRANSACTION_TYPE;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.UPDED;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.UPDSD;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.USER_STATUS_IDS;
import static lithium.csv.cashier.transactions.provider.services.CashierTransactionRequestParams.USER_TAG_IDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CashierTransactionRequestParamsTest {

    /**
     * The specified field of TransactionFilterRequest not processed by the service-csv-provider-cashier-transaction.
     * To fix:
     * 1. Add field processing to CashierTransactionRequestParams::buildTransactionFilter
     * 2. Set field into CashierTransactionRequestParamsTest::getCompleteCashierParams
     */
    @Test
    public void shouldPopulateAllFields(){

        CashierTransactionRequestParams params = new CashierTransactionRequestParams(getCompleteCashierParams());

        TransactionFilterRequest actualRequest = params.buildTransactionFilter();
        assertThat(actualRequest).hasNoNullFieldsOrProperties();
    }

    @Test
    public void shouldOverrideAllDefaults() throws IllegalAccessException {

        CashierTransactionRequestParams params = new CashierTransactionRequestParams(getCompleteCashierParams());

        TransactionFilterRequest actualRequest = params.buildTransactionFilter();

        TransactionFilterRequest defaultFilterRequest = new TransactionFilterRequest();

        for (Field field : defaultFilterRequest.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(defaultFilterRequest);
            if (value != null && !isStatic(field.getModifiers())) {
                assertThat(value)
                        .withFailMessage("Field: \\\"%s\\\" of TransactionFilterRequest not processed by the service-csv-provider-cashier-transaction.\n" +
                                "To fix:\n" +
                                "1. Add field processing to CashierTransactionRequestParams::buildTransactionFilter\n" +
                                "2. Set field into CashierTransactionRequestParamsTest::getCompleteCashierParams", field.getName())
                        .isNotEqualTo(field.get(actualRequest));
            }
        }
    }

    public static Map<String, String> getCompleteCashierParams() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(DMP, "5");
        parameters.put(DM, "6");
        parameters.put(DOMAIN, "livescore_nigeria");
        parameters.put(TRANSACTION_TYPE, "deposit");
        parameters.put(STATUSES, "SUCCESS");
        parameters.put(SEARCH, "12");
        parameters.put(CRESD, "0");
        parameters.put(CREED, "0");
        parameters.put(UPDSD, "0");
        parameters.put(UPDED, "0");
        parameters.put(REGISTRATION_START, "0");
        parameters.put(REGISTRATION_END, "0");
        parameters.put(PROCESSOR_REFERENCE, "processorReference");
        parameters.put(ADDITIONAL_REFERENCE, "additionalReference");
        parameters.put(STATUS, "START");
        parameters.put(PAYMENT_TYPE, "ussd");
        parameters.put(DECLINE_REASON, "declineReason");
        parameters.put(LAST_FOUR_DIGITS, "0458");
        parameters.put(ID, "id_16");
        parameters.put(TRANSACTION_RUNTIME_QUERY, "transactionRuntimeQuery");
        parameters.put(TEST_ACCOUNT, "false");
        parameters.put(GUID, "1245/livescore_nigeria");
        parameters.put(INCLUDED_TRANSACTION_TAGS_NAMES, "FIRST_DEPOSIT");
        parameters.put(EXCLUDED_TRANSACTION_TAGS_NAMES, "AUTO_APPROVED");
        parameters.put(DEPOSIT_COUNT, ">=2");
        parameters.put(DAYS_SINCE_FIRST_DEPOSIT, ">=2");
        parameters.put(TRANSACTION_AMOUNT, ">=200.00");
        parameters.put(ACTIVE_PAYMENT_METOD_COUNT, ">=1");
        parameters.put(USER_STATUS_IDS, "1,2");
        parameters.put(USER_TAG_IDS, "1,2");
        return parameters;
    }

}
