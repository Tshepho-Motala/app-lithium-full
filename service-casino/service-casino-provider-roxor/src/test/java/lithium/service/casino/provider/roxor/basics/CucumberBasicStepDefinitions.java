package lithium.service.casino.provider.roxor.basics;

import static org.junit.Assert.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CucumberBasicStepDefinitions {

//  @Before
  //  public void setUp()
  //  throws Exception
  //  {
  //    MockitoAnnotations.initMocks(this);
  //    //    .getDomainClient().findAllDomains();
  //    //    progressiveScheduledService.setCachingDomainClientService(cachingDomainClientService);
  //    ProgressiveScheduledService progressiveScheduledService = mock(ProgressiveScheduledService.class);
  //    doNothing().when(progressiveScheduledService).progressiveScheduler();
  //    given(cachingDomainClientService.getDomainClient()).willReturn(domainClient);
  //    given(cachingDomainClientService.getDomainClient().findAllDomains()).willReturn(
  //        (Response<Iterable<Domain>>) Arrays.asList(Domain.builder().build()));
  //  }

  private String today;
  private String actualAnswer;

  @Given( "The day of the week" )
  public void today_is_Sunday() {
    DateFormat formatter = new SimpleDateFormat("EEEE", Locale.US);
    today = formatter.format(new Date());
//    today = "Sunday";
  }

  @When( "I ask whether it's Friday yet" )
  public void i_ask_whether_it_s_Friday_yet() {
    actualAnswer = ("Friday".equalsIgnoreCase(today)?"YES!!!!":"Nope...");
  }

  @Then( "I should be told {string}" )
  public void i_should_be_told(String expectedAnswer) {
    assertEquals(expectedAnswer, actualAnswer);
  }
}
