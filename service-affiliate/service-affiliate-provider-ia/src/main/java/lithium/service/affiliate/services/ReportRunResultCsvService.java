package lithium.service.affiliate.services;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVParser;
import com.opencsv.ICSVWriter;
import lithium.service.accounting.client.AccountingPeriodClient;
import lithium.service.accounting.objects.Period;
import lithium.service.affiliate.data.entities.ReportRevision;
import lithium.service.affiliate.data.entities.ReportRun;
import lithium.service.affiliate.data.entities.ReportRunResults;
import lithium.service.affiliate.data.repositories.ReportRunResultsRepository;
import lithium.service.client.LithiumServiceClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class ReportRunResultCsvService {
	@Autowired
	ReportRunResultsRepository repoRunResults;
	@Autowired
	LithiumServiceClientFactory services;

	public void csvReg(ReportRun run, OutputStream regOutputStream) throws IOException, Exception {
		csv(run, regOutputStream, null);
	}

	public void csvSales(ReportRun run, OutputStream salesOutputStream) throws IOException, Exception {
		csv(run, null, salesOutputStream);
	}

	public void csv(ReportRun run, OutputStream regOutputStream, OutputStream salesOutputStream) throws IOException, Exception {
		// Prep
		StringWriter regWriter = new StringWriter();
		StringWriter salesWriter = new StringWriter();

		// Head
		ICSVWriter regCsv = initRegistrationWriter(regWriter);
		ICSVWriter salesCsv = initSalesWriter(salesWriter);

		//Filtering registration dates
		AccountingPeriodClient accountingPeriodClient = services.target(AccountingPeriodClient.class);
		ReportRevision rev = run.getReportRevision();
		Date periodStartDate = run.getPeriodStartDate();
		if (periodStartDate == null) {
			periodStartDate = run.getStartedOn();
		}
		// Body
		Pageable pageRequest = PageRequest.of(0, 5, Direction.ASC, new String[]{"username"});
		Page<ReportRunResults> pageResult = null;
		do {
			pageResult = repoRunResults.findByReportRunId(run.getId(), pageRequest);
			for (ReportRunResults result : pageResult.getContent()) {

				writeRegistrationLine(result, regCsv, periodStartDate);
				writeSalesLine(result, salesCsv, periodStartDate);
			}
			pageRequest = pageRequest.next();
		} while (!pageResult.isLast());

		// Final write to stream
		if (regOutputStream != null) {
			regOutputStream.write(regWriter.toString().getBytes());
		}
		if (salesOutputStream != null) {
			salesOutputStream.write(salesWriter.toString().getBytes());
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
		headers.add("ACCOUNT_OPENING_DATE");
		headers.add("BTAG");
		headers.add("PLAYER_ID");
		headers.add("USERNAME");
		headers.add("PLAYER_COUNTRY");
		csvWriter.writeNext(headers.toArray(new String[headers.size()]));

		return csvWriter;
	}

	/**
	 * Creates a csv writer instance with the sales file headers added
	 *
	 * @param writer
	 * @return ICSVWriter
	 */
	private ICSVWriter initSalesWriter(final StringWriter writer) {
		ICSVWriter csvWriter = initCsvWriter(writer);

		ArrayList<String> headers = new ArrayList<>();
		headers.add("TRANSACTION_DATE");
		headers.add("PLAYER_ID");
		headers.add("BTAG");
		headers.add("DEPOSITS");
		headers.add("CHARGEBACKS");
		headers.add("CASINO_#_OF_BETS");
		headers.add("CASINO_REVENUE");
		headers.add("CASINO_STAKE");
		headers.add("CPA_ELIGIBLE");
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
	private void writeRegistrationLine(final ReportRunResults result, ICSVWriter csvWriter, Date periodStartDate) {
		if (result.getAffiliateGuid() == null) return;
		// FIXME: 2019/12/29 Need to get the country from some place
		String country = "Canada";

		if (periodStartDate != null) {
			if (result.getCreatedDate().before(periodStartDate)) {
				return;
			}
		}
		if (result.getResidentialAddressCountry() != null) {
			if (result.getResidentialAddressCountry().getValue() != null &&
				!result.getResidentialAddressCountry().getValue().contentEquals("null") &&
				!result.getResidentialAddressCountry().getValue().trim().isEmpty()) {
				country = result.getResidentialAddressCountry().getValue();
			}
		}
		csvWriter.writeNext(new String[]{
				new SimpleDateFormat("yyyy-MM-dd")
						.format(periodStartDate),
				buildBtag(
						result.getAffiliateGuid().getValue(),
						result.getBannerGuid().getValue(),
						result.getCampaignGuid().getValue()),
				result.getUserId().toString(),
				result.getUsername().getValue(),
				country
		});
	}

	/**
	 * Write a sales body line to the csv writer
	 *
	 * @param result
	 * @param csvWriter
	 */
	private void writeSalesLine(final ReportRunResults result, ICSVWriter csvWriter, Date periodStartDate) {
		if (result.getAffiliateGuid() == null) return;

		csvWriter.writeNext(new String[]{
				new SimpleDateFormat("yyyy-MM-dd")
						.format(periodStartDate),
				result.getUserId().toString(),
				buildBtag(
						result.getAffiliateGuid().getValue(),
						result.getBannerGuid().getValue(),
						result.getCampaignGuid().getValue()),
				result.getDepositAmountCents() != null ? new BigDecimal(result.getDepositAmountCents()).movePointLeft(2).toPlainString() : "0",
				"0", //Chargebacks
				""+ReportRunService.zeroOrValue(result.getCasinoBetCount()) + ReportRunService.zeroOrValue(result.getCasinoBonusBetCount()), //All bets, this is going to cause confusion in future for affiliates (Allan wanted it like this)
				BigDecimal.valueOf(result.getNgrAmount()).movePointLeft(2).toPlainString(),
				new BigDecimal(ReportRunService.zeroOrValue(result.getCasinoBetAmountCents()) + ReportRunService.zeroOrValue(result.getCasinoBonusBetAmountCents())).movePointLeft(2).toPlainString(),
				"0" //CPA eligible
		});
	}

	/**
	 * Builds an income access btag using the lithium afifliate parameters if they are available.
	 * Null values are replaced with empty strings.
	 * @param affiliate
	 * @param banner
	 * @param campaign
	 * @return
	 */
	private String buildBtag(String affiliate, String banner, String campaign) {
		return new StringBuilder()
				.append("a_")
				.append(affiliate != null ? affiliate : "")
				.append("b_")
				.append(banner != null ? banner : "")
				.append("c_")
				.append(campaign != null ? campaign : "")
				.toString();
	}

	/**
	 * Helper function to convert the cent amounts to decimal currency values.
	 * Example: 567 would become 5.67
	 * @param cents
	 * @return
	 */
	private String centsToDecimal(final long cents) {
		return BigDecimal.valueOf(cents).movePointLeft(2).toPlainString();
	}
}
