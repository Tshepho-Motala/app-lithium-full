package lithium.service.report.player.trans.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.service.casino.CasinoClientService;
import lithium.service.casino.client.CasinoTransactionDetailClient;
import lithium.service.casino.client.objects.TransactionDetailPayload;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingReportingClient;
import lithium.service.accounting.objects.LabelValue;
import lithium.service.accounting.objects.TransactionEntry;
import lithium.service.cashier.CashierTransactionLabels;
import lithium.service.casino.CasinoTransactionLabels;
import lithium.service.casino.client.CasinoBonusClient;
import lithium.service.casino.client.data.BonusRevision;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.objects.Game;
import lithium.service.report.player.trans.data.entities.PlayerTransaction;
import lithium.service.report.player.trans.data.entities.PlayerTransactionQueryCriteria;
import lithium.service.report.player.trans.data.entities.PlayerTransactionRequest;
import lithium.service.report.player.trans.data.repositories.PlayerTransactionQueryCriteriaRepository;
import lithium.service.report.player.trans.data.repositories.PlayerTransactionRepository;
import lithium.service.report.player.trans.data.repositories.PlayerTransactionRequestRepository;
import lithium.service.report.player.trans.data.repositories.specifications.PlayerTransactionSpecification;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PlayerTransactionReportService implements CasinoTransactionLabels, CashierTransactionLabels {
	
	private static final int PAGE_SIZE = 100;
	private static final long PAGE_REQUEST_SLEEP = 100L;

	@Autowired private  LithiumServiceClientFactory lithiumServiceClientFactory;
	@Autowired private PlayerTransactionRepository playerTranRepo;
	@Autowired private PlayerTransactionQueryCriteriaRepository playerTranQueryCriteriaRepo;
	@Autowired private PlayerTransactionRequestRepository playerTranRequestRepo;
	@Autowired private CasinoClientService casinoClientService;
	
	//TODO: Possibly add retry in here or have a schedule try at a later stage to perform the data prep
	@Async
	public void generateTransactionData(DateTime startDate, DateTime endDate, String userGuid, Principal principal, PlayerTransactionRequest ptr) {
		//TODO: Check if data exists
		//TODO: Check if data is complete
		//TODO: Handle cases of incomplete data executions (service restart/downtime)
		//TODO: Handle data regeneration if end date  > now and new request is received
		//TODO: Handle data enrichment when primary data is retrieved
		
		PlayerTransactionQueryCriteria queryCriteria = findOrCreateQueryCriteria(startDate, endDate, userGuid);
		ptr.setQueryCriteria(queryCriteria);
		ptr = playerTranRequestRepo.save(ptr);
		
		if(!(queryCriteria.isDataPurged() || queryCriteria.getCompletedDate() == null)) return;
		
		DataTableRequest request = new DataTableRequest();
		request.setPageRequest(PageRequest.of(0, PAGE_SIZE));
		boolean hasMoreData = true;
		
		try {
			while (hasMoreData) {
				DataTableResponse<TransactionEntry> dtResponse = getAccountingReportingClient().findPlayerTransactionsForDateRangeAndUserGuid(startDate.toString(), endDate.toString(), userGuid, PAGE_SIZE+"", request.getPageRequest().getOffset()+"", PAGE_SIZE+"");
				
				handleTransactionEntryPage(dtResponse.getData(), queryCriteria, request.getPageRequest().getPageNumber());
				
				if (dtResponse.getData().size() < PAGE_SIZE) {
					hasMoreData = false;
				} else {
					request.setPageRequest(PageRequest.of(request.getPageRequest().getPageNumber()+1,PAGE_SIZE));
					Thread.sleep(PAGE_REQUEST_SLEEP);
				}
			}
			
		} catch (Exception e) {
			log.error("Unable to retrieve transaction entry information from accounting service: "+ e.getMessage(), e);
		}
		
		queryCriteria.setCompletedDate(new DateTime().toDate());
		queryCriteria.setDataPurged(false);
		queryCriteria = playerTranQueryCriteriaRepo.save(queryCriteria);
	}
	
	private void handleTransactionEntryPage(ArrayList<TransactionEntry> tranEntryList, PlayerTransactionQueryCriteria queryCriteria, int pageNumber) throws Exception {
		log.debug("Tran entry list: " + tranEntryList.toString());
		
		ArrayList<PlayerTransaction> ptList = new ArrayList<>(PAGE_SIZE);
		
		List<LabelValue> labelValueList = null;
		Long lastTranId = null;
		for(TransactionEntry te : tranEntryList) {
			
			//basic builder
			PlayerTransaction pt = PlayerTransaction.builder()
					.tranId(te.getTransactionId())
					.tranType(te.getTransactionType())
					.tranCurrency(te.getAccount().getCurrency().getCode())
					.tranEntryId(te.getId())
					.tranEntryDate(te.getDate())
					.tranEntryAmount(te.getAmountCents())
					.tranEntryAccountType(te.getAccount().getAccountType().getCode())
					.tranEntryAccountCode(te.getAccount().getAccountCode().getCode())
					.tranEntryAccountBalance(te.getPostEntryAccountBalanceCents())
					.userGuid(te.getAccount().getOwner().getGuid())
					.queryCriteria(queryCriteria)
					.build();
			
			//TODO: decide if we want to save before label enrichment (could be useful is label pull is async. maybe too much effort and incomplete data could exist)
			
			//label handler
			if(te.getTransactionId() != lastTranId) { // small optimization hack
				labelValueList = getAccountingReportingClient().findLabelsForTransaction(te.getTransactionId());
				lastTranId = te.getTransactionId(); 
			}
			String gameSessionId = null;
			for (LabelValue lv : labelValueList) {
				if (lv.getValue() == null) continue;
				
				switch (lv.getLabel().getName()) {
				case BONUS_REVISION_ID : {
					Long bonusRevisionId = Long.parseLong(lv.getValue());
					pt.setBonusRevisionId(bonusRevisionId);
					if(bonusRevisionId != null && bonusRevisionId > 0) {
						BonusRevision br = getBonusRevision(bonusRevisionId);
						if (br != null) {
							pt.setBonusName(br.getBonusName());
							pt.setBonusCode(br.getBonusCode());
						}
					}
				}
				break;
				case CasinoTransactionLabels.TRAN_ID_LABEL : pt.setExternalTranId(lv.getValue());
				break;
				case GAME_GUID_LABEL : {
					pt.setGameGuid(lv.getValue());
					pt.setGameName(getGameName(te.getAccount().getDomain().getName(), lv.getValue()));
				}
				break;
				case PLAYER_BONUS_HISTORY_ID : pt.setPlayerBonusHistoryId(Long.parseLong(lv.getValue()));
				break;
				case PROCESSING_METHOD_LABEL : pt.setProcessingMethod(lv.getValue());
				break;
				case CasinoTransactionLabels.PROVIDER_GUID_LABEL : pt.setProviderGuid(lv.getValue());
				break;
				case CasinoTransactionLabels.GAME_SESSION_ID_LABEL : gameSessionId = lv.getValue();
				break;
				case CasinoTransactionLabels.ACCOUNTING_CLIENT_LABEL : pt.setAccountingClientTranId(lv.getValue());
				break;
				case CasinoTransactionLabels.ACCOUNTING_CLIENT_RESPONSE_LABEL : pt.setAccountingClientExternalId(lv.getValue());
				break;
				default:
					log.warn("Found a label with no implementation: TE: " +te.toString()+ " LV:"+ lv.toString());
				}
					
			}
			
			//HACK HACK HACK
			if ((pt.getProviderGuid() != null && pt.getProviderGuid().toLowerCase().contains("rival")) && gameSessionId != null) {
				pt.setExternalTranId(gameSessionId);
			}
			
			ptList.add(pt);
		}
		playerTranRepo.saveAll(ptList);
	}
	
	private BonusRevision getBonusRevision(Long bonusRevisionId) {
		try {
			Response<BonusRevision> response = getCasinoBonusClient().findByBonusRevisionId(bonusRevisionId);
			return response.getData();
		} catch (Exception e) {
			log.error("Unable to find bonus revision using id: " + bonusRevisionId);
			return null;
		}
	}

	private String getGameName(String domainName, String gameGuid) {
		if (gameGuid == null || domainName == null || gameGuid.isEmpty() || domainName.isEmpty()) {
			return null;
		}
		
		if (gameGuid.startsWith(domainName)) {
			gameGuid = gameGuid.replace(domainName + "/", "");
		}
		
		if (!gameGuid.contains("/")) {
			int i = gameGuid.lastIndexOf("_");
			gameGuid = gameGuid.substring(0, i) + "/" + gameGuid.substring(i + 1, gameGuid.length());
		}
		
		try {
			Response<Game> response = getGameClient().findByGuidAndDomainName(domainName, gameGuid);
			return response.getData().getName();
		} catch (Exception e) {
			log.error("Unable to find game name for domain:" + domainName  +" gameGuid: " + gameGuid, e);
			return null;
		}
		
	}
	
	public boolean isPlayerTranDataReady(DateTime sDate, DateTime eDate, String userGuid) {
		PlayerTransactionQueryCriteria ptqc = playerTranQueryCriteriaRepo.findByHash(getHash(sDate, eDate, userGuid));
		if (ptqc == null || ptqc.getCompletedDate() == null || ptqc.isDataPurged()) {
			return false;
		}
		
		return true;
	}

	public boolean isPlayerTranDataProcessing(DateTime sDate, DateTime eDate, String userGuid) {
		PlayerTransactionQueryCriteria ptqc = playerTranQueryCriteriaRepo.findByHash(getHash(sDate, eDate, userGuid));
		if (ptqc != null && ptqc.getCompletedDate() == null  && !ptqc.isDataPurged()) {
			return true;
		}
		
		return false;
	}
	
	private String getHash(DateTime startDate, DateTime endDate, String userGuid) {
		return startDate.toString() + endDate.toString() + userGuid;
	}
	
	private PlayerTransactionQueryCriteria findOrCreateQueryCriteria(DateTime startDate, DateTime endDate, String userGuid) {
		PlayerTransactionQueryCriteria ptqc = playerTranQueryCriteriaRepo.findByHash(getHash(startDate, endDate, userGuid));
		
		if(ptqc == null) {
			ptqc = PlayerTransactionQueryCriteria.builder()
					.createdDate(new DateTime().toDate())
					.hash(getHash(startDate, endDate, userGuid))
					.startDate(startDate.toDate())
					.endDate(endDate.toDate())
					.userGuid(userGuid)
					.dataPurged(false)
					.completedDate(null)
					.build();
			
			ptqc = playerTranQueryCriteriaRepo.save(ptqc);
		}
		return ptqc;
	}
	
	private AccountingReportingClient getAccountingReportingClient() throws Exception {
		return lithiumServiceClientFactory.target(AccountingReportingClient.class, "service-accounting-provider-internal", true);
	}
	
	private GamesClient getGameClient() throws Exception {
		return lithiumServiceClientFactory.target(GamesClient.class, "service-games", true);
	}
	
	private CasinoBonusClient getCasinoBonusClient() throws Exception {
		return lithiumServiceClientFactory.target(CasinoBonusClient.class, "service-casino", true);
	}

	public DataTableResponse<PlayerTransaction> getPlayerTransactions(DataTableRequest request, DateTime startDate,
			DateTime endDate, String userGuid, PlayerTransactionRequest ptr) {
		
		DataTableResponse<PlayerTransaction> response = null;
		
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<PlayerTransaction> s = Specification.where(PlayerTransactionSpecification.find(userGuid, startDate.toDate(), endDate.toDate(), ptr.getQueryCriteria().getId(), request.getSearchValue()));
			response = new DataTableResponse<>(request, playerTranRepo.findAll(s, request.getPageRequest()));
		} else {
			response = new DataTableResponse<>(request, playerTranRepo.findByUserGuidAndTranEntryDateIsBetweenAndQueryCriteriaId(userGuid, startDate.toDate(), endDate.toDate(), ptr.getQueryCriteria().getId(), request.getPageRequest()));
		}

		final List<TransactionDetailPayload> detailRequestList = new ArrayList<>(response.getData().size());
		response.getData().forEach(playerTran -> {
			detailRequestList.add(
					TransactionDetailPayload.builder()
						.providerGuid(playerTran.getProviderGuid())
						.transactionType(playerTran.getTranType())
						.providerTransactionGuid(playerTran.getExternalTranId())
						.build());
		});
		try {
			final List<TransactionDetailPayload> detailResponseList  = casinoClientService.findTransactionDetailUrls(detailRequestList);
			DataTableResponse<PlayerTransaction> finalResponse = response;
			detailResponseList.forEach(detailResponse -> {
				finalResponse.getData()
						.stream()
						.filter((playerTran) -> playerTran.getExternalTranId() != null)
						.filter(playerTran -> playerTran.getExternalTranId().contentEquals(detailResponse.getProviderTransactionGuid()))
						.findFirst()
						.ifPresent(matchedTran -> {
							matchedTran.setExternalTransactionDetailUrl(detailResponse.getTransactionDetailUrl());
						});
			});
		} catch (ErrorCodeException e) {
			log.debug("Problem looking up transaction details: " + e.getMessage(), e);
		} catch (Exception e) {
			log.info("Problem looking up transaction details: " + e.getMessage(), e);
		}
		return response;
	}

	public PlayerTransactionQueryCriteria findQueryCriteria(DateTime sDate, DateTime eDate, String userGuid) {
		PlayerTransactionQueryCriteria ptqc = playerTranQueryCriteriaRepo.findByHash(getHash(sDate, eDate, userGuid));
		
		return ptqc;
	}
	
	public PlayerTransactionRequest registerPlayerTransactionRequest(DateTime sDate, DateTime eDate, String userGuid, Principal principal) {
		PlayerTransactionQueryCriteria ptqc = playerTranQueryCriteriaRepo.findByHash(getHash(sDate, eDate, userGuid));
		
		PlayerTransactionRequest ptr = PlayerTransactionRequest.builder()
				.author(principal.getName())
				.queryCriteria(ptqc)
				.requestDate(new DateTime().toDate())
				.build();
		
		ptr = playerTranRequestRepo.save(ptr);
		
		return ptr;
	}
	
	public Page<PlayerTransaction> getPlayerTranPage(DateTime startDate,
			DateTime endDate, String userGuid, Long queryCriteriaId, Pageable pageRequest) {
		return playerTranRepo.findByUserGuidAndTranEntryDateIsBetweenAndQueryCriteriaId(userGuid, startDate.toDate(), endDate.toDate(), queryCriteriaId, pageRequest);
	}
	
	public void purgeQueryCriteria(PlayerTransactionRequest ptr) {
		PlayerTransactionQueryCriteria criteria = ptr.getQueryCriteria();
		
		if(criteria == null) return;
		
		criteria.setDataPurged(true);
		playerTranQueryCriteriaRepo.save(criteria);
	}
}
