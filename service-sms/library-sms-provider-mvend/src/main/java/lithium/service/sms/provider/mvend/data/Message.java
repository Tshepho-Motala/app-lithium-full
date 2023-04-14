package lithium.service.sms.provider.mvend.data;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName="message")
public class Message {
	@JacksonXmlProperty(localName = "from")
	private String from;
	@JacksonXmlProperty(localName = "to")
	private String to;
	@JacksonXmlProperty(localName = "text")
	private String text;
}
