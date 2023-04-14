package lithium.service.report.players.services;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVParser;
import com.opencsv.ICSVWriter;

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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;

@Service
public class ReportRunResultCsvService {
	@Autowired
	ReportRunResultsRepository repoRunResults;
//	private Date periodStartDate;

	public void csvReg(ReportRun run, OutputStream regOutputStream) throws IOException, Exception {
		csv(run, regOutputStream, null);
	}

	public void csv(ReportRun run, OutputStream regOutputStream, OutputStream salesOutputStream) throws IOException, Exception {
		// Prep
		StringWriter regWriter = new StringWriter();

		// Head
		ICSVWriter regCsv = initRegistrationWriter(regWriter);

		Pageable pageRequest = PageRequest.of(0, 5, Direction.ASC, new String[]{"username"});
		Page<ReportRunResults> pageResult = null;
		do {
			pageResult = repoRunResults.findByReportRunId(run.getId(), pageRequest);
			for (ReportRunResults result : pageResult.getContent()) {
				writeRegistrationLine(result, regCsv);
			}
			pageRequest = pageRequest.next();
		} while (!pageResult.isLast());

		// Final write to stream
		if (regOutputStream != null) {
			regOutputStream.write(regWriter.toString().getBytes());
		}
	}
	/**
	 * Creates a csv writer instance with the registration file headers added
	 *
	 * @param writer
	 * @return
	 */
	private ICSVWriter initRegistrationWriter(final StringWriter writer) {
		ICSVWriter csvWriter = initCsvWriter(writer);

		ArrayList<String> headers = new ArrayList<>();
			headers.add("Username");
			headers.add("Email");
			headers.add("First Name");
			headers.add("Last Name");
			headers.add("Enabled");
			headers.add("Status");
			headers.add("Email Validated");
			headers.add("R. Address Line 1");
			headers.add("R. Address Line 2");
			headers.add("R. Address Line 3");
			headers.add("R. Address City");
			headers.add("R. Address Region");
			headers.add("R. Address Country");
			headers.add("R. Address Postal Code");
			headers.add("P. Address Line 1");
			headers.add("P. Address Line 2");
			headers.add("P. Address Line 3");
			headers.add("P. Address City");
			headers.add("P. Address Region");
			headers.add("P. Address Country");
			headers.add("P. Address Postal Code");
			headers.add("Tel. Number");
			headers.add("Cel. Number");
			headers.add("Created");
			headers.add("Updated");
			headers.add("Signup Bonus Code");
			headers.add("D.O.B.");
			headers.add("D.O.B. Day");
			headers.add("D.O.B. Month");
			headers.add("D.O.B. Year");
			headers.add("Affiliate");
			headers.add("Banner");
			headers.add("Campaign");
			headers.add("Current Balance");
			headers.add("Opening Balance");
			headers.add("Closing Balance");
			headers.add("Current Balance (Casino Bonus)");
			headers.add("Opening Balance (Casino Bonus)");
			headers.add("Closing Balance (Casino Bonus)");
			headers.add("Current Balance (Pending Casino Bonus)");
			headers.add("Opening Balance (Pending Casino Bonus)");
			headers.add("Closing Balance (Pending Casino Bonus)");
			headers.add("Current Balance (Pending Withdrawal)");
			headers.add("Opening Balance (Pending Withdrawal)");
			headers.add("Closing Balance (Pending Withdrawal)");
			headers.add("Transfer To Player Balance Pending Withdrawal");
			headers.add("Transfer From Player Balance Pending Withdrawal");
			headers.add("Deposits");
			headers.add("Deposits (#)");
			headers.add("Deposit Fees");
			headers.add("Payouts");
			headers.add("Payouts (#)");
			headers.add("Adjustments");
			headers.add("Adjustmentss (#)");

			headers.add("Casino Bets");
			headers.add("Casino Bets (#)");
			headers.add("Casino Wins");
			headers.add("Casino Wins (#)");
			headers.add("Casino Net");

			headers.add("Virtual Bets");
			headers.add("Virtual Bets (#)");
			headers.add("Virtual Loss (#)");
			headers.add("Virtual Wins");
			headers.add("Virtual Wins (#)");
			headers.add("Virtual Bets Voided");
			headers.add("Virtual Bets Voided (#)");

			headers.add("Casino Bonus Bets");
			headers.add("Casino Bonus Bets (#)");
			headers.add("Casino Bonus Wins");
			headers.add("Casino Bonus Wins (#)");
			headers.add("Casino Bonus Net");

			headers.add("Pending Casino Bonus (freemoney)");
			headers.add("Transfer To Pending Casino Bonus");
			headers.add("Transfer From Pending Casino Bonus");
			headers.add("Pending Casino Bonus Cancel");
			headers.add("Pending Casino Bonus (#)");
			headers.add("Casino Bonus Activated (freemoney)");
			headers.add("Transfer To Casino Bonus");
			headers.add("Transfer From Casino Bonus");
			headers.add("Casino Bonus Cancel");
			headers.add("Casino Bonus Expire");
			headers.add("Casino Max Payout Excess");
			headers.add("Email Opt Out");
			headers.add("SMS Opt Out");
			headers.add("Call Opt Out");
			headers.add("Referral Code");
			headers.add("Gamstop Status");
			csvWriter.writeNext(headers.toArray(new String[headers.size()]));

		return csvWriter;
	}
	/**
	 * Generic csv writer init method for use in all csv files
	 *
	 * @param writer
	 * @return ICSVWriter
	 */
	private ICSVWriter initCsvWriter(final StringWriter writer) {
		ICSVWriter csvWriter = new CSVWriterBuilder(writer)
				.withSeparator(ICSVParser.DEFAULT_SEPARATOR)
				.withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
				.withEscapeChar(ICSVParser.DEFAULT_ESCAPE_CHARACTER)
				.withLineEnd(ICSVWriter.RFC4180_LINE_END)
				.build();
		return csvWriter;
	}

