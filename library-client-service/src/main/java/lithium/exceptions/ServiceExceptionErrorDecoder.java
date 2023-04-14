package lithium.exceptions;

import lithium.exceptions.decoder.ReflectionErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceExceptionErrorDecoder
		extends ReflectionErrorDecoder<CustomHttpErrorCodeResponse, ErrorCodeException> {

	public ServiceExceptionErrorDecoder(Class<?> apiClass) {
		super(apiClass, CustomHttpErrorCodeResponse.class, ErrorCodeException.class, "lithium.exceptions");
		log.info("Feign service exception decoder is being constructed for: " + apiClass.getCanonicalName());
	}

	@Override
	protected String getKeyFromException(ErrorCodeException exception) {
		return exception.getErrorCode();
	}

	@Override
	protected String getKeyFromResponse(CustomHttpErrorCodeResponse apiResponse) {
		return apiResponse.getErrorCode();
	}

	@Override
	protected String getMessageFromResponse(CustomHttpErrorCodeResponse apiResponse) {
		return apiResponse.getMessage();
	}
}
