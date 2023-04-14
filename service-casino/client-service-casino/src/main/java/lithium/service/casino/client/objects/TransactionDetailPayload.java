package lithium.service.casino.client.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.casino.exceptions.Status422InvalidParameterProvidedException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class TransactionDetailPayload implements Serializable {
	//Request data
	private String providerGuid;  //domain/service-casino-provider-x
	private String providerTransactionGuid; //Whatever provider transaction id is
	private String transactionType; //Not even sure if this is relevant but it is additional scope reduction for lookup by providers

	//Response data
	private String transactionDetailUrl;

	@JsonIgnore
	public String getDomainFromProviderGuid() throws Status422InvalidParameterProvidedException {
		return validateProviderGuid()[0];
	}

	@JsonIgnore
	public String getProviderFromProviderGuid() throws Status422InvalidParameterProvidedException {
		return validateProviderGuid()[1];
	}

	@JsonIgnore
	private String[] validateProviderGuid() throws Status422InvalidParameterProvidedException {
		if (providerGuid == null) {
			throw new Status422InvalidParameterProvidedException("providerGuid: " + providerGuid);
		}
		String[] domainAndProvider = providerGuid.split("/");
		if (domainAndProvider.length < 2) {
			throw new Status422InvalidParameterProvidedException("providerGuid: " + providerGuid);
		}
		return domainAndProvider;
	}

}
