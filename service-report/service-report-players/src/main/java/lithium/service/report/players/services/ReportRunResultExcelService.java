package lithium.service.report.players.services;

import lithium.report.XlsReport;
import lithium.service.report.players.data.entities.ReportRun;
import lithium.service.report.players.data.entities.ReportRunResults;
import lithium.service.report.players.data.repositories.ReportRunResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
public class ReportRunResultExcelService {
	@Autowired ReportRunResultsRepository repoRunResults;
	
	public void xls(ReportRun run, OutputStream outputStream) throws IOException {
		XlsReport report = new XlsReport(run.getReportRevision().getName());
		report.run(outputStream, () -> {
			report.sheet("data", () -> {
				report.columnHeading("Username");
				report.columnHeading("Email");
				report.columnHeading("First Name");
				report.columnHeading("Last Name");
				report.columnHeading("Enabled");
				report.columnHeading("Status");
				report.columnHeading("Email Validated");
				report.columnHeading("R. Address Line 1");
				report.columnHeading("R. Address Line 2");
				report.columnHeading("R. Address Line 3");
				report.columnHeading("R. Address City");
				report.columnHeading("R. Address Region");
				report.columnHeading("R. Address Country");
				report.columnHeading("R. Address Postal Code");
				report.columnHeading("P. Address Line 1");
				report.columnHeading("P. Address Line 2");
				report.columnHeading("P. Address Line 3");
				report.columnHeading("P. Address City");
				report.columnHeading("P. Address Region");
				report.columnHeading("P. Address Country");
				report.columnHeading("P. Address Postal Code");
				report.columnHeading("Tel. Number");
				report.columnHeading("Cel. Number");
				report.columnHeading("Created");
				report.columnHeading("Updated");
				report.columnHeading("Signup Bonus Code");
				report.columnHeading("D.O.B.");
				report.columnHeading("D.O.B. Day");
				report.columnHeading("D.O.B. Month");
				report.columnHeading("D.O.B. Year");
				report.columnHeading("Affiliate");
				report.columnHeading("Banner");
				report.columnHeading("Campaign");
				report.columnHeading("Current Balance");
				report.columnHeading("Opening Balance");
				report.columnHeading("Closing Balance");
				report.columnHeading("Current Balance (Casino Bonus)");
				report.columnHeading("Opening Balance (Casino Bonus)");
				report.columnHeading("Closing Balance (Casino Bonus)");
				report.columnHeading("Current Balance (Pending Casino Bonus)");
				report.columnHeading("Opening Balance (Pending Casino Bonus)");
				report.columnHeading("Closing Balance (Pending Casino Bonus)");
				report.columnHeading("Current Balance (Pending Withdrawal)");
				report.columnHeading("Opening Balance (Pending Withdrawal)");
				report.columnHeading("Closing Balance (Pending Withdrawal)");
				report.columnHeading("Transfer To Player Balance Pending Withdrawal");
				report.columnHeading("Transfer From Player Balance Pending Withdrawal");
				report.columnHeading("Deposits");
				report.columnHeading("Deposits (#)");
				report.columnHeading("Deposit Fees");
				report.columnHeading("Payouts");
				report.columnHeading("Payouts (#)");
				report.columnHeading("Adjustments");
				report.columnHeading("Adjustmentss (#)");

				report.columnHeading("Casino Bets");
				report.columnHeading("Casino Bets (#)");
				report.columnHeading("Casino Wins");
				report.columnHeading("Casino Wins (#)");
				report.columnHeading("Casino Net");

				report.columnHeading("Virtual Bets");
				report.columnHeading("Virtual Bets (#)");
				report.columnHeading("Virtual Loss (#)");
				report.columnHeading("Virtual Wins");
				report.columnHeading("Virtual Wins (#)");
				report.columnHeading("Virtual Bets Voided");
				report.columnHeading("Virtual Bets Voided (#)");

				report.columnHeading("Casino Bonus Bets");
				report.columnHeading("Casino Bonus Bets (#)");
				report.columnHeading("Casino Bonus Wins");
				report.columnHeading("Casino Bonus Wins (#)");
				report.columnHeading("Casino Bonus Net");

				report.columnHeading("Pending Casino Bonus (freemoney)");
				report.columnHeading("Transfer To Pending Casino Bonus");
				report.columnHeading("Transfer From Pending Casino Bonus");
				report.columnHeading("Pending Casino Bonus Cancel");
				report.columnHeading("Pending Casino Bonus (#)");
				report.columnHeading("Casino Bonus Activated (freemoney)");
				report.columnHeading("Transfer To Casino Bonus");
				report.columnHeading("Transfer From Casino Bonus");
				report.columnHeading("Casino Bonus Cancel");
				report.columnHeading("Casino Bonus Expire");
				report.columnHeading("Casino Max Payout Excess");
				report.columnHeading("Email Opt Out");
				report.columnHeading("SMS Opt Out");
				report.columnHeading("Call Opt Out");
				report.columnHeading("Gamstop Status");
				report.columnHeading("Referral Code");
			}, () -> {

				Pageable pageRequest = PageRequest.of(0, 5, Direction.ASC, new String[] {"username"});
				Page<ReportRunResults> pageResult = null;
				
				do {
					pageResult = repoRunResults.findByReportRunId(run.getId(), pageRequest);
					for (ReportRunResults result: pageResult.getContent()) {
						
						report.row(() -> {
							report.cell(result.getUsername().getValue());
							report.cell(result.getEmail().getValue());
							report.cell(result.getFirstName().getValue());
							report.cell(result.getLastName().getValue());
							report.cell(result.getEnabled() ? "Yes": "No");
							report.cell(result.getStatus().getValue());
							report.cell(result.getEmailValidated() ? "Yes": "No");
							report.cell(result.getResidentialAddressLine1().getValue());
							report.cell(result.getResidentialAddressLine2().getValue());
							report.cell(result.getResidentialAddressLine3().getValue());
							report.cell(result.getResidentialAddressCity().getValue());
							report.cell(result.getResidentialAddressAdminLevel1().getValue());
							report.cell(result.getResidentialAddressCountry().getValue());
							report.cell(result.getResidentialAddressPostalCode().getValue());
							report.cell(result.getPostalAddressLine1().getValue());
							report.cell(result.getPostalAddressLine2().getValue());
							report.cell(result.getPostalAddressLine3().getValue());
							report.cell(result.getPostalAddressCity().getValue());
							report.cell(result.getPostalAddressAdminLevel1().getValue());
							report.cell(result.getPostalAddressCountry().getValue());
							report.cell(result.getPostalAddressPostalCode().getValue());
							report.cell(result.getTelephoneNumber().getValue());
							report.cell(result.getCellphoneNumber().getValue());
							report.cellDateTime(result.getCreatedDate());
							report.cellDateTime(result.getUpdatedDate());
							report.cell(result.getSignupBonusCode().getValue());
							report.cellDate(result.getDateOfBirth());
							report.cellNumeric(result.getDateOfBirthDay());
							report.cellNumeric(result.getDateOfBirthMonth());
							report.cellNumeric(result.getDateOfBirthYear());
							report.cell(result.getAffiliateGuid().getValue());
							report.cell(result.getBannerGuid().getValue());
							report.cell(result.getCampaignGuid().getValue());
							report.cellCents(result.getCurrentBalanceCents());
							report.cellCents(result.getPeriodOpeningBalanceCents());
							report.cellCents(result.getPeriodClosingBalanceCents());
							report.cellCents(result.getCurrentBalanceCasinoBonusCents());
							report.cellCents(result.getPeriodOpeningBalanceCasinoBonusCents());
							report.cellCents(result.getPeriodClosingBalanceCasinoBonusCents());
							report.cellCents(result.getCurrentBalanceCasinoBonusPendingCents());
							report.cellCents(result.getPeriodOpeningBalanceCasinoBonusPendingCents());
							report.cellCents(result.getPeriodClosingBalanceCasinoBonusPendingCents());
							report.cellCents(result.getCurrentBalancePendingWithdrawalCents());
							report.cellCents(result.getPeriodOpeningBalancePendingWithdrawalCents());
							report.cellCents(result.getPeriodClosingBalancePendingWithdrawalCents());
							report.cellCents(result.getTransferToPlayerBalancePendingWithdrawalAmountCents());
							report.cellCents(result.getTransferFromPlayerBalancePendingWithdrawalAmountCents());
							report.cellCents(result.getDepositAmountCents());
							report.cellNumeric(result.getDepositCount());
							report.cellCents(result.getDepositFeeCents());
							report.cellCents(result.getPayoutAmountCents());
							report.cellNumeric(result.getPayoutCount());
							report.cellCents(result.getBalanceAdjustAmountCents());
							report.cellNumeric(result.getBalanceAdjustCount());

							report.cellCents(result.getCasinoBetAmountCents());
							report.cellNumeric(result.getCasinoBetCount());
							report.cellCents(result.getCasinoWinAmountCents());
							report.cellNumeric(result.getCasinoWinCount());
							report.cellCents(result.getCasinoNetAmountCents());

							report.cellCents(result.getVirtualBetAmountCents());
							report.cellNumeric(result.getVirtualBetCount());
							report.cellNumeric(result.getVirtualLossCount());
							report.cellCents(result.getVirtualWinAmountCents());
							report.cellNumeric(result.getVirtualWinCount());
							report.cellCents(result.getVirtualBetVoidAmountCents());
							report.cellNumeric(result.getVirtualBetVoidCount());

							report.cellCents(result.getCasinoBonusBetAmountCents());
							report.cellNumeric(result.getCasinoBonusBetCount());
							report.cellCents(result.getCasinoBonusWinAmountCents());
							report.cellNumeric(result.getCasinoBonusWinCount());
							report.cellCents(result.getCasinoBonusNetAmountCents());
							
							report.cellCents(result.getCasinoBonusPendingAmountCents());
							report.cellCents(result.getCasinoBonusTransferToBonusPendingAmountCents());
							report.cellCents(result.getCasinoBonusTransferFromBonusPendingAmountCents());
							report.cellCents(result.getCasinoBonusPendingCancelAmountCents());
							report.cellNumeric(result.getCasinoBonusPendingCount());
							
							report.cellCents(result.getCasinoBonusActivateAmountCents());
							report.cellCents(result.getCasinoBonusTransferToBonusAmountCents());
							report.cellCents(result.getCasinoBonusTransferFromBonusAmountCents());
							
							report.cellCents(result.getCasinoBonusCancelAmountCents());
							report.cellCents(result.getCasinoBonusExpireAmountCents());
							report.cellCents(result.getCasinoBonusMaxPayoutExcessAmountCents());
							
							report.cell(result.getEmailOptOut().toString());
							report.cell(result.getSmsOptOut().toString());
							report.cell(result.getCallOptOut().toString());
							report.cell(result.getGamstopStatus());
							if (result.getReferralCode() != null)
								report.cell(result.getReferralCode().getValue());
						});
						
					}
					pageRequest = pageRequest.next();
				} while (!pageResult.isLast());

			});
		});
	}
}
