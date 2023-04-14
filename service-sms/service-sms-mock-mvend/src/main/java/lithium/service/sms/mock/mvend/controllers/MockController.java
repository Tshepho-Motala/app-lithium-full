package lithium.service.sms.mock.mvend.controllers;

import lithium.service.sms.provider.mvend.data.MvendRequest;
import lithium.service.sms.provider.mvend.data.MvendResponse;
import lithium.service.sms.provider.mvend.data.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MockController {

	@PostMapping(path = "/api", produces= MediaType.APPLICATION_XML_VALUE, consumes=MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody MvendResponse mock(
		@RequestBody MvendRequest mvendRequest
	) throws Exception {
		log.info("Received request to send sms: " + mvendRequest);

		return MvendResponse.builder()
			.response(
				Response.builder()
				.code(0)
				.message("OK")
				.build()
			)
			.build();
	}
}