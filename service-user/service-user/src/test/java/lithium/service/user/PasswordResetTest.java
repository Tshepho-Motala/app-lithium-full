package lithium.service.user;

import lithium.client.changelog.ChangeLogService;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.sms.client.stream.SMSStream;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserPasswordToken;
import lithium.service.user.data.repositories.UserPasswordTokenRepository;
import lithium.service.user.exceptions.Status100InvalidInputDataException;
import lithium.service.user.exceptions.Status422InvalidDateOfBirthException;
import lithium.service.user.exceptions.Status999GeneralFailureException;
import lithium.service.user.services.PasswordResetService;
import lithium.service.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.MessageSource;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class PasswordResetTest {
	private PasswordResetService passwordResetService = new PasswordResetService();
	private ChangeLogService changeLogService = mock(ChangeLogService.class);
	private MessageSource messageSource = mock(MessageSource.class);
	private CachingDomainClientService cachingDomainClientService = mock(CachingDomainClientService.class);
	private UserService userService = mock(UserService.class);
	private MailStream mailStream = mock(MailStream.class);
	private SMSStream smsStream = mock(SMSStream.class);

	private UserPasswordTokenRepository userPasswordTokenRepository = mock(UserPasswordTokenRepository.class);

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Before
	public void setUp() {
		passwordResetService.setMessageSource(messageSource);
		passwordResetService.setChangeLogService(changeLogService);
		passwordResetService.setCachingDomainClientService(cachingDomainClientService);
		passwordResetService.setMailStream(mailStream);
		passwordResetService.setSmsStream(smsStream);
		passwordResetService.setUserService(userService);
	}

	@Test
	public void testPasswordResetStep1() throws Status999GeneralFailureException, Status100InvalidInputDataException, Status422InvalidDateOfBirthException {
		log.warn("THIS TEST IS HALF WAY COMPLETED, NEEDS ATTENTION!");
		final AtomicReference<UserPasswordToken> exclusion = new AtomicReference<>();

		when(userPasswordTokenRepository.save(any(UserPasswordToken.class))).then(invocation -> {
			log.info(invocation.toString());
			exclusion.set((UserPasswordToken) invocation.getArguments()[0]);

			return exclusion.get();
		});

		when(userPasswordTokenRepository.findByUser(any())).then(invocation -> {
			log.info(invocation.toString());
			return exclusion.get();
		});

		when(userPasswordTokenRepository.findByUserAndToken(any(User.class), anyString())).then(invocation -> {
			log.info(invocation.toString());
			return exclusion.get();
		});

		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				log.info("deleteByUser called with arguments: " + Arrays.toString(args));
				exclusion.set(null);
				return null;
			}
		}).when(userPasswordTokenRepository).deleteByUser(any(User.class));

		Date dob = new Date(1982, 6, 16);

//		exceptionRule.expect(Status100InvalidInputDataException.class);
//		exceptionRule.expectMessage("Invalid Input Data Provided");
//		passwordResetService.step1("ggmvsoc", "", "", "", UserValidationBaseService.Type.EMAIL, UserValidationBaseService.TokenType.NUMERIC, 10, dob);
	}

	@Test
	public void testDetection() {
		try {
//			assertEquals(CardType.VISA, CardType.detect("4000056655665556"));
			String generateEmailToken = generateEmailToken();
			String generateNumericEmailToken = generateNumericEmailToken();
			String generateMobileToken = generateMobileToken();

			log.info("generateEmailToken : "+generateEmailToken);
			log.info("generateNumericEmailToken : "+generateNumericEmailToken);
			log.info("generateMobileToken : "+generateMobileToken);

			log.info("encoded ::: ");
			log.info("generateEmailToken : "+encode(generateEmailToken));
			log.info("generateNumericEmailToken : "+encode(generateNumericEmailToken));
			log.info("generateMobileToken : "+encode(generateMobileToken));

			log.info("decoded ::: ");
			log.info("generateEmailToken : "+decode(encode(generateEmailToken)));
			log.info("generateEmailToken : "+decode(generateEmailToken));
			log.info("generateNumericEmailToken : "+decode(encode(generateNumericEmailToken)));
			log.info("generateNumericEmailToken : "+decode(generateNumericEmailToken));
			log.info("generateMobileToken : "+decode(encode(generateMobileToken)));

		} catch (AssertionError | UnsupportedEncodingException | NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
		}
	}

	protected String generateEmailToken() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String token = RandomStringUtils.random(5, "0123456789ABCDEFX");
		byte[] bytes = token.getBytes("UTF-8");
		return Base64.getEncoder().encodeToString(bytes);
	}
	protected String generateNumericEmailToken() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String token = RandomStringUtils.random(5, "0123456789");
		byte[] bytes = token.getBytes("UTF-8");
		return Base64.getEncoder().encodeToString(bytes);
	}

	protected String generateMobileToken() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String token = RandomStringUtils.random(5, "0123456789");
		return token;
	}

	private String encode(String token) throws UnsupportedEncodingException {
		byte[] newToken = token.getBytes();
		String encoded = new String(java.util.Base64.getEncoder().encode(newToken), "UTF-8");
		return encoded;
	}

	private String decode(String token) throws UnsupportedEncodingException {
		return new String(java.util.Base64.getDecoder().decode(token), "UTF-8");
	}
}
