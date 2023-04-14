package lithium.csv.cashier.transactions.provider.data;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lithium.service.document.generation.client.objects.CsvContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashierTransactionCsv implements CsvContent {
    @CsvBindByName(column = "ID")
    @CsvBindByPosition(position = 0)
    private String id;
    @CsvBindByName(column = "Date_Created")
    @CsvBindByPosition(position = 1)
    private String createdOn;
    @CsvBindByName(column = "Date_Updated")
    @CsvBindByPosition(position = 2)
    private String updatedOn;
    @CsvBindByName(column = "Type")
    @CsvBindByPosition(position = 3)
    private String transactionType;
    @CsvBindByName(column = "Processor")
    @CsvBindByPosition(position = 4)
    private String processorName;
    @CsvBindByName(column = "Method")
    @CsvBindByPosition(position = 5)
    private String domainMethodName;
    @CsvBindByName(column = "Payment Type")
    @CsvBindByPosition(position = 6)
    private String transactionPaymentType;
    @CsvBindByName(column = "Amount")
    @CsvBindByPosition(position = 7)
    private String amount;
    @CsvBindByName(column = "Player")
    @CsvBindByPosition(position = 8)
    private String guid;
    @CsvBindByName(column = "Descriptor")
    @CsvBindByPosition(position = 9)
    private String descriptor;
    @CsvBindByName(column = "Status")
    @CsvBindByPosition(position = 10)
    private String status;
    @CsvBindByName(column = "Decline Reason")
    @CsvBindByPosition(position = 11)
    private String declineReason;
    @CsvBindByName(column = "Processor Reference")
    @CsvBindByPosition(position = 12)
    private String processorReference;
    @CsvBindByName(column = "Additional Reference")
    @CsvBindByPosition(position = 13)
    private String additionalReference;
    @CsvBindByName(column = "Is Test Account")
    @CsvBindByPosition(position = 14)
    private String testAccount;
    @CsvBindByName(column = "Auto Approved")
    @CsvBindByPosition(position = 15)
    private String autoApproved;
    @CsvBindByName(column = "Reviewed By")
    @CsvBindByPosition(position = 16)
    private String reviewedByFullName;
    @CsvBindByName(column = "Account Info")
    @CsvBindByPosition(position = 17)
    private String accountInfo;
    @CsvBindByName(column = "Bonus Code")
    @CsvBindByPosition(position = 18)
    private String bonusCode;
    @CsvBindByName(column = "Bonus Id")
    @CsvBindByPosition(position = 19)
    private String bonusId;
    @CsvBindByName(column = "Runtime (sec)")
    @CsvBindByPosition(position = 20)
    private String runtime;
}
