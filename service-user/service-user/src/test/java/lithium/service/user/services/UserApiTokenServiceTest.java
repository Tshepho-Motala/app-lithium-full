package lithium.service.user.services;

import lithium.service.user.config.ServiceUserConfigurationProperties;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserApiToken;
import lithium.service.user.data.repositories.UserApiTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Slf4j
public class UserApiTokenServiceTest {

    static final String GUID = "domain/1";

    UserApiTokenService service = new UserApiTokenService();
    UserApiTokenRepository userApiTokenRepo = Mockito.mock(UserApiTokenRepository.class);
    ServiceUserConfigurationProperties properties = Mockito.mock(ServiceUserConfigurationProperties.class);
    UserService userService = Mockito.mock(UserService.class);

    @Before
    public void setup() {
        service.setUserApiTokenRepo(userApiTokenRepo);
        service.setProperties(properties);
        service.setUserService(userService);
        when(userApiTokenRepo.save(any(UserApiToken.class))).then(AdditionalAnswers.returnsFirstArg());
    }

    @Test
    public void testJohanVanDenBerg() {
        User user = User.builder().firstName("Johan").lastName("van den Berg").build();
        when(userService.findFromGuid(GUID)).thenReturn(user);
        assertEquals("Short guid should be", "JOHAN", service.findOrGenerateShortGuid(GUID));
    }

    @Test
    public void testJohanVanDenBergAlreadyHasAToken() {
        User user = User.builder().firstName("Johan").lastName("van den Berg").build();
        UserApiToken token = UserApiToken.builder().token("EXPECTEDTOKEN").guid(GUID).shortGuid("SHORTGUID").build();
        when(userApiTokenRepo.findByGuid(GUID)).thenReturn(token);
        when(userService.findFromGuid(GUID)).thenReturn(user);
        assertEquals("Short guid should be", "SHORTGUID", service.findOrGenerateShortGuid(GUID));
    }

    @Test
    public void testJohanVanDenBergClash4Times() {
        testClash4Times("Johan", "van den Berg");
    }

    @Test
    public void testJohanVanDenBergWithSpaces() {
        testClash4Times("Johan", "van den Berg ");
        testClash4Times("Johan", "van den Berg  ");
        testClash4Times("Johan", " van den Berg");
        testClash4Times("Johan", "  van den Berg");
    }

    @Test
    public void testClash12Times() {
        User user = User.builder().firstName("Johan").lastName("van den Berg").build();
        when(userService.findFromGuid(GUID)).thenReturn(user);
        AtomicInteger count = new AtomicInteger();
        when(userApiTokenRepo.findByShortGuid(any())).then(invocation -> {
            count.getAndIncrement();
            return new UserApiToken();
        });
        assertEquals("Short guid length is", 14, service.findOrGenerateShortGuid(GUID).length());
        assertEquals("Attempts is", 15, count.get());
    }

    private void testClash4Times(String firstName, String lastName) {
        User user = User.builder().firstName(firstName).lastName(lastName).build();
        when(userService.findFromGuid(GUID)).thenReturn(user);
        when(userApiTokenRepo.findByShortGuid("JOHAN")).thenReturn(new UserApiToken());
        when(userApiTokenRepo.findByShortGuid("JOHANVDB")).thenReturn(new UserApiToken());
        when(userApiTokenRepo.findByShortGuid("JVDB")).thenReturn(new UserApiToken());
        when(userApiTokenRepo.findByShortGuid("JBERG")).thenReturn(new UserApiToken());
        assertEquals("Short guid should be", "JOHAN", service.findOrGenerateShortGuid(GUID).substring(0, 5));
    }


}