package lithium.service.affiliate.util;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import static lithium.service.affiliate.ServiceAffiliatePrvIncomeAccessModuleInfo.ConfigProperties;
import lithium.util.SecurityKeyPairGenerator;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyPair;
import java.util.Map;



@Slf4j
public class SFTPSender {

	private Map<String, String> providerConfigProperties;
	private JSch jsch = null;
	private Session session = null;

	public SFTPSender(Map<String, String> mProviderConfigProperties) {
		providerConfigProperties = mProviderConfigProperties;
		jsch = new JSch();
		try {
			initIdentity(jsch,
						 providerConfigProperties.get(ConfigProperties.PRIVATE_KEY.getValue()),
						 providerConfigProperties.get(ConfigProperties.PUBLIC_KEY.getValue()));

			session = initSession(jsch,
					providerConfigProperties.get(ConfigProperties.SFTP_HOST_NAME.getValue()),
					providerConfigProperties.get(ConfigProperties.SFTP_HOST_PORT.getValue()),
					providerConfigProperties.get(ConfigProperties.SFTP_USERNAME.getValue()),
					providerConfigProperties.get(ConfigProperties.SFTP_PASSWORD.getValue()));
		} catch (Exception e) {
			log.error("Unable to init sftp session or identity", e);
		}
	}

	public boolean sendRegistrationFile(byte[] fileData, final String fileName) {
		try {
			performSendingSequence(providerConfigProperties.get(ConfigProperties.REGISTRATION_FILE_URL.getValue()), fileName, fileData);
		} catch (Exception e) {
			log.error("Unable to perform sftp put to remote host for registration file", e);
			return false;
		}
		return true;
	}

	public boolean sendSalesFile(byte[] fileData, final String fileName) {
		try {
			performSendingSequence(providerConfigProperties.get(ConfigProperties.SALES_FILE_URL.getValue()), fileName, fileData);
		} catch (Exception e) {
			log.error("Unable to perform sftp put to remote host for sales file", e);
			return false;
		}
		return true;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		closeSession();
	}

	public void closeSession() {
		if (session != null) {
			session.disconnect();
			session = null;
		}
	}

	private void performSendingSequence(final String url, final String filename, byte[] fileData) throws Exception {
		//JSch.setLogger(new JSCHLogger());
		ChannelSftp channel = initChannel(session);
		channelCd(channel, url);
		channelPut(channel, new ByteArrayInputStream(fileData), filename + ".csv");
//		try {
//			channel.chmod(Integer.parseInt("777", 8), url + "/" + filename + ".csv");
//		} catch (Exception ex) {
//			log.warn("Unable to change permissions for " + url + "/" + filename + ".csv | " + ex.getMessage());
//		}
		channel.exit();
	}

	/**
	 * Load idenity to be used for sftp send from provider config properties
	 *
	 * @param jsch
	 * @throws Exception
	 */
	private void initIdentity(JSch jsch, final String privateKey, final String publicKey) throws Exception {
		SecurityKeyPairGenerator spg = new SecurityKeyPairGenerator();
		// KeyPair keyPair = spg.generateKeyPair();
		// byte[] privateKeyBytes = spg.printPrivateKeyInPemFormat(keyPair).getBytes();//(new String()).getBytes(); // TODO: Read key string from provider propeties
		// byte[] publicKeyBytes = spg.printPublicKeyInPemFormat(keyPair).getBytes();//(new String()).getBytes(); // TODO: Read key string from provider propeties

		byte[] privateKeyBytes = privateKey.getBytes();
		byte[] publicKeyBytes = publicKey.getBytes();

		String idName = "ksftp";
		byte[] passphrase = null;
		jsch.addIdentity(idName, privateKeyBytes, publicKeyBytes, passphrase);
	}

	/**
	 * Initialize session using config properties
	 * @param jsch
	 * @return
	 */
	private Session initSession(JSch jsch, final String sshServer, final String sshPort, final String username, final String password) throws Exception {
		//String username = "root"; // TODO: Read value from provider properties
		//String sshServer = "master.cloud.playsafesa.com"; // TODO: Read value from provider properties
		//Integer sshPort = 22; // TODO: Read value from provider properties
		Session session = jsch.getSession(username, sshServer, Integer.parseInt(sshPort));
		//Enabling strict checking would require some extra code and maintenance, but would increase security.
		session.setConfig("StrictHostKeyChecking", "no");
		session.setTimeout(15000); // TODO: Consider adding this to config params?
		session.setPassword(password); // incase there is a password required
		return session;
	}

	/**
	 *
	 * @param session
	 * @return
	 */
	private ChannelSftp initChannel(Session session) throws Exception {
		ChannelSftp channel = null;

		if (!session.isConnected()) {
			session.connect();
		}
		channel = (ChannelSftp) session.openChannel("sftp");
		channel.connect();
		return channel;
	}

	private ChannelSftp channelCd(ChannelSftp channel, String dirPath) throws Exception {
		try {
			channel.mkdir(dirPath);
		} catch (SftpException e) {
			//This error is ok, by convention it is better to just catch the error and continue
			if (!e.getMessage().contentEquals("The file already exists.")) {
				throw e;
			}
		}
		channel.cd(dirPath);
		return channel;
	}

	private ChannelSftp channelPut(ChannelSftp channel, InputStream localStream, String remoteFileName) throws Exception {
		channel.put(localStream, remoteFileName, ChannelSftp.OVERWRITE);
		return channel;
	}

//	public static void main(String[] args) {
//		SFTPSender sender = new SFTPSender();
//		try {
//			sender.init();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
