package lithium.service.user;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserLink;
import lithium.service.user.data.repositories.UserLinkRepository;
import lithium.service.user.exceptions.Status100InvalidInputDataException;
import lithium.service.user.services.UserLinkService;
import org.mockito.Mockito;
import org.modelmapper.internal.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.when;

public class ShowOnPlayerProfileTheOriginalAccountMyStepdefs {

  public static final String LIVESCORE_TEST_22 = "livescore_test/22";
  private User primaryUser;
  public static final String LIVESCORE_TEST_23 = "livescore_test/23";
  private User secondaryUser;
  private UserLink userLink;

  //testing playerlinks
  @Given("^Player_Link$")
  public void player_link() throws Status100InvalidInputDataException {
    ArrayList<UserLink> result = getUserLinks();
    userLink = getUserLinks().stream().findFirst().get();
    Assert.notNull(result);
    Assert.notNull(userLink);
  }

  private ArrayList<UserLink> getUserLinks() throws Status100InvalidInputDataException {

    setUpUserData();

    UserLinkRepository repository = getUserLinkRepository();

    UserLinkService userLinkService = new UserLinkService(null, null, repository, null,
        null, null, null, null);

    return userLinkService.findUserLinksByUser(primaryUser);
  }

  private UserLinkRepository getUserLinkRepository() {
    UserLinkRepository repository = Mockito.mock(UserLinkRepository.class);
    when(repository.findByPrimaryUserAndDeletedFalse(ArgumentMatchers.any(User.class)))
        .thenReturn(new ArrayList<>(Arrays.asList(new UserLink[]{userLink})));
    return repository;
  }

  private void setUpUserData() {
    userLink = new UserLink();

    primaryUser = new User();
    secondaryUser = new User();
    primaryUser.setGuid(LIVESCORE_TEST_22);
    secondaryUser.setGuid(LIVESCORE_TEST_23);

    userLink.setPrimaryUser(primaryUser);
    userLink.setSecondaryUser(secondaryUser);
  }

  @When("^Displaying$")
  public void displaying() throws Status100InvalidInputDataException {
    primaryUser = getPrimaryUser();
    String result = primaryUser.guid();
    assertEquals(result, LIVESCORE_TEST_22);

  }

  private User getPrimaryUser() throws Status100InvalidInputDataException {
    userLink = getUserLinks().stream().findFirst().get();
    primaryUser = userLink.getPrimaryUser();
    return primaryUser;
  }

  private User getSecondaryUser() throws Status100InvalidInputDataException {
    userLink = getUserLinks().stream().findFirst().get();
    secondaryUser = userLink.getSecondaryUser();
    return secondaryUser;
  }

  @Then("^Return_Primary_Player_Account$")
  public void return_primary_player_account() throws Status100InvalidInputDataException {
    primaryUser = getPrimaryUser();
    userLink = getUserLinks().stream().findFirst().get();
    assertEquals(LIVESCORE_TEST_22, primaryUser.guid());
    assertEquals(LIVESCORE_TEST_22, userLink.getPrimaryUser().guid());


  }

  @Then("^Return_Secondary_Player_Account$")
  public void return_secondary_player_account() throws Status100InvalidInputDataException {
    secondaryUser = getSecondaryUser();
    userLink = getUserLinks().stream().findFirst().get();
    assertEquals(LIVESCORE_TEST_23, secondaryUser.guid());
    assertEquals(LIVESCORE_TEST_23, userLink.getSecondaryUser().guid());
  }

  @Given("^Primary_Account$")
  public void primary_Account() throws Status100InvalidInputDataException {
    setUpUserData();
    assertNotNull(userLink.getPrimaryUser());
  }

  @Then("^Return_Primary_Player_Link_Column$")
  public void return_primary_player_link_column() {
    String playerLink = userLink.getPrimaryUser().guid();
    assertEquals(LIVESCORE_TEST_22, playerLink);
  }

  @Given("^Secondary_Account$")
  public void secondary_Account() {
    setUpUserData();
    assertNotNull(userLink.getSecondaryUser());
  }

  @Then("^Return_Secondary_Player_Link_Column$")
  public void return_secondary_player_link_column() {
    String playerLink = userLink.getSecondaryUser().guid();
    assertEquals(LIVESCORE_TEST_23, playerLink);
  }

  @Then("^Return_Player_With_LMS_Account_Only$")
  public void return_player_with_lms_account_only() {
    String playerLink = userLink.getSecondaryUser().guid();
    assertEquals(LIVESCORE_TEST_23, playerLink);
  }

}
