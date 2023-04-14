package lithium.service.cashier.services;

import lithium.report.XlsReport;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.TransactionPaymentType;
import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.cashier.data.entities.backoffice.CashierTransactionBO;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class ResultBatchExcelService {

	@Autowired
	LithiumServiceClientFactory serviceFactory;

	public void xls(List<CashierTransactionBO> transactions, OutputStream outputStream) throws Exception {
		log.debug("Generating excel for "+transactions.size()+" trans.");
		XlsReport report = new XlsReport("name"); //TODO:
		report.run(outputStream, () -> {
			transactionSheet(transactions, report);
		});
	}

	private void transactionSheet(List<CashierTransactionBO> transactions, XlsReport report) {
		report.sheet("Cashier Transactions", Arrays.asList(1,2), () -> {
			report.columnHeading("ID");
			report.columnHeading("Date_Created");
			report.columnHeading("Date_Updated");
			report.columnHeading("Type");
			report.columnHeading("Processor");
			report.columnHeading("Method");
			report.columnHeading("Payment Type");
			report.columnHeading("Amount");
			report.columnHeading("Player");
			report.columnHeading("Descriptor");
			report.columnHeading("Status");
			report.columnHeading("Decline Reason");
			report.columnHeading("Processor Reference");
			report.columnHeading("Additional Reference");
			report.columnHeading("Is Test Account");
			report.columnHeading("Auto Approved");
			report.columnHeading("Reviewed By");
			report.columnHeading("Account Info");
			report.columnHeading("Bonus Code");
			report.columnHeading("Bonus Id");
			report.columnHeading("Runtime (sec)");
		}, () -> {
			for (CashierTransactionBO t:transactions) {
				report.row(() -> {
					//"ID"
					report.cellNumeric(t.getId());
					//"Date_Created"
					report.cellDateTime(t.getCreatedOn());
					//"Date_Updated"
					report.cellDateTime(t.getCurrent().getTimestamp());
					//"Type"
					report.cell(t.getTransactionType().name());
					//"Processor"
					report.cell(
							ofNullable(t.getCurrent().getProcessor())
									.map(DomainMethodProcessor::getDescription)
									.orElse(""));
					//"Method"
					report.cell(t.getDomainMethod().getName());
					//"Payment Type"
					report.cell(
							ofNullable(t.getTransactionPaymentType())
									.map(TransactionPaymentType::getPaymentType)
									.orElse(null));
					//"Amount"
					report.cellCents(t.getAmountCents(), t.getCurrencyCode());
					//"Player"
					report.cell(t.getUser().guid());
					//"Descriptor"
					report.cell(
							ofNullable(t.getPaymentMethod())
									.map(ProcessorUserCard::getLastFourDigits)
									.orElse("N/A"));
					//"Status"
					report.cell(
							ofNullable(t.getCurrent().getStatus())
									.map(TransactionStatus::getCode)
									.orElse(""));
					//"Decline Reason"
					report.cell(t.getDeclineReason());
					//"Processor Reference"
					report.cell(t.getProcessorReference());
					//"Additional Reference"
					report.cell(t.getAdditionalReference());
					//"Is Test Account"
					report.cell((t.isTestAccount()) ? "yes" : "no");
					//"Auto Approved"
					report.cell((t.isAutoApproved()) ? "yes" : "no");
					//"Reviewed By"
					report.cell(t.getReviewedByFullName());
					//"Account Info"
					report.cell(t.getAccountInfo());
					//"Bonus Code"
					report.cell(t.getBonusCode());
					//"Bonus Id"
					report.cellNumeric(t.getBonusId());
					//"Runtime (sec)"
					report.cellNumeric(t.getRuntime());
				});
			}
		});
	}
}
