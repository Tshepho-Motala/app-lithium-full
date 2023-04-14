package lithium.service.cashier.processor.skrill.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="response")
public class TransferPrepareResponse {
	/**
	 * Returned if the authorisation and payment preparation is successful. The SID (Session Identifier) must be submitted in your transfer execution request.
	 */
	@XmlElement
	private String sid;
	/**
	 * Included if an error occurs.
	 */
	@XmlElement
	private Error error;
}
