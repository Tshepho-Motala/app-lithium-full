package lithium.service.user.provider.vipps.service;

import java.security.Key;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.user.provider.vipps.domain.Address;
import lithium.service.user.provider.vipps.domain.AuthAttempt;
import lithium.service.user.provider.vipps.domain.UserDetails;
import lithium.service.user.provider.vipps.domain.CallbackRequest.Status;
import lithium.service.user.provider.vipps.repository.AuthAttemptRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthAttemptService {
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	
	@Autowired
	private AuthAttemptRepository authAttemptRepository;
	
	private String generate() {
		return UUID.randomUUID().toString();
	}
	
	public AuthAttempt generateAndSave() {
		String xRequestId = generate();
		log.info("Generated xRequestId : "+xRequestId);
		AuthAttempt aa = authAttemptRepository.findByXRequestId(xRequestId);
		if (aa == null) {
			aa = AuthAttempt.builder().xRequestId(xRequestId).created(DateTime.now()).build();
			aa = authAttemptRepository.save(aa);
		} else {
			aa = generateAndSave();
		}
		return aa;
	}
	
	public AuthAttempt saveAuthAttempt(AuthAttempt authAttempt) {
		return authAttemptRepository.save(authAttempt);
	}
	
	public AuthAttempt findAuthAttemptByXRequestId(String xRequestId) {
		return authAttemptRepository.findByXRequestId(xRequestId);
	}
	public AuthAttempt findAuthAttemptByRequestId(String requestId) {
		return authAttemptRepository.findByCallbackRequestRequestId(requestId);
	}
	
	private String obfuscate(String data) {
		return "deleted-"+data+"-deleted";
	}
	
	public Address obfuscateAddress(Address address) {
		if (address.getAddressLine1()!=null) address.setAddressLine1(obfuscate(address.getAddressLine1()));
		if (address.getAddressLine2()!=null) address.setAddressLine2(obfuscate(address.getAddressLine2()));
		if (address.getCity()!=null) address.setCity(obfuscate(address.getCity()));
		if (address.getCountry()!=null) address.setCountry(obfuscate(address.getCountry()));
		return address;
	}
	
	public UserDetails obfuscateUserDetails(UserDetails userDetails) {
//		if (userDetails.getAddress()!=null) userDetails.setAddress(obfuscateAddress(userDetails.getAddress()));
		if (userDetails.getEmail()!=null) userDetails.setEmail(obfuscate(userDetails.getEmail()));
		if (userDetails.getMobileNumber()!=null) userDetails.setMobileNumber(obfuscate(userDetails.getMobileNumber()));
		return userDetails;
	}
	
	public AuthAttempt obfuscateAndSaveAuthAttempt(AuthAttempt authAttempt) {
		authAttempt.getCallbackRequest().setStatus(Status.REMOVED);
		authAttempt.getCallbackRequest().setUserDetails(obfuscateUserDetails(authAttempt.getCallbackRequest().getUserDetails()));
		return saveAuthAttempt(authAttempt);
	}
	
	public String passwordGen(String rid) {
		try {
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			
			Key key = new SecretKeySpec(rid.getBytes(), HMAC_SHA1_ALGORITHM);
			mac.init(key);
			byte[] output = mac.doFinal(rid.getBytes());
			
			StringBuffer sb = new StringBuffer();
			for (byte b : output) {
				sb.append(String.format("%02x", b & 0xff));
			}
			
			return sb.toString();
		} catch (Exception e) {
			log.error("Password Gen error : "+e.getMessage(), e);
			return rid;
		}
	}
}
