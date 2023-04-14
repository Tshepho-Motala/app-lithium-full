package lithium.service.user.services;

import lithium.client.changelog.ChangeLogService;
import lithium.service.UserGuidStrategy;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.user.ServiceUserApplication;
import lithium.service.user.data.entities.User;
import lithium.service.user.client.objects.PasswordBasic;
import lithium.service.user.data.repositories.UserRepository;
import lithium.service.user.services.notify.PasswordChangeNotificationService;
import lithium.util.PasswordHashing;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class UserServiceTest {
	private UserService userService = new UserService();
	private UserLinkService userLinkService = mock(UserLinkService.class);
	private UserRepository userRepository = mock(UserRepository.class);
	private ChangeLogService changeLogService = mock(ChangeLogService.class);
	private PasswordChangeNotificationService passwordChangeNotificationService = mock(PasswordChangeNotificationService.class);
	private LimitInternalSystemService limitService = mock(LimitInternalSystemService.class);
  private UserPasswordHashAlgorithmService userPasswordHashAlgorithmService = mock(UserPasswordHashAlgorithmService.class);

	@Before
	public void setUp() throws Exception {
		ServiceUserApplication.GUID_STRATEGY = UserGuidStrategy.ID;
		userService.setUserLinkService(userLinkService);
		userService.setChangeLogService(changeLogService);
		userService.setUserRepository(userRepository);
		userService.setPasswordChangeNotificationService(passwordChangeNotificationService);
		userService.setLimitService(limitService);
		userService.setPasswordSalt("passwordSalt");
    userService.setUserPasswordHashAlgorithmService(userPasswordHashAlgorithmService);

		final AtomicReference<User> user = new AtomicReference<>();

		when(userRepository.findOne(anyLong())).then(invocation -> {
			log.info(invocation.toString());
			return user.get();
		});
		when(userRepository.save(any(User.class))).then(invocation -> {
			log.info(invocation.toString());
			user.set((User) invocation.getArguments()[0]);

			return user.get();
		});

		when(changeLogService.copy(any(), any(), any())).then(invocation -> {
			log.info(invocation.toString());
			return null;
		});
	}

	@Test
	public void testChangePasswordHappyPath() throws Exception {
		User u = userService.save(User.builder().id(3L).guid("ggmvsoc/3").build());

		userService.changePassword("default/1", u.guid(), "NewPassword12", null);
		assertEquals("Password not updated..", u.getPasswordHash(), PasswordHashing.hashPassword("NewPassword12", "passwordSalt"));
		userService.changePassword(
			u.guid(),
			PasswordBasic.builder().newPassword("password123").confirmPassword("password123").currentPassword("NewPassword12").build(),
  null
		);
		assertEquals("Password not updated..", u.getPasswordHash(), PasswordHashing.hashPassword("password123", "passwordSalt"));
	}

	@Test
	public void testChangePasswordHappyPathNoSalt() throws Exception {
		userService.setPasswordSalt(null);
		User u = userService.save(User.builder().id(3L).guid("ggmvsoc/3").build());

		userService.changePassword("default/1", u.guid(), "NewPassword12", null);
		assertEquals("Password not updated..", u.getPasswordHash(), PasswordHashing.hashPassword("NewPassword12", null));
		userService.changePassword(
				u.guid(),
				PasswordBasic.builder().newPassword("password123").confirmPassword("password123").currentPassword("NewPassword12").build(),
    null
		);
		assertEquals("Password not updated..", u.getPasswordHash(), PasswordHashing.hashPassword("password123", null));
	}

	@Test(expected=Status490SoftSelfExclusionException.class)
	public void testSoftExclusion() throws Exception {
		Answer<Void> answer = invocation -> {
			throw new Status490SoftSelfExclusionException("You have excluded yourself from playing until " + new DateTime().plusDays(1));
		};
		doAnswer(answer).when(limitService).checkPlayerRestrictions(anyString(), anyString());

		User u = userService.save(User.builder().id(3L).guid("ggmvsoc/3").build());

		userService.changePassword(
			u.guid(),
			PasswordBasic.builder().newPassword("password123").confirmPassword("password123").currentPassword("NewPassword12").build(),
  null
		);
	}

	@Test(expected=Status491PermanentSelfExclusionException.class)
	public void testPermanentExclusion() throws Exception {
		Answer<Void> answer = invocation -> {
			throw new Status491PermanentSelfExclusionException("You have excluded yourself permanently from playing.");
		};
		doAnswer(answer).when(limitService).checkPlayerRestrictions(anyString(), anyString());

		User u = userService.save(User.builder().id(3L).guid("ggmvsoc/3").build());

		userService.changePassword(
			u.guid(),
			PasswordBasic.builder().newPassword("password123").confirmPassword("password123").currentPassword("NewPassword12").build(),
  null
		);
	}
}
