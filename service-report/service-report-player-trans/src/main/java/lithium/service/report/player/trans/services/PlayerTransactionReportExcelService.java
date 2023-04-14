package lithium.service.report.player.trans.services;

import java.io.IOException;
import java.io.OutputStream;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import lithium.report.XlsReport;
import lithium.service.report.player.trans.data.entities.PlayerTransaction;
import lithium.service.report.player.trans.data.entities.PlayerTransactionQueryCriteria;

@Service
public class PlayerTransactionReportExcelService {
	@Autowired PlayerTransactionReportService playerTranReportService;
	
	public void xls(String userGuid, DateTime sDate, DateTime eDate, OutputStream outputStream) throws IOException {
		XlsReport report = new XlsReport(userGuid+"_"+sDate.toString("ddMMMyyyy")+"_"+eDate.toString("ddMMMyyyy"));
		report.run(outputStream, () -> {
			report.sheet("data", () -> {
				report.columnHeading("Transaction Id");
				report.columnHeading("Transaction Entry Id");
				report.columnHeading("Transaction Date");
				report.columnHeading("Transaction Type");
				report.columnHeading("Transaction Currency");
				report.columnHeading("Transaction amount");
				report.columnHeading("Account Type");
				report.columnHeading("Account Code");
				report.columnHeading("Account Balance");
				report.columnHeading("Provider Guid");
				report.columnHeading("Provider Transaction Id");
				report.columnHeading("Game Name");
				report.columnHeading("Game Guid");
				report.columnHeading("Bonus Revision");
				report.columnHeading("Bonus Name");
				report.columnHeading("Bonus Code");
				report.columnHeading("Bonus Player History Id");
				report.columnHeading("Processing Method");
				report.columnHeading("User Guid");
				report.columnHeading("Accounting Client Transaction Id");
				report.columnHeading("Accounting Client External Id");
			}, () -> {

				PlayerTransactionQueryCriteria criteria = playerTranReportService.findQueryCriteria(sDate, eDate, userGuid);
				Pageable pageRequest = PageRequest.of(0, 5, Direction.ASC, new String[] {"tranId"});
				Page<PlayerTransaction> pageResult = null;
				
				do {
					pageResult = playerTranReportService.getPlayerTranPage(sDate, eDate, userGuid, criteria.getId(), pageRequest);
					for (PlayerTransaction result: pageResult.getContent()) {
						report.row(() -> {
							report.cellNumeric(result.getTranId());
							report.cellNumeric(result.getTranEntryId());
							report.cellDateTime(result.getTranEntryDate());
							report.cell(result.getTranType());
							report.cell(result.getTranCurrency());
							report.cellCents(result.getTranEntryAmount(), result.getTranEntryAmount() > 10000 || result.getTranEntryAmount() < -10000 ? true : false, result.getTranCurrency()); //Tran amount highlight rule (this needs some more work but will do for now
							report.cell(result.getTranEntryAccountType());
							report.cell(result.getTranEntryAccountCode());
							report.cellCents(result.getTranEntryAccountBalance());
							report.cell(result.getProviderGuid());
							report.cell(result.getExternalTranId());
							report.cell(result.getGameName());
							report.cell(result.getGameGuid());
							report.cellNumeric(result.getBonusRevisionId());
							report.cell(result.getBonusName());
							report.cell(result.getBonusCode());
							report.cellNumeric(result.getPlayerBonusHistoryId());
							report.cell(result.getProcessingMethod());
							report.cell(result.getUserGuid());
							report.cell(result.getAccountingClientTranId());
							report.cell(result.getAccountingClientExternalId());
						});
						
					}
					pageRequest = pageRequest.next();
				} while (!pageResult.isLast());
				
			});
		});
	}
}