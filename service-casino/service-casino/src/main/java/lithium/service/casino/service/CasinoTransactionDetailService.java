package lithium.service.casino.service;

import lithium.service.casino.client.CasinoTransactionDetailClient;
import lithium.service.casino.client.objects.TransactionDetailPayload;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CasinoTransactionDetailService {
	@Autowired private LithiumServiceClientFactory serviceFactory;

	@Value("${lithium.service.casino.tran.detail.lookup.timeout:10000}")
	private Long tranDetailLookupTimeout;

	public TransactionDetailPayload handleTransactionDetailLookup(TransactionDetailPayload transactionDetailRequest) {
		try {
			return CasinoTransactionDetailClient.lookupService(
					CasinoTransactionDetailClient.class,
					transactionDetailRequest.getProviderFromProviderGuid(),
					serviceFactory)
					.findTransactionDetailUrl(transactionDetailRequest);
		} catch (Exception e) {
			String msg = "Problem looking up: " +transactionDetailRequest + " error: " + e.getMessage();
			log.debug(msg, e);
			log.info(msg);
		}
		return transactionDetailRequest;
	}

	public List<TransactionDetailPayload> handleTransactionDetailLookup(List<TransactionDetailPayload> transactionDetailRequestList) {
		TreeMap<String, List<TransactionDetailPayload>> transactionDetailTreeMap = new TreeMap<>();

		transactionDetailRequestList.stream()
				.filter(payload -> payload.getProviderGuid() != null)
				.forEach(transactionDetailPayload -> {
			// Adds the different providers to their own lists
			try {
				if (transactionDetailTreeMap.containsKey(transactionDetailPayload.getProviderFromProviderGuid())) {
					transactionDetailTreeMap.get(transactionDetailPayload.getProviderFromProviderGuid()).add(transactionDetailPayload);
				} else {
					ArrayList tmpList = new ArrayList<TransactionDetailPayload>(transactionDetailRequestList.size());
					tmpList.add(transactionDetailPayload);
					transactionDetailTreeMap
							.put(transactionDetailPayload.getProviderFromProviderGuid(), tmpList); //I feel there is a way to do the instance creation and method call using lambda, but I can't remember how
				}
			} catch (Exception e) {
				String msg = "Problem adding tran detail request for lookup: " +transactionDetailPayload + " error: " + e.getMessage();
				log.debug(msg, e);
				log.info(msg);
			}
		});
		List<TransactionDetailPayload> resultPayload = null;
		return runConcurrentDetailLookup(transactionDetailTreeMap); //This should block until all results are in
	}

	/**
	 * Creates the list of Callable items to lookup the transaction details.<br>
	 * All exceptions are caught since we don't care if it does not work for individual providers when executed.
	 * @param segmentedProviderMap
	 * @return
	 */
	private List<Callable<List<TransactionDetailPayload>>> createCallables(TreeMap<String, List<TransactionDetailPayload>> segmentedProviderMap) {
		List<Callable<List<TransactionDetailPayload>>> callables = new ArrayList<>(segmentedProviderMap.size());

		segmentedProviderMap.forEach((key, value) -> callables.add(() -> {
			try {
				//Service call execution that will take place when the Callables are invoked
				return CasinoTransactionDetailClient.lookupService(
						CasinoTransactionDetailClient.class,
						key,
						serviceFactory)
						.findTransactionDetailUrls(value);
			} catch (Exception ex) {
				String msg = "Failure to perform transaction detail lookup: " + key + ":" + value + " error: " + ex.getMessage();
				log.debug(msg, ex);
				log.info(msg);
			}
			return new ArrayList<>();
		}));
		return callables;
	}

	/**
	 * Performs a parallel lookup for transaction details form the various providers
	 * @param segmentedProviderMap
	 * @return
	 */
	private List<TransactionDetailPayload> runConcurrentDetailLookup(TreeMap<String, List<TransactionDetailPayload>> segmentedProviderMap) {
		List<TransactionDetailPayload> resultList = new ArrayList<>();
		if (segmentedProviderMap == null || segmentedProviderMap.isEmpty()) {
			return resultList;
		}
		ExecutorService executor = Executors.newFixedThreadPool(segmentedProviderMap.size());

		List<Callable<List<TransactionDetailPayload>>> callables = createCallables(segmentedProviderMap);

		try {
			//Invoke all lookup requests to the various casino providers. This will block until timeout or all results are back
			final List<Future<List<TransactionDetailPayload>>> futures =
					executor.invokeAll(callables, tranDetailLookupTimeout, TimeUnit.MILLISECONDS);

			//Got all the results or timeout was reached on some/all
			for (Future<List<TransactionDetailPayload>> future : futures) {
					try {
						if (future.isDone()) {
							resultList.addAll(future.get());
						}
					} catch (Exception e) {
						log.debug("Transaction detail request failed: " + e.getMessage(), e);
					}
			}
		} catch (Exception e) {
			log.error("Invocation of transaction detail jobs failed: " + e.getMessage(), e);
		} finally {
			return resultList;
		}
	}
}
