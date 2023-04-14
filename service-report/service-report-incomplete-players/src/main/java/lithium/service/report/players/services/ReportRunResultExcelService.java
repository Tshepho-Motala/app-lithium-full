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
				report.columnHeading("Cel. Number");
				report.columnHeading("Created");
				report.columnHeading("Gender");
				report.columnHeading("Stage");
			}, () -> {

				Pageable pageRequest = PageRequest.of(0, 5, Direction.ASC, new String[] {"username"});
				Page<ReportRunResults> pageResult = null;
				
				do {
					pageResult = repoRunResults.findByReportRunId(run.getId(), pageRequest);
					for (ReportRunResults result: pageResult.getContent()) {
						
						report.row(() -> {
							report.cell((result.getUsername() != null)? result.getUsername().getValue(): null);
							report.cell((result.getEmail() != null)? result.getEmail().getValue(): null);
							report.cell((result.getFirstName() != null)? result.getFirstName().getValue(): null);
							report.cell((result.getLastName() != null)? result.getLastName().getValue(): null);
							report.cell((result.getCellphoneNumber() != null)? result.getCellphoneNumber().getValue(): null);
							report.cellDateTime((result.getCreatedDate() != null)? result.getCreatedDate(): null);
							report.cell((result.getGender() != null)? result.getGender().getValue(): null);
							report.cell((result.getStage() != null)? result.getStage().getValue(): null);
						});
						
					}
					pageRequest = pageRequest.next();
				} while (!pageResult.isLast());

			});
		});
	}
}