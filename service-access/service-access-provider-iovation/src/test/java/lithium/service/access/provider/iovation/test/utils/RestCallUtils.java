package lithium.service.access.provider.iovation.test.utils;

import static org.apache.commons.collections.MapUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hamcrest.Matcher;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.access.provider.iovation.test.model.RestCallRequestConfig;

@SuppressWarnings({ "rawtypes" })
public class RestCallUtils {

	/**
	 * Builds a verifier given the expectations passed in as parameters
	 * 
	 * @param lithiumSuccess - whether the operation was successful from the service
	 * @param message        - the expected message on a successful operation
	 * @param errorMessage   - the expected error message from an unsuccessful
	 *                       operation
	 * 
	 * @return
	 */
	public static Map<String, Matcher> populateResultVerifier(Boolean lithiumSuccess, String message) {
		Map<String, Matcher> verifier = new HashMap<>();
		verifier.put("$.data.successful", equalTo(lithiumSuccess));

		if (!lithiumSuccess) {
			verifier.put("$.data.errorMessage", notNullValue());
			verifier.put("$.data.errorMessage", equalTo(message));
		}

		verifier.put("$.data.message", notNullValue());
		verifier.put("$.data.message", equalTo(message));

		return verifier;
	}

	/**
	 * Returns a typed null
	 * 
	 * @return
	 */
	public static MultiValueMap<String, String> getEmptyHttpParameters() {
		return null;
	}

	/**
	 * Prepares an http action with the specified arguments.
	 * It will throw an exception if you attempt to use unsupported actions
	 * 
	 * @param request - a request object encapsulating the required fields to execute a rest call
	 * 
	 * @return {@link MockHttpServletRequestBuilder}
	 * 
	 * @throws Exception
	 */
	public static MockHttpServletRequestBuilder prepareRestCallByMethod(RestCallRequestConfig request) throws Exception {
		
		MockHttpServletRequestBuilder requestBuilder = null;
		RequestMethod method = request.getMethod();
		String resource = request.getEndpoint();
		MultiValueMap<String, String> params = request.getParams();
		String value = stringify(request.getRequest());

		switch (method.ordinal()) {
		case 0: // GET
			requestBuilder = get(resource);
			break;
		case 2: // POST
			requestBuilder = post(resource);
			break;
		case 3: // PUT
			requestBuilder = put(resource);
			break;
		case 4: // PATCH
			requestBuilder = patch(resource);
			break;
		case 5: // DELETE
			requestBuilder = delete(resource);
			break;
		default:
			throw new UnsupportedOperationException("Invalid http action method");
		}

		if (isNotEmpty(params)) {
			requestBuilder.params(params);
		}

		if (isNotBlank(value)) {
			requestBuilder.content(value);
		}
		
		return requestBuilder;
	}

	/**
	 * Converts an object to its JSON parsable string
	 * 
	 * @param anyObject - the object to convert to a json string
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String stringify(Object anyObject) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(anyObject);
	}
}
