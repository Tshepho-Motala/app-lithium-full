package lithium.service.report.games.services;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingPeriodClient;
import lithium.service.accounting.client.AccountingSummaryDomainLabelValueClient;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.objects.Label;
import lithium.service.report.games.data.entities.Report;
import lithium.service.report.games.data.entities.ReportFilter;
import lithium.service.report.games.data.entities.ReportRevision;
import lithium.service.report.games.data.entities.ReportRun;
import lithium.service.report.games.data.entities.ReportRunResults;
import lithium.service.report.games.data.repositories.ReportFilterRepository;
import lithium.service.report.games.data.repositories.ReportRepository;
import lithium.service.report.games.data.repositories.ReportRunRepository;
import lithium.service.report.games.data.repositories.ReportRunResultsRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportRunService {
	@Autowired ReportRepository reportRepository;
	@Autowired ReportRunRepository reportRunRepository;
	@Autowired ReportRunResultsRepository reportRunResultsRepository;
	@Autowired ReportFilterRepository reportFilterRepository;
	@Autowired ReportFilterService reportFilterService;
	@Autowired ReportActionService reportActionService;
	@Autowired StringValueService svs;
	@Autowired CachingDomainClientService currencyService;
	
	@Autowired LithiumServiceClientFactory services;
	
	private static final String LABEL_GAME_GUID = "game_guid";
	
	private Response<List<SummaryLabelValue>> findLimited(String domain, int granularity, String accountCode, String transactionType, String labelName, String labelValue, String labelValue2, String currency, String dateStart, String dateEnd) throws Exception {
		AccountingSummaryDomainLabelValueClient accountingSummaryDomainLabelValueClient = services.target(AccountingSummaryDomainLabelValueClient.class);
		Response<List<SummaryLabelValue>> r = accountingSummaryDomainLabelValueClient.findLimited(domain, granularity, accountCode, transactionType, labelName, labelValue, currency, dateStart, dateEnd);
		if (r.getData() == null || r.getData().size() == 0) {
			r = accountingSummaryDomainLabelValueClient.findLimited(domain, granularity, accountCode, transactionType, labelName, labelValue2, currency, dateStart, dateEnd);
		}
		return r;
	}
	
	//TODO: add missing transactions like freeround wins on bonus
	private void run(ReportRun reportRun, int runGranularityOffset) throws Exception {
		Domain domain = currencyService.retrieveDomainFromDomainService(reportRun.getReport().getDomainName());
		
		GamesClient gamesClient = services.target(GamesClient.class);
		AccountingPeriodClient accountingPeriodClient = services.target(AccountingPeriodClient.class);
		
		ReportRevision rev = reportRun.getReportRevision();
		List<ReportFilter> reportFilters = reportFilterRepository.findByReportRevision(rev);
			
		Period period = accountingPeriodClient.findByOffset(reportRun.getReport().getDomainName(), 
				rev.getGranularity(), runGranularityOffset).getData();
		
		log.info("Period for report " + period + " " + reportRun);
		
		boolean hasMoreGames = true;
		Long gamesCount = 0L;
		Long gamesPos = 0L;
		
		Long totalRecords = 0L, filteredRecords = 0L, processedRecords = 0L;
		while (hasMoreGames) {
			reportRun.setRunGranularity(rev.getGranularity());
			reportRun.setRunGranularityOffset(runGranularityOffset);
			reportRun.setPeriodString(period.getDateStart()+"-"+period.getDateEnd());
			reportRun = reportRunRepository.save(reportRun);
			
			DataTableResponse<Game> games = gamesClient.listDomainGamesReport(reportRun.getReport().getDomainName(), "1", gamesPos, 10L);
			totalRecords = games.getRecordsTotal();
			
			reportRun.setTotalRecords(totalRecords);
			reportRun = reportRunRepository.save(reportRun);
			
			gamesCount = gamesCount + games.getData().size();
			
			gamesPos = gamesPos + games.getData().size();
			if (gamesCount >= games.getRecordsTotal()) hasMoreGames = false;
			
			gamesLoop:
			for (Game game: games.getData()) {
				log.debug("Got game " + game);
				
				// in accounting, the label for game_guid is sometimes like {{provider_guid}}/{{provider_game_id}}
				String gameGuid = game.getProviderGuid() + "/" + game.getProviderGameId();
				// but in certain instances, for example supera games, it is like {{domainName}}/{{provider_guid}}_{{provider_game_id}} 
				String gameGuid2 = domain.getName() + "/" + game.getProviderGuid() + "_" + game.getProviderGameId();
				
				ReportRunResults row = ReportRunResults.builder().reportRun(reportRun).build();
				
				HashMap<String, Label> gameLabels = game.getLabels();
				
				String gameName = game.getName();
				if (gameLabels.get("os") != null) {
					gameName = gameName + " - " + gameLabels.get("os").getValue();
				}
				
				row.setName(svs.link(gameName));
				row.setInternalId(game.getId());
				row.setProviderId(svs.link(game.getProviderGameId()));
				row.setProviderName(svs.link(game.getProviderGuid()));
				row.setEnabled(svs.link(String.valueOf(game.isEnabled())));
				
				long casinoBetAmount = 0L;
				long casinoBetCount = 0L;
				long casinoWinAmount = 0L;
				long casinoWinCount = 0L;
				
				long casinoBonusBetAmount = 0L;
				long casinoBonusBetCount = 0L;
				long casinoBonusWinAmount = 0L;
				long casinoBonusWinCount = 0L;
				
				{
					{
						Response<List<SummaryLabelValue>> r = findLimited(reportRun.getReport().getDomainName(), period.getGranularity(), "PLAYER_BALANCE", "CASINO_BET", LABEL_GAME_GUID, gameGuid, gameGuid2, domain.getCurrency(), period.getDateStart().toInstant().toString(), period.getDateEnd().toInstant().toString());
						List<SummaryLabelValue> d = r.getData(); 
						if (d == null || d.size() < 1) {
							casinoBetAmount += 0L;
							casinoBetCount += 0L;
						} else {
							for (SummaryLabelValue slv: d) {
								casinoBetAmount += slv.getDebitCents() - slv.getCreditCents();
								casinoBetCount += slv.getTranCount();
							}
						}
					}
					{
						Response<List<SummaryLabelValue>> r = findLimited(reportRun.getReport().getDomainName(), period.getGranularity(), "PLAYER_BALANCE", "CASINO_BET_ROLLBACK", LABEL_GAME_GUID, gameGuid, gameGuid2, domain.getCurrency(), period.getDateStart().toInstant().toString(), period.getDateEnd().toInstant().toString());
						List<SummaryLabelValue> d = r.getData(); 
						if (d == null || d.size() < 1) {
							casinoBetAmount -= 0L;
							casinoBetCount -= 0L;
						} else {
							for (SummaryLabelValue slv: d) {
								casinoBetAmount -= slv.getCreditCents() - slv.getDebitCents();
								casinoBetCount -= slv.getTranCount();
							};
						}
					}
					{
						Response<List<SummaryLabelValue>> r = findLimited(reportRun.getReport().getDomainName(), period.getGranularity(), "PLAYER_BALANCE", "CASINO_NEGATIVE_BET", LABEL_GAME_GUID, gameGuid, gameGuid2, domain.getCurrency(), period.getDateStart().toInstant().toString(), period.getDateEnd().toInstant().toString());
						List<SummaryLabelValue> d = r.getData(); 
						if (d == null || d.size() < 1) {
							casinoBetAmount -= 0L;
							casinoBetCount -= 0L;
						} else {
							for (SummaryLabelValue slv: d) {
								casinoBetAmount -= slv.getCreditCents() - slv.getDebitCents();
								casinoBetCount -= slv.getTranCount();
							};
						}
					}
					{
						Response<List<SummaryLabelValue>> r = findLimited(reportRun.getReport().getDomainName(), period.getGranularity(), "PLAYER_BALANCE", "CASINO_NEGATIVE_BET_ROLLBACK", LABEL_GAME_GUID, gameGuid, gameGuid2, domain.getCurrency(), period.getDateStart().toInstant().toString(), period.getDateEnd().toInstant().toString());
						List<SummaryLabelValue> d = r.getData();
						if (d == null || d.size() < 1) {
							casinoBetAmount += 0L;
							casinoBetCount += 0L;
						} else {
							for (SummaryLabelValue slv: d) {
								casinoBetAmount += slv.getDebitCents() - slv.getCreditCents();
								casinoBetCount += slv.getTranCount();
							};
						}
					}
					{
						Response<List<SummaryLabelValue>> r = findLimited(reportRun.getReport().getDomainName(), period.getGranularity(), "PLAYER_BALANCE", "CASINO_WIN", LABEL_GAME_GUID, gameGuid, gameGuid2, domain.getCurrency(), period.getDateStart().toInstant().toString(), period.getDateEnd().toInstant().toString());
						List<SummaryLabelValue> d = r.getData();
						if (d == null || d.size() < 1) {
							casinoWinAmount += 0L;
							casinoWinCount += 0L;
						} else {
							for (SummaryLabelValue slv: d) {
								casinoWinAmount += slv.getCreditCents() - slv.getDebitCents();
								casinoWinCount += slv.getTranCount();
							};
						}
					}
					{
						Response<List<SummaryLabelValue>> r = findLimited(reportRun.getReport().getDomainName(), period.getGranularity(), "PLAYER_BALANCE", "CASINO_WIN_ROLLBACK", LABEL_GAME_GUID, gameGuid, gameGuid2, domain.getCurrency(), period.getDateStart().toInstant().toString(), period.getDateEnd().toInstant().toString());
						List<SummaryLabelValue> d = r.getData();
						if (d == null || d.size() < 1) {
							casinoWinAmount -= 0L;
							casinoWinCount -= 0L;
						} else {
							for (SummaryLabelValue slv: d) {
								casinoWinAmount -= slv.getDebitCents() - slv.getCreditCents();
								casinoWinCount -= slv.getTranCount();
							};
						}
					}
				}
				
				{
					{
						Response<List<SummaryLabelValue>> r = findLimited(reportRun.getReport().getDomainName(), period.getGranularity(), "PLAYER_BALANCE_CASINO_BONUS", "CASINO_BET", LABEL_GAME_GUID, gameGuid, gameGuid2, domain.getCurrency(), period.getDateStart().toInstant().toString(), period.getDateEnd().toInstant().toString());
						List<SummaryLabelValue> d = r.getData(); 
						if (d == null || d.size() < 1) {
							casinoBonusBetAmount += 0L;
							casinoBonusBetCount += 0L;
						} else {
							for (SummaryLabelValue slv: d) {
								casinoBonusBetAmount += slv.getDebitCents() - slv.getCreditCents();
								casinoBonusBetCount += slv.getTranCount();
							};
						}
					}
					{
						Response<List<SummaryLabelValue>> r = findLimited(reportRun.getReport().getDomainName(), period.getGranularity(), "PLAYER_BALANCE_CASINO_BONUS", "CASINO_BET_ROLLBACK", LABEL_GAME_GUID, gameGuid, gameGuid2, domain.getCurrency(), period.getDateStart().toInstant().toString(), period.getDateEnd().toInstant().toString());
						List<SummaryLabelValue> d = r.getData();
						if (d == null || d.size() < 1) {
							casinoBonusBetAmount -= 0L;
							casinoBonusBetCount -= 0L;
						} else {
							for (SummaryLabelValue slv: d) {
								casinoBonusBetAmount -= slv.getCreditCents() - slv.getDebitCents();
								casinoBonusBetCount -= slv.getTranCount();
							};
						}
					}
					{
						Response<List<SummaryLabelValue>> r = findLimited(reportRun.getReport().getDomainName(), period.getGranularity(), "PLAYER_BALANCE_CASINO_BONUS", "CASINO_NEGATIVE_BET", LABEL_GAME_GUID, gameGuid, gameGuid2, domain.getCurrency(), period.getDateStart().toInstant().toString(), period.getDateEnd().toInstant().toString());
						List<SummaryLabelValue> d = r.getData();
						if (d == null || d.size() < 1) {
							casinoBonusBetAmount -= 0L;
							casinoBonusBetCount -= 0L;
						} else {
							for (SummaryLabelValue slv: d) {
								casinoBonusBetAmount -= slv.getCreditCents() - slv.getDebitCents();
								casinoBonusBetCount -= slv.getTranCount();
							};
						}
					}
					{
						Response<List<SummaryLabelValue>> r = findLimited(reportRun.getReport().getDomainName(), period.getGranularity(), "PLAYER_BALANCE_CASINO_BONUS", "CASINO_NEGATIVE_BET_ROLLBACK", LABEL_GAME_GUID, gameGuid, gameGuid2, domain.getCurrency(), period.getDateStart().toInstant().toString(), period.getDateEnd().toInstant().toString());
						List<SummaryLabelValue> d = r.getData();
						if (d == null || d.size() < 1) {
							casinoBonusBetAmount += 0L;
							casinoBonusBetCount += 0L;
						} else {
							for (SummaryLabelValue slv: d) {
								casinoBonusBetAmount += slv.getDebitCents() - slv.getCreditCents();
								casinoBonusBetCount += slv.getTranCount();
							};
						}
					}
					{
						Response<List<SummaryLabelValue>> r = findLimited(reportRun.getReport().getDomainName(), period.getGranularity(), "PLAYER_BALANCE_CASINO_BONUS", "CASINO_WIN", LABEL_GAME_GUID, gameGuid, gameGuid2, domain.getCurrency(), period.getDateStart().toInstant().toString(), period.getDateEnd().toInstant().toString());
						List<SummaryLabelValue> d = r.getData(); 
						if (d == null || d.size() < 1) {
							casinoBonusWinAmount += 0L;
							casinoBonusWinCount += 0L;
						} else {
							for (SummaryLabelValue slv: d) {
								casinoBonusWinAmount += slv.getCreditCents() - slv.getDebitCents();
								casinoBonusWinCount += slv.getTranCount();
							};
						}
					}
					{
						Response<List<SummaryLabelValue>> r = findLimited(reportRun.getReport().getDomainName(), period.getGranularity(), "PLAYER_BALANCE_CASINO_BONUS", "CASINO_WIN_ROLLBACK", LABEL_GAME_GUID, gameGuid, gameGuid2, domain.getCurrency(), period.getDateStart().toInstant().toString(), period.getDateEnd().toInstant().toString());
						List<SummaryLabelValue> d = r.getData();
						if (d == null || d.size() < 1) {
							casinoBonusWinAmount -= 0L;
							casinoBonusWinCount -= 0L;
						} else {
							for (SummaryLabelValue slv: d) {
								casinoBonusWinAmount -= slv.getDebitCents() - slv.getCreditCents();
								casinoBonusWinCount -= slv.getTranCount();
							};
						}
					}
				}
				
				row.setCasinoBetAmountCents(casinoBetAmount);
				row.setCasinoBetCount(casinoBetCount);
				row.setCasinoWinAmountCents(casinoWinAmount);
				row.setCasinoWinCount(casinoWinCount);
				row.setCasinoNetAmountCents(casinoBetAmount - casinoWinAmount);
				
				row.setCasinoBonusBetAmountCents(casinoBonusBetAmount);
				row.setCasinoBonusBetCount(casinoBonusBetCount);
				row.setCasinoBonusWinAmountCents(casinoBonusWinAmount);
				row.setCasinoBonusWinCount(casinoBonusWinCount);
				row.setCasinoBonusNetAmountCents(casinoBonusBetAmount - casinoBonusWinAmount);
				
				processedRecords++;
				reportRun.setProcessedRecords(processedRecords);
				reportRun = reportRunRepository.save(reportRun);
				
				Map<String, Boolean> filtration = new LinkedHashMap<String, Boolean>();
				for (ReportFilter reportFilter: reportFilters) {
					if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_GAME_CASINO_BET_AMOUNT_CENTS)) {
						if (row.getCasinoBetAmountCents() == null) { row.setCasinoBetAmountCents(0L); }
						filtration.put(reportFilter.getField(), reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getCasinoBetAmountCents()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_GAME_CASINO_BET_COUNT)) {
						if (row.getCasinoBetCount() == null) { row.setCasinoBetCount(0L); }
						filtration.put(reportFilter.getField(), reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getCasinoBetCount()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_GAME_CASINO_WIN_AMOUNT_CENTS)) {
						if (row.getCasinoWinAmountCents() == null) { row.setCasinoBonusWinAmountCents(0L); }
						filtration.put(reportFilter.getField(), reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getCasinoWinAmountCents()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_GAME_CASINO_WIN_COUNT)) {
						if (row.getCasinoWinCount() == null) { row.setCasinoWinCount(0L); }
						filtration.put(reportFilter.getField(), reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getCasinoWinCount()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_GAME_CASINO_NET_AMOUNT_CENTS)) {
						if (row.getCasinoNetAmountCents() == null) { row.setCasinoNetAmountCents(0L);; }
						filtration.put(reportFilter.getField(), reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getCasinoNetAmountCents()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_GAME_CASINO_BONUS_BET_AMOUNT_CENTS)) {
						if (row.getCasinoBonusBetAmountCents() == null) { row.setCasinoBonusBetAmountCents(0L); }
						filtration.put(reportFilter.getField(), reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getCasinoBonusBetAmountCents()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_GAME_CASINO_BONUS_BET_COUNT)) {
						if (row.getCasinoBonusBetCount() == null) { row.setCasinoBonusBetCount(0L); }
						filtration.put(reportFilter.getField(), reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getCasinoBonusBetCount()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_GAME_CASINO_BONUS_WIN_AMOUNT_CENTS)) {
						if (row.getCasinoBonusWinAmountCents() == null) { row.setCasinoBonusWinAmountCents(0L); }
						filtration.put(reportFilter.getField(), reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getCasinoBonusWinAmountCents()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_GAME_CASINO_BONUS_WIN_COUNT)) {
						if (row.getCasinoBonusWinCount() == null) { row.setCasinoBonusWinCount(0L); }
						filtration.put(reportFilter.getField(), reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getCasinoBonusWinCount()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_GAME_CASINO_BONUS_NET_AMOUNT_CENTS)) {
						if (row.getCasinoBonusNetAmountCents() == null) { row.setCasinoBonusNetAmountCents(0L); }
						filtration.put(reportFilter.getField(), reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getCasinoBonusNetAmountCents()));
					}
				}
				
				if (filtration.size() > 0) {
					boolean matchAllFilters = (rev.getAllFiltersApplicable() != null)? rev.getAllFiltersApplicable(): false;
					boolean matchedAtleastOneFilter = false;
					for (Map.Entry<String, Boolean> entry: filtration.entrySet()) {
						if (matchAllFilters && !entry.getValue()) continue gamesLoop;
						if (entry.getValue()) matchedAtleastOneFilter = true;
					}
					if (!matchedAtleastOneFilter) continue gamesLoop;
				}
				
				reportRunResultsRepository.save(row);
				
				filteredRecords++;
				reportRun.setFilteredRecords(filteredRecords);
				reportRun = reportRunRepository.save(reportRun);
			}
		}
		
		Thread.sleep(1000);
	}
	
	@Async
	public void run(Report report, String startedBy) {
		try {
		
			if (report.getRunning() != null) {
				log.debug("Report " + report + " already running");
				return;
			}
			
			ReportRun runParent = ReportRun.builder()
					.reportRevision(report.getCurrent())
					.report(report)
					.startedBy(startedBy)
					.startedOn(new Date())
					.build();
			runParent = reportRunRepository.save(runParent);
			report.setRunning(runParent);
			report = reportRepository.save(report);
			
			ReportRun reportRun = runParent;
			
			try {
				int totalRuns = 1 + ((report.getCurrent().getCompareXperiods() != null)? report.getCurrent().getCompareXperiods(): 0);
				
				for (int i = 1; i <= totalRuns; i++) {
					ReportRevision rev = reportRun.getReportRevision();
					int runGranularityOffset = (rev.getGranularityOffset() != null)? rev.getGranularityOffset(): 0;
					
					if (i > 1) {
						reportRun = ReportRun.builder()
									.reportRevision(report.getCurrent())
									.report(report)
									.startedBy(startedBy)
									.startedOn(new Date())
									.runParent(runParent)
									.build();
						reportRun = reportRunRepository.save(reportRun);
						report.setRunning(reportRun);
						report = reportRepository.save(report);
						runGranularityOffset += i-1;
					}
					
					run(reportRun, runGranularityOffset);
					
					log.info("Report run started: " + reportRun);
				
					// It might be some time after we got the last object, and we allow updates of the main report object
					// while reports run, so lets get the latest.
					report = reportRepository.findOne(report.getId());
					reportRun = reportRunRepository.findOne(reportRun.getId());
					
					reportRun.setCompleted(true);
					reportRun.setCompletedOn(new Date());
					report.setLastCompleted(reportRun);
					report.setScheduledDate(null);
					reportRunRepository.save(reportRun);
					
					if (i == 1) {
						reportActionService.processActions(reportRun);
					}
					
					report.setRunRetriesCount(null);
					
					log.info("Report completed: " + reportRun);
				}
			} catch (Exception ex) {
				log.error("Report run failed: " + reportRun + " " + ex, ex);
				reportRun = reportRunRepository.findOne(reportRun.getId());
				reportRun.setCompleted(true);
				reportRun.setCompletedOn(new Date());
				reportRun.setFailed(true);
				reportRun.setFailReason(ex.getMessage());
				report = reportRepository.findOne(report.getId());
				report.setLastFailed(reportRun);
				report.setRunRetriesCount((report.getRunRetriesCount() != null)? (report.getRunRetriesCount() + 1): 1);
				reportRunRepository.save(reportRun);
			}
			
			report.setRunning(null);
			reportRepository.save(report);
			
		} catch (Exception e) {
			log.error("Unhandled exception runnning report " + report + " " + e, e);
		}
	}
}