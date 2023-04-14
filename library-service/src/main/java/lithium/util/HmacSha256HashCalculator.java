package lithium.util;

import lithium.exceptions.Status470HashInvalidException;
import org.slf4j.Logger;

import java.util.Vector;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacSha256HashCalculator {
	private Vector<String> items;
	private String password;

	public HmacSha256HashCalculator(String password) {
		items = new Vector<String>();
		this.password = password;
	}

	public void addItem(String item) {
		if (item != null) items.add(item);
	}

	public void addItem(long item) {
		items.add(new Long(item).toString());
	}

	public void addItem(double item) {
		items.add(new Double(item).toString());
	}

	public void addItem(Boolean item) {
		if (item != null) items.add(item.toString());
	}

	public void addItem(Integer item) {
		if (item != null) items.add(item.toString());
	}

	public void addItem(Long item) {
		if (item != null) items.add(item.toString());
	}	

	public void addItem(int item) {
		items.add(new Integer(item).toString());
	}

	public String getUnencryptedPayload() {
		String allItems = "";
		for (String item: items) {
			allItems += item;
			allItems += ":";
		}
		allItems = allItems.substring(0, allItems.length()-1);
		return allItems;
	}

	public String calculateHash() {
		//TODO: check if this thing spits out expected hash
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(password.getBytes(),
					"HmacSHA256");
			mac.init(secretKey);
			byte[] macData = mac.doFinal(getUnencryptedPayload().getBytes());
			String result = "";
			for (final byte element : macData) {
				result += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void validate(String suppliedHash, Logger log, Object context) throws Status470HashInvalidException {
		String expectedHash = calculateHash();
		if (!expectedHash.equals(suppliedHash)) {
			log.warn("Expected " + expectedHash + " sha256 but got " + suppliedHash + " " + context.toString());
			log.warn("Hash calculated using " + getUnencryptedPayload() + " and key " + password);
			throw new Status470HashInvalidException();
		}

	}
}