	/**
	 * Write a registration body line to the csv writer
	 *
	 * @param result
	 * @param csvWriter
	 */
	private void writeRegistrationLine(final ReportRunResults result, ICSVWriter csvWriter) {
		csvWriter.writeNext(new String[]{
			result.getUsername().getValue(),
			result.getEmail().getValue(),
			result.getFirstName().getValue(),
			result.getLastName().getValue(),
			result.getEnabled() ? "Yes": "No",
			result.getStatus().getValue(),
			result.getEmailValidated() ? "Yes": "No",
			result.getResidentialAddressLine1().getValue(),
			result.getResidentialAddressLine2().getValue(),
			result.getResidentialAddressLine3().getValue(),
			result.getResidentialAddressCity().getValue(),
			result.getResidentialAddressAdminLevel1().getValue(),
			result.getResidentialAddressCountry().getValue(),
			result.getResidentialAddressPostalCode().getValue(),
			result.getPostalAddressLine1().getValue(),
			result.getPostalAddressLine2().getValue(),
			result.getPostalAddressLine3().getValue(),
			result.getPostalAddressCity().getValue(),
			result.getPostalAddressAdminLevel1().getValue(),
			result.getPostalAddressCountry().getValue(),
			result.getPostalAddressPostalCode().getValue(),
			result.getTelephoneNumber().getValue(),
			result.getCellphoneNumber().getValue(),
			result.getCreatedDate() != null ? result.getCreatedDate().toString() : "",
			result.getUpdatedDate() != null ? result.getUpdatedDate().toString() : "",
			result.getSignupBonusCode().getValue(),
			result.getDateOfBirth() != null ? result.getDateOfBirth().toString() : "",
			result.getDateOfBirthDay() != null ? result.getDateOfBirthDay().toString() : "",
			result.getDateOfBirthMonth() != null ? result.getDateOfBirthMonth().toString() : "",
			result.getDateOfBirthYear() != null ? result.getDateOfBirthMonth().toString() : "",
			result.getAffiliateGuid().getValue(),
			result.getBannerGuid().getValue(),
			result.getCampaignGuid().getValue(),
			result.getCurrentBalanceCents() != null ? result.getCurrentBalanceCents().toString() : "",
			result.getPeriodOpeningBalanceCents() != null ? result.getPeriodOpeningBalanceCents().toString() : "",
			result.getPeriodClosingBalanceCents() != null ? result.getPeriodClosingBalanceCents().toString() : "",
			result.getCurrentBalanceCasinoBonusCents() != null ? result.getCurrentBalanceCasinoBonusCents().toString() : "",
			result.getPeriodOpeningBalanceCasinoBonusCents() != null ? result.getPeriodOpeningBalanceCasinoBonusCents().toString() : "",
			result.getPeriodClosingBalanceCasinoBonusCents() != null ? result.getPeriodClosingBalanceCasinoBonusCents().toString() : "",
			result.getCurrentBalanceCasinoBonusPendingCents() != null ? result.getCurrentBalanceCasinoBonusPendingCents().toString() : "",
			result.getPeriodOpeningBalanceCasinoBonusPendingCents() != null ? result.getPeriodOpeningBalanceCasinoBonusPendingCents().toString() : "",
			result.getPeriodClosingBalanceCasinoBonusPendingCents() != null ? result.getPeriodClosingBalanceCasinoBonusPendingCents().toString() : "",
			result.getCurrentBalancePendingWithdrawalCents() != null ? result.getCurrentBalancePendingWithdrawalCents().toString() : "",
			result.getPeriodOpeningBalancePendingWithdrawalCents() != null ? result.getPeriodOpeningBalancePendingWithdrawalCents().toString() : "",
			result.getPeriodClosingBalancePendingWithdrawalCents() != null ? result.getPeriodClosingBalancePendingWithdrawalCents().toString() : "",
			result.getTransferToPlayerBalancePendingWithdrawalAmountCents() != null ? result.getTransferToPlayerBalancePendingWithdrawalAmountCents().toString() : "",
			result.getTransferFromPlayerBalancePendingWithdrawalAmountCents() != null ? result.getTransferFromPlayerBalancePendingWithdrawalAmountCents().toString() : "",
			result.getDepositAmountCents() != null ? result.getDepositAmountCents().toString() : "",
			result.getDepositCount() != null ? result.getDepositCount().toString() : "",
			result.getDepositFeeCents() != null ? result.getDepositFeeCents().toString() : "",
			result.getPayoutAmountCents() != null ? result.getPayoutAmountCents().toString() : "",
			result.getPayoutCount() != null ? result.getPayoutCount().toString() : "",
			result.getBalanceAdjustAmountCents() != null ? result.getBalanceAdjustAmountCents().toString() : "",
			result.getBalanceAdjustCount() != null ? result.getBalanceAdjustCount().toString() : "",

			result.getCasinoBetAmountCents() != null ? result.getCasinoBetAmountCents().toString() : "",
			result.getCasinoBetCount() != null ? result.getCasinoBetCount().toString() : "",
			result.getCasinoWinAmountCents() != null ? result.getCasinoWinAmountCents().toString() : "",
			result.getCasinoWinCount() != null ? result.getCasinoWinCount().toString() : "",
			result.getCasinoNetAmountCents() != null ? result.getCasinoNetAmountCents().toString() : "",

			result.getVirtualBetAmountCents() != null ? result.getVirtualBetAmountCents().toString() : "",
			result.getVirtualBetCount() != null ? result.getVirtualBetCount().toString() : "",
			result.getVirtualLossCount() != null ? result.getVirtualLossCount().toString() : "",
			result.getVirtualWinAmountCents() != null ? result.getVirtualWinAmountCents().toString() : "",
			result.getVirtualWinCount() != null ? result.getVirtualWinCount().toString() : "",
			result.getVirtualBetVoidAmountCents() != null ? result.getVirtualBetVoidAmountCents().toString() : "",
			result.getVirtualBetVoidCount() != null ? result.getVirtualBetVoidCount().toString() : "",

			result.getCasinoBonusBetAmountCents() != null ? result.getCasinoBonusBetAmountCents().toString() : "",
			result.getCasinoBonusBetCount() != null ? result.getCasinoBonusBetCount().toString() : "",
			result.getCasinoBonusWinAmountCents() != null ? result.getCasinoBonusWinAmountCents().toString() : "",
			result.getCasinoBonusWinCount() != null ? result.getCasinoBonusWinCount().toString() : "",
			result.getCasinoBonusNetAmountCents() != null ? result.getCasinoBonusNetAmountCents().toString() : "",

			result.getCasinoBonusPendingAmountCents() != null ? result.getCasinoBonusPendingAmountCents().toString() : "",
			result.getCasinoBonusTransferToBonusPendingAmountCents() != null ? result.getCasinoBonusTransferToBonusPendingAmountCents().toString() : "",
			result.getCasinoBonusTransferFromBonusPendingAmountCents() != null ? result.getCasinoBonusTransferFromBonusPendingAmountCents().toString() : "",
			result.getCasinoBonusPendingCancelAmountCents() != null ? result.getCasinoBonusPendingCancelAmountCents().toString() : "",
			result.getCasinoBonusPendingCount() != null ? result.getCasinoBonusPendingCount().toString() : "",

			result.getCasinoBonusActivateAmountCents() != null ? result.getCasinoBonusActivateAmountCents().toString() : "",
			result.getCasinoBonusTransferToBonusAmountCents() != null ? result.getCasinoBonusTransferToBonusAmountCents().toString() : "",
			result.getCasinoBonusTransferFromBonusAmountCents() != null ? result.getCasinoBonusTransferFromBonusAmountCents().toString() : "",

			result.getCasinoBonusCancelAmountCents() != null ? result.getCasinoBonusCancelAmountCents().toString() : "",
			result.getCasinoBonusExpireAmountCents() != null ? result.getCasinoBonusExpireAmountCents().toString() : "",
			result.getCasinoBonusMaxPayoutExcessAmountCents() != null ? result.getCasinoBonusMaxPayoutExcessAmountCents().toString() : "",

			result.getEmailOptOut() != null ? result.getEmailOptOut().toString() : "",
			result.getSmsOptOut() != null ? result.getSmsOptOut().toString() : "",
			result.getCallOptOut() != null ? result.getCallOptOut().toString() : "",
			result.getReferralCode() != null ? result.getReferralCode().getValue() : "",
			result.getGamstopStatus() != null ? result.getReferralCode().getValue() : ""
		});
	}
}