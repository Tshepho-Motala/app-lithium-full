package lithium.service.cdn.provider.google.api.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lithium.service.Response.Status;
import lithium.service.cdn.provider.google.service.storage.RequestInitializable;
import lithium.service.cdn.provider.google.service.template.TemplateService;
import lithium.service.cdn.provider.google.storage.objects.Template;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
public class BackofficeControllerTest {

  private final static String DOMAIN_NAME = "testDomain";
  private final static String TEMPLATE_NAME = "testTemplateName";
  private final static String TEMPLATE_LANGUAGE = "en";
  private final static String TEMPLATE_BO_URL = String.format("/backoffice/%s/template/%s", DOMAIN_NAME, TEMPLATE_NAME);

  private static final ObjectMapper mapper = new ObjectMapper();

  @InjectMocks
  private BackofficeController backofficeController;

  private MockMvc mockMvc;

  @Mock
  private TemplateService templateService;

  @Mock
  private RequestInitializable storageDetails;


  @Before
  public void init() {
    mockMvc = MockMvcBuilders.standaloneSetup(backofficeController).build();
  }

  @Test
  public void exceptionHandlerShouldInterceptExceptions() throws Exception {
    String errorMessage = "Test error";
    given(templateService.createOrUpdate(any(Template.class), anyString(), anyString())).willThrow(new RuntimeException(errorMessage));

    mockMvc
        .perform(post(TEMPLATE_BO_URL + "/" + TEMPLATE_LANGUAGE).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(getTemplate())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is(Status.INTERNAL_SERVER_ERROR.id())))
        .andExpect(jsonPath("$.message", is(errorMessage)));

    verify(storageDetails).initialize(DOMAIN_NAME);
  }

  @Test
  public void createShouldReturnOkIfSuccess() throws Exception {
    mockMvc
        .perform(post(TEMPLATE_BO_URL + "/" + TEMPLATE_LANGUAGE).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(getTemplate())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is(Status.OK.id())));

    verify(templateService).createOrUpdate(eq(getTemplate()), eq(TEMPLATE_NAME), eq(TEMPLATE_LANGUAGE));
  }

  @Test
  public void deleteShouldReturnOkIfSuccess() throws Exception {
    mockMvc
        .perform(delete(TEMPLATE_BO_URL + "/"+ TEMPLATE_LANGUAGE).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is(Status.OK.id())));

    verify(storageDetails).initialize(DOMAIN_NAME);
    verify(templateService).delete(eq(TEMPLATE_NAME),eq(TEMPLATE_LANGUAGE));
  }

  @Test
  public void getLinkShouldReturnLinkIfFound() throws Exception {
    String link = "https://cdn.net/bucket-prefix/" + TEMPLATE_BO_URL + ".html";
    given(templateService.getLink(eq(TEMPLATE_NAME),eq(TEMPLATE_LANGUAGE))).willReturn(Optional.of(link));

    mockMvc
        .perform(get(TEMPLATE_BO_URL + "/link/" +TEMPLATE_LANGUAGE).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is(Status.OK.id())))
        .andExpect(jsonPath("$.data", is(link)));

    verify(storageDetails).initialize(DOMAIN_NAME);
    verify(templateService).getLink(eq(TEMPLATE_NAME), eq(TEMPLATE_LANGUAGE));
  }

  @Test
  public void getLinkShouldReturnNotFoundWhenFileIsAbsendOnCDN() throws Exception {
    given(templateService.getLink(eq(TEMPLATE_NAME), eq(TEMPLATE_LANGUAGE))).willReturn(Optional.empty());

    mockMvc
        .perform(get(TEMPLATE_BO_URL + "/link/" + TEMPLATE_LANGUAGE).contentType(MediaType.APPLICATION_JSON))
        //.andDo(MockMvcResultHandlers.print()) //to debug response
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is(Status.NOT_FOUND.id())))
        .andExpect(jsonPath("$.data", is((String) null)));

    verify(storageDetails).initialize(DOMAIN_NAME);
    verify(templateService).getLink(eq(TEMPLATE_NAME),eq(TEMPLATE_LANGUAGE));
  }

  private static Template getTemplate() {
    Template template = new Template();
    template.setContent("<p>Test</p>");
    template.setHead("Head");
    return template;
  }

}
