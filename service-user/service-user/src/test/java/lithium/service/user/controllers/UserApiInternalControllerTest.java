package lithium.service.user.controllers;


import lithium.client.changelog.objects.ChangeLogFieldChange;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class UserApiInternalControllerTest {

  UserApiInternalController userApiInternalController = new UserApiInternalController();

  @Test
  public void shouldAddToChangeLogFieldsWhenCurrentValueIsNull() {
    List<ChangeLogFieldChange> fields = new ArrayList<>();

    userApiInternalController.addChangedCommsFieldToChangeLogFieldChanges(fields, null, "emailOptOut");

    Assert.assertEquals(1, fields.size());
  }

  @Test
  public void shouldAddToChangeLogFieldsWhenCurrentValueIsFalse() {
    List<ChangeLogFieldChange> fields = new ArrayList<>();

    userApiInternalController.addChangedCommsFieldToChangeLogFieldChanges(fields, false, "emailOptOut");

    Assert.assertEquals(1, fields.size());
  }

  @Test
  public void shouldNotAddToChangeLogFieldsWhenCurrentValueIsTrue() {
    List<ChangeLogFieldChange> fields = new ArrayList<>();

    userApiInternalController.addChangedCommsFieldToChangeLogFieldChanges(fields, true, "emailOptOut");

    Assert.assertEquals(0, fields.size());
  }
}
