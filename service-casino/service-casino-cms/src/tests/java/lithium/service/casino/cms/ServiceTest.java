package lithium.service.casino.cms;

import lithium.service.casino.cms.services.LobbyService;
import lithium.service.casino.cms.storage.entities.LobbyRevision;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.objects.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Project: app-lithium
 * User: Tapiwanashe Shoshore
 * Date: 10/19/2021
 * Time: 6:37 AM
 * Created with IntelliJ IDEA
 */

public class ServiceTest {
    public static final String LIVESCORE_USER_1 = "livescore/user1";
    public static final String LIVESCORE_USER_2 = "livescore/user2";
    @InjectMocks
    LobbyService service;
    private List<User> serviceUserData;
    private User fakeUser;
    private User fakeModifier;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        fakeUser = new User();
        fakeUser.setGuid("livescore/user1");
        fakeUser.setFirstName("First");
        fakeUser.setLastName("LastName");

        fakeModifier = new User();
        fakeModifier.setGuid("livescore/user2");
        fakeModifier.setFirstName("Modifier");
        fakeModifier.setLastName("LastModifier");

        lithium.service.user.client.objects.User[] fakeUsers={fakeUser, fakeModifier};
        serviceUserData = Arrays.asList(fakeUsers);
    }
    @Test
    public void given_a_list_of_users_when_retrieving_user_data_then_return_created_by_and_modified_by() {
        fakeUser.setGuid(LIVESCORE_USER_1);
        fakeModifier.setGuid(LIVESCORE_USER_2);

        lithium.service.user.client.objects.User[] fakeUsers={fakeUser, fakeModifier};
        List< lithium.service.user.client.objects.User> serviceUserData = Arrays.asList(fakeUsers);
        User newCreatedBy = service.getUserByGuid(fakeUser.getGuid(), serviceUserData);
        User newModifiedBy = service.getUserByGuid(fakeModifier.getGuid(), serviceUserData);

        assertNotNull(newCreatedBy);
        assertNotNull(newModifiedBy);

        assertEquals("First LastName", newCreatedBy.getName());
        assertEquals("Modifier LastModifier", newModifiedBy.getName());
    }

    @Test
    public void given_a_list_of_lobby_revisions_set_user_data(){
        LobbyRevision fakeLobbyRevision = new LobbyRevision();
        List<LobbyRevision> lobbyRevisionData = new ArrayList<>();

        lobbyRevisionData.add(fakeLobbyRevision);
        Page<LobbyRevision> page = new PageImpl<>(lobbyRevisionData);

        lithium.service.casino.cms.storage.entities.User fakeCreatedBy =new lithium.service.casino.cms.storage.entities.User();
        lithium.service.casino.cms.storage.entities.User fakeModifiedBy =new lithium.service.casino.cms.storage.entities.User();

        fakeCreatedBy.setGuid(LIVESCORE_USER_1);
        fakeModifiedBy.setGuid(LIVESCORE_USER_2);
        fakeLobbyRevision.setCreatedBy(fakeCreatedBy);
        fakeLobbyRevision.setModifiedBy(fakeModifiedBy);

        service.setLobbyRevisionUserData(serviceUserData, page.getContent());
        LobbyRevision lobbyRevision =page.getContent().stream().findFirst().get();

        assertEquals ("First LastName",lobbyRevision.getCreatedBy().getFullName());
        assertEquals ("Modifier LastModifier",lobbyRevision.getModifiedBy().getFullName());
    }
}
