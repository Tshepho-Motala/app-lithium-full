package lithium.service.casino.client.objects.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class Request implements Serializable {
	private static final long serialVersionUID = 1L;
	protected String domainName;
	protected String hash;
	protected String providerGuid;
}
