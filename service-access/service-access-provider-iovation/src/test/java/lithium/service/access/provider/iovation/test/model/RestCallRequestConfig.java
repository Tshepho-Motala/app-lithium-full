package lithium.service.access.provider.iovation.test.model;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestCallRequestConfig {
	private MockMvc mvc;
	private String endpoint;
	private Object request;
	private boolean lithiumSuccess;
	private String expectedMessage;
	private RequestMethod method;
	private  String contentType;
	private MultiValueMap<String, String> params;
}
