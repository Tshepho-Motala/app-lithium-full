package lithium.service.user.threshold;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/*@Ignore
@ExtendWith( {RestDocumentationExtension.class, SpringExtension.class} )
@SpringBootTest( classes = ServiceUserThresholdApplication.class )*/
public class BackofficeThresholdLossLimitIntegrationTest {
//
//  @Autowired
//  private ObjectMapper objectMapper;
//
//  private MockMvc mockMvc;
//
//  private static final String GRANULARITY = "2";
//  private static final String PERCENTAGE = "55.55";
//  private static final String SYSTEM_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJsYXN0TmFtZSI6IlNjaG9lbWFuIiwibGFzdExvZ2luIjoxNjc0NDcxMjAxNTczLCJqd3RVc2VyIjoiSDRzSUFBQUFBQUFBLzUyU1NVL0RNQkNGLzBvMTV3VEloa2xPRkpVRGdsWW9xQ2RVSVNlZXBCYlo1RGhjcXZ4M0prMlgwSVFEM042OFozOHpYbllnSWJBTWFDQUFMbkpaZ0FFSjZWQnkzdW1NOUZ1OExUSGZsMGhsM1ZTbzdnVW12TW4wVlZ6bTVJdWVJZ3JLRDBubjFoQzg3K0RDN01xd0xQVnNVZVo4MzdBU0VDUThxOUVBZGRveFh5eWZWaFFTUTZ1R0l0R0xkdE1hL1lwTWZtRWRsd3FQMUplQk1XQldnLzZqdlIrRlRGRkpQbUxNVnFlZ0VzY1pxb3UycURRNU9VNXdJOVJqNXNQZS9JMDNoalNmWThiNitiOGpGZG5FS1ZGdlVXVzhvTWY2eTJRNWlxbExXeDdzbjZEejYvZlRLV2czQm5CU3JpOWNZVEZtTW5RODAzVkVZa2FSN1p2c2xqazJzMjNQdWZPNlQ1QjIvM0Q5K2hqT3FVclB5R3VyU3lWMWM5bU5iN2Zma1U2ZWlkQUNBQUE9Iiwic2Vzc2lvbktleSI6ImNhOGFjODFmLTJkMDctNDJmNy1iNTI3LTE0NmM2OTNkMzQzMyIsImxhc3RJUCI6IjA6MDowOjA6MDowOjA6MSIsInVzZXJfbmFtZSI6ImFkbWluIiwic2Vzc2lvbklkIjo0NzA5MiwidXNlckd1aWQiOiJkZWZhdWx0LzEiLCJ1c2VySWQiOjEsImF1dGhvcml0aWVzIjpbIlJPTEVfVkFMSURBVEVEX0VNQUlMIiwiUk9MRV9VU0VSIl0sImNsaWVudF9pZCI6ImFjbWUiLCJmaXJzdE5hbWUiOiJSaWFhbiIsImF1ZCI6WyJvYXV0aDItcmVzb3VyY2UiXSwic2NvcGUiOlsib3BlbmlkIl0sImRvbWFpbk5hbWUiOiJkZWZhdWx0IiwicmVnaXN0cmF0aW9uRGF0ZSI6MTU2ODU2MjM1NjAwMCwiZXhwIjoxNjc0NDk4MTkyLCJzaG9ydEd1aWQiOiJTVVBFUkEiLCJqdGkiOiI3b2JvWHNyUksycWl3am9WdGdvakxoc0tSc1EiLCJlbWFpbCI6InN1cGVyQGRlZmF1bHQuY29tIiwidXNlcm5hbWUiOiJhZG1pbiJ9.IWnXWEVdMM6-eROkcfuH3LwJZrofcCX9N-M61YoKnoLNYAXzzvf-wtWPLiZRor_DA4dvysScnOY8M-W3RP8khZkWLNOUc_ddXnj7BmKkQ4aCnaVroH81VvV1TSVnH1Mz0F0OafyMDL7Mbr-PyJuPuBoM7UOYR2azykekfcxFpPOdGMQ4YFF4Rfw2610FtUs62dw4xNKCCGbPmSf6YG-Nune8Bd1C05kPO3ElS7QgzUD6iCnps3RA7JvGdynT9Mq9aoMKnxoIACtrPBI9xvPNskhI6kMtvQhik2pEzGeTehNaKHGhmdcGyjw1Ea65-OaocJfWXSvZBDTfvdOPK9HCg-dWq_dtJef8YPWM2IjPHzRAXNsA-xQOy6T-xQ8ImalZ_lZxk93TpD14WDRwZlP4iZNliSrbIffepO5mkeTZn0yBIMtipW45ay_XLOEz_p5PrTVPa1pOKNvAsrAVkXDHyzfRGIZZiDXiOevKhpmJgVbWTNCpE3XZegv-9cZ84q5O-cQHQjcwNI_WjHOxb7NTGsJXQ4e0rY-kH0gTQi8w3IuZ16o54_AvH23Z1B_CZLoNwj2DwS0qjqDGrWomXnljTc5EALniZPhAp-SYsxXseMo85UpWZuTomGeki6hEsVkElDjpPQ1i0u0vUmpKnmyT7RyVjsDCyL2qx30B_xLmnY8";
//
//  @BeforeEach
//  public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
//    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//        .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
//        .apply(documentationConfiguration(restDocumentation).uris()
//            .and()
//            .snippets()
//            .withDefaults(HttpDocumentation.httpRequest(), HttpDocumentation.httpResponse()))
//        //            .withDefaults(CliDocumentation.curlRequest(), HttpDocumentation.httpRequest(), HttpDocumentation.httpResponse(),
//        //                AutoDocumentation.requestFields(), AutoDocumentation.responseFields(), AutoDocumentation.pathParameters(),
//        //                AutoDocumentation.requestParameters(), AutoDocumentation.description(), AutoDocumentation.methodAndPath(),
//        //                AutoDocumentation.section()))
//        .build();
//  }
//
//  //  @Test
//  @Ignore
//  public void saveDefault()
//  throws Exception
//  {
//    Map<String, Object> crud = new HashMap<>();
//    crud.put("granularity", GRANULARITY);
//    crud.put("percentage", PERCENTAGE);
//    crud.put("id", 1);
//    HttpHeaders httpHeaders = new HttpHeaders();
//    httpHeaders.add("Authorization", "Bearer " + SYSTEM_TOKEN);
//    this.mockMvc.perform(post("/backoffice/threshold/loss-limit/{domainName}/v1/save", "livescore_uk").headers(httpHeaders)
//        .queryParam("granularity", GRANULARITY)
//        .queryParam("percentage", PERCENTAGE)
//        .queryParam("id", "1")
//        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//        .content(this.objectMapper.writeValueAsString(crud))).andExpect(status().isOk());
//    //        .andDo(document("find-default", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
//    //            requestFields(fieldWithPath("granularity").description("Granularity details here"))));
//  }
//
//  //@Test
//  @Ignore
//  public void findDefault()
//  throws Exception
//  {
//    Map<String, Object> crud = new HashMap<>();
//    crud.put("granularity", GRANULARITY);
//    this.mockMvc.perform(get("/backoffice/threshold/loss-limit/{domainName}/v1/find", "livescore_uk").queryParam("granularity", GRANULARITY)
//        .contentType(MediaType.APPLICATION_JSON_VALUE)
//        .content(this.objectMapper.writeValueAsString(crud))).andExpect(status().isOk());
//    //        .andDo(document("find-default", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
//    //            requestFields(fieldWithPath("granularity").description("Granularity details here"))));
//  }
//
//  //@Test
//  @Ignore
//  public void findAge()
//  throws Exception
//  {
//    Map<String, Object> crud = new HashMap<>();
//    crud.put("granularity", GRANULARITY);
//    crud.put("ageMin", 18);
//    crud.put("ageMax", 25);
//    this.mockMvc.perform(get("/backoffice/threshold/loss-limit/{domainName}/v1/find", "livescore_uk").queryParam("granularity", GRANULARITY)
//        .queryParam("ageMin", "18")
//        .queryParam("ageMax", "25")
//        .contentType(MediaType.APPLICATION_JSON_VALUE)
//        .content(this.objectMapper.writeValueAsString(crud))).andExpect(status().isOk());
//    //        .andDo(
//    //            document("find-age",
//    //                preprocessRequest(prettyPrint()),
//    //                preprocessResponse(prettyPrint()),
//    //                requestFields(
//    //                    fieldWithPath("granularity").description("Granularity details here."),
//    //                    fieldWithPath("ageMin").description("Age lower level."),
//    //                    fieldWithPath("ageMax").description("Age upper level.")
//    //                )
//    //            )
//    //        );
//  }
}
