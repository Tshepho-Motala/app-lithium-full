package lithium.service.mail.provider.smtp;

import java.util.Properties;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lithium.service.mail.client.internal.DoProviderRequest;
import lithium.service.mail.client.internal.DoProviderResponse;
import lithium.service.mail.client.internal.DoProviderResponseStatus;
import lithium.service.mail.provider.DoProviderInterface;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DoProvider implements DoProviderInterface {

    private static final String NO_FROM_REPLACE = "noFromReplace";

	@Autowired JavaMailSenderImpl javaMailSenderImpl;
	
	@Value("${spring.mail.host}") String defaultMailHost;
	@Value("${spring.mail.port}") Integer defaultMailPort;
	@Value("${spring.mail.username}") String defaultMailUsername;
	@Value("${spring.mail.password}") String defaultMailPassword;
	@Value("${spring.mail.properties.mail.smtp.auth}") Boolean defaultMailAuth;
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}") Boolean defaultMailTls;
	
	private boolean validEmailAddress(String email) {
		boolean valid = true;
		try {
			InternetAddress address = new InternetAddress(email);
			address.validate();
		} catch (AddressException ex) {
			valid = false;
		}
		return valid;
	}
	
	@Override
	public DoProviderResponse send(DoProviderRequest request) throws Exception {
		String host = this.defaultMailHost;
		Integer port = this.defaultMailPort;
		String username = this.defaultMailUsername;
		String password = this.defaultMailPassword;
		Boolean auth = this.defaultMailAuth;
		Boolean tls = this.defaultMailTls;
		String from = request.getFrom();
		String bcc = null;
		
		if (request.getProperties() != null && request.getProperties().size() > 0) {
			String hostFromProps = request.getProperty("host");
			if (!hostFromProps.isEmpty()) host = hostFromProps;
			Integer portFromProps = (request.getProperty("port") != null && !request.getProperty("port").isEmpty())
				? Integer.parseInt(request.getProperty("port")): null;
			if (portFromProps != null) port = portFromProps;
			String usernameFromProps = request.getProperty("username");
			if (!usernameFromProps.isEmpty()) username = usernameFromProps;
			String passwordFromProps = request.getProperty("password");
			if (!passwordFromProps.isEmpty()) password = passwordFromProps;
			Boolean authFromProps = (request.getProperty("auth") != null && !request.getProperty("auth").isEmpty())
				? Boolean.valueOf(request.getProperty("auth")): null;
			if (authFromProps != null) auth = authFromProps;
			Boolean tlsFromProps = (request.getProperty("tls") != null && !request.getProperty("tls").isEmpty())
				? Boolean.valueOf(request.getProperty("tls")): null;
			if (tlsFromProps != null) tls = tlsFromProps;

			String noReplace = request.getProperties().get(NO_FROM_REPLACE);

            if (noReplace==null || !Boolean.valueOf(noReplace)) {
                String fromFromProps = request.getProperty("from");
                if (!fromFromProps.isEmpty() && validEmailAddress(fromFromProps)) from = fromFromProps;
            }

			String bccFromProps = request.getProperty("bcc");
			if (!bccFromProps.isEmpty() && validEmailAddress(bccFromProps)) bcc = bccFromProps;
		}
		
		DoProviderResponseStatus status = DoProviderResponseStatus.FAILED;
		String message = null;
		
		try {
			javaMailSenderImpl.setProtocol("smtp");
			javaMailSenderImpl.setHost(host);
			javaMailSenderImpl.setPort(port);
			javaMailSenderImpl.setUsername(username);
			javaMailSenderImpl.setPassword(password);
			Properties props = javaMailSenderImpl.getJavaMailProperties();
			props.put("mail.smtp.auth", auth);
			props.put("mail.smtp.starttls.enable", tls);
			props.put("mail.smtp.starttls.required", tls);
			props.put("mail.debug", log.isDebugEnabled());
			
			MimeMessage mimeMessage = javaMailSenderImpl.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			mimeMessageHelper.setFrom(from);
			mimeMessageHelper.setTo(request.getTo());
			if (bcc != null) mimeMessageHelper.setBcc(bcc);
			mimeMessageHelper.setSubject(request.getSubject());
			mimeMessageHelper.setText(request.getBody(), true);
			if (request.getAttachmentName() != null &&
				!request.getAttachmentName().isEmpty() &&
				request.getAttachmentData() != null) {
					mimeMessageHelper.addAttachment(request.getAttachmentName(),
						new ByteArrayResource(request.getAttachmentData()));
			}
			
			javaMailSenderImpl.send(mimeMessage);
			
			log.info("Mail sent to " + request.getTo() + " from " + from + " via " + javaMailSenderImpl.getHost()
				+ " with subject " + request.getSubject());
			
			status = DoProviderResponseStatus.SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			status = DoProviderResponseStatus.FAILED;
			message = ExceptionUtils.getStackTrace(e);
		}
		
		DoProviderResponse response = DoProviderResponse.builder()
		.mailId(request.getMailId())
		.from(from)
		.bcc(bcc)
		.status(status)
		.message(message)
		.build();
		
		return response;
	}
}