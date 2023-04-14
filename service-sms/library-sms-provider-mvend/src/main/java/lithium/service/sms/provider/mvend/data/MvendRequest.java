package lithium.service.sms.provider.mvend.data;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName="request")
public class MvendRequest {
	private String hash;
	private String timestamp;
	@JacksonXmlProperty(localName = "message")
	private Message message;
}