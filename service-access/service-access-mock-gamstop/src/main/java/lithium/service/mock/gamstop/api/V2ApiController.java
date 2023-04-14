package lithium.service.mock.gamstop.api;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import lithium.service.mock.gamstop.model.Exclusions;
import lithium.service.mock.gamstop.model.Person;
import lithium.service.mock.gamstop.model.Persons;
import lithium.service.mock.gamstop.service.BlockedPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-09-22T18:03:17.345Z")

@Controller
public class V2ApiController implements V2Api {

    private static final Logger log = LoggerFactory.getLogger(V2ApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private BlockedPersonService blockedPersonService;

    @org.springframework.beans.factory.annotation.Autowired
    public V2ApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> v2PostSingle(@Parameter(description = "First name of person", required=true) @RequestParam(value="firstName", required=true)  String firstName,
                                             @Parameter(description = "Last name of person", required=true) @RequestParam(value="lastName", required=true)  String lastName,
                                             @Parameter(description = "Date of birth in ISO format (yyyy-mm-dd)", required=true) @RequestParam(value="dateOfBirth", required=true)  String dateOfBirth,
                                             @Parameter(description = "Email address", required=true) @RequestParam(value="email", required=true)  String email,
                                             @Parameter(description = "Postcode - spaces not significant", required=true) @RequestParam(value="postcode", required=true)  String postcode,
                                             @Parameter(description = "UK mobile telephone number which may include spaces, hyphens and optionally be prefixed with the international dialling code (+44, 0044, +353, 00353).", required=true) @RequestParam(value="mobile", required=true)  String mobile,
                                             @Parameter(description = "A freeform field that is put into the audit log that can be used by the caller to identify a request. This might be something to indicate the person being checked (in some psuedononymous fashion), a unique request ID, or a trace ID from a system such as zipkin" ) @RequestHeader(value="X-Trace-Id", required=false) String xTraceId) throws ParseException {
        String accept = request.getHeader("Accept");
        ResponseEntity<Void> responseEntity;
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        if (blockedPersonService.hasSEhistory(firstName, lastName, dateOfBirth, email, postcode, mobile)){
            headers.add("X-Exclusion", "P");
            headers.add("X-Unique-Id", "ae24040a-5d8dd848-00000041");
            responseEntity = new ResponseEntity<Void>(headers, HttpStatus.OK);
        }else if (blockedPersonService.isBlocked(firstName, lastName, dateOfBirth, email, postcode, mobile)){
            headers.add("X-Exclusion", "Y");
            headers.add("X-Unique-Id", "ae24040a-5d8dd848-00000041");
            responseEntity = new ResponseEntity<Void>(headers, HttpStatus.OK);
        } else {
            headers.add("X-Exclusion", "N");
            headers.add("X-Unique-Id", "ae24040a-5d8dd848-00000041");
            responseEntity = new ResponseEntity<Void>(headers, HttpStatus.OK);
        }
        return responseEntity;
    }

    public ResponseEntity<Exclusions> v2PostBatch(@Parameter(name = "The list of persons to search" ,required=true )  @Valid @RequestBody Persons persons, @Parameter(name = "A freeform field that is put into the audit log that can be used by the caller to identify a request. This might be something to indicate the person being checked (in some psuedononymous fashion), a unique request ID, or a trace ID from a system such as zipkin" ) @RequestHeader(value="X-Trace-Id", required=false) String xTraceId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {

                StringBuilder sb = new StringBuilder();
                sb.append("[");
                List<String> blocked = Arrays.asList("");
                int size = persons.size();
                int counter = 0;
                for (Person person: persons) {
                    if (blockedPersonService.hasSEhistory(person)) {
                        sb.append("{\"msRequestId\":\"1be7260f-5f6b57b7-0000000"+counter+"\",\"exclusion\":\"P\",\"correlationId\":\""+person.getCorrelationId()+"\"}");
                    } else if (blockedPersonService.isBlocked(person)) {
                        sb.append("{\"msRequestId\":\"1be7260f-5f6b57b7-0000000"+counter+"\",\"exclusion\":\"Y\",\"correlationId\":\""+person.getCorrelationId()+"\"}");
                    }else  {
                        sb.append("{\"msRequestId\":\"1be7260f-5f6b57b7-0000000"+counter+"\",\"exclusion\":\"N\",\"correlationId\":\""+person.getCorrelationId()+"\"}");
                    }
                    counter++;
                    sb.append(",");
                }
                sb.append("]");
                String response = sb.toString();
                log.info(response);
                response = response.replace(",]","]");
                return new ResponseEntity<Exclusions>(objectMapper.readValue(response, Exclusions.class), HttpStatus.OK);
            } catch (IOException | ParseException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Exclusions>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Exclusions>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Persons> v2Upload(@Valid @RequestBody Persons persons) {
        Persons people = blockedPersonService.addBlockedPersons(persons);
        return new ResponseEntity<Persons>(people, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Persons> v2Reset() {
        Persons people = blockedPersonService.resetBlockedPersons();
        return new ResponseEntity<Persons>(people, HttpStatus.OK);
    }

}
