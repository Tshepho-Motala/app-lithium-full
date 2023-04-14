package lithium.service.accounting.provider.internal.context.adjust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import lithium.service.accounting.objects.AdjustmentRequest;
import lithium.service.accounting.objects.AdjustmentResponse;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.CompleteTransactionV2;
import lithium.service.accounting.objects.TransactionStreamData;
import lithium.service.accounting.provider.internal.data.entities.Account;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class AdjustmentContext {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    //https://stackoverflow.com/a/68853132
    private static ObjectMapper mapper = new ObjectMapper().registerModule(new JodaModule());

	private AdjustmentRequest request;
	private AdjustmentResponse response;
    private ArrayList<TransactionStreamData> transactionStreamDataList;
    private ArrayList<ArrayList<LabelValue>> summaryLabelValueList;
    private ArrayList<Long> accountIdList;
    private HashMap<String, Domain> domains;
    private ArrayList<Account> accounts;
    private List<CompleteTransaction> completedTransactions;
    private List<CompleteTransactionV2> completeTransactionV2s;

    public void setRequest(AdjustmentRequest request) {
    	this.request = request;

    	// We should choose a sizes here that caters for most transactions so that we do not
        // incur the cost of increasing the arrays unnecessarily.
    	
        accountIdList = new ArrayList<>(request.getAdjustments().size() * 4);
        accounts = new ArrayList<>(request.getAdjustments().size() * 4);
    	transactionStreamDataList = new ArrayList<>(request.getAdjustments().size());
        summaryLabelValueList = new ArrayList<ArrayList<LabelValue>>(request.getAdjustments().size());
        domains = new HashMap<>(request.getAdjustments().size());
        completedTransactions = new ArrayList<>();
        completeTransactionV2s = new ArrayList<>();
    }
        	
	@Override
	public String toString() {
		
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			log.warn("Could not convert object to JSON: ", e);
			return super.toString();
		}
		
	}

}
