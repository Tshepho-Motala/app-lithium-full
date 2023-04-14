package lithium.service.report.games.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import lithium.report.XlsReport;
import lithium.service.report.games.data.entities.ReportRun;
import lithium.service.report.games.data.entities.ReportRunResults;
import lithium.service.report.games.data.repositories.ReportRunRepository;
import lithium.service.report.games.data.repositories.ReportRunResultsRepository;

@Service
public class ReportRunResultExcelService {
	@Autowired ReportRunRepository reportRunRepository;
	@Autowired ReportRunResultsRepository repoRunResults;
	
	public void xls(ReportRun run, OutputStream outputStream) throws IOException {
		XlsReport report = new XlsReport(run.getReportRevision().getName());
		
		report.run(outputStream, () -> {
			
			List<ReportRun> runs = new ArrayList<>();
			if (run.getRunParent() != null) {
				runs = reportRunRepository.findByIdOrRunParentOrderByRunGranularityOffsetAsc(run.getRunParent().getId(), run.getRunParent());
			} else {
				runs = reportRunRepository.findByIdOrRunParentOrderByRunGranularityOffsetAsc(run.getId(), run);
			}
			
			runs.stream().forEach(rr -> {
				
				report.sheet(
						(rr.getRunGranularity() + "-" + rr.getRunGranularityOffset() + "-" + rr.getPeriodString())
						.replaceAll(":", "."), () -> {
					report.columnHeading("Name");
					report.columnHeading("Internal ID");
					report.columnHeading("Provider ID");
					report.columnHeading("Provider Name");
					report.columnHeading("Enabled");
					report.columnHeading("Casino Bets");
					report.columnHeading("Casino Bets (#)");
					report.columnHeading("Casino Wins");
					report.columnHeading("Casino Wins (#)");
					report.columnHeading("Casino Net");
					report.columnHeading("Casino Bonus Bets");
					report.columnHeading("Casino Bonus Bets (#)");
					report.columnHeading("Casino Bonus Wins");
					report.columnHeading("Casino Bonus Wins (#)");
					report.columnHeading("Casino Bonus Net");
				}, () -> {
					Pageable pageRequest = PageRequest.of(0, 5, Direction.ASC, new String[] {"name"});
					Page<ReportRunResults> pageResult = null;
					do {
						pageResult = repoRunResults.findByReportRunId(rr.getId(), pageRequest);
						for (ReportRunResults result: pageResult.getContent()) {
							
							report.row(() -> {
								report.cell(result.getName().getValue());
								report.cellNumeric(result.getInternalId());
								report.cell(result.getProviderId().getValue());
								report.cell(result.getProviderName().getValue());
								report.cell((result.getEnabled() != null)? result.getEnabled().getValue(): "");
								report.cellCents(result.getCasinoBetAmountCents());
								report.cellNumeric(result.getCasinoBetCount());
								report.cellCents(result.getCasinoWinAmountCents());
								report.cellNumeric(result.getCasinoWinCount());
								report.cellCents(result.getCasinoNetAmountCents());
								report.cellCents(result.getCasinoBonusBetAmountCents());
								report.cellNumeric(result.getCasinoBonusBetCount());
								report.cellCents(result.getCasinoBonusWinAmountCents());
								report.cellNumeric(result.getCasinoBonusWinCount());
								report.cellCents(result.getCasinoBonusNetAmountCents());
							});
							
						}
						pageRequest = pageRequest.next();
					} while (!pageResult.isLast());
				});
				
			});
			
		});
	}
}