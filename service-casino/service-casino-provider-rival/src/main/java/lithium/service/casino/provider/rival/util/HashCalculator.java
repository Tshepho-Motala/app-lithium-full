package lithium.service.casino.provider.rival.util;

import java.util.Vector;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HashCalculator {
	private Vector<String> items;
	private String password;

	public HashCalculator(String password) {
		items = new Vector<String>();
		this.password = password;
	}

	public void addItem(String item) {
		if (item != null) items.add(item);
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

	public String calculateHash() {
		String allItems = "";
		for (String item: items) {
			allItems += item;
			allItems += "&";
		}

		allItems = allItems.substring(0, allItems.length()-1);
		//TODO: check if this thing spits out expected hash
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(password.getBytes(),
					"HmacSHA256");
			mac.init(secretKey);
			byte[] macData = mac.doFinal(allItems.getBytes());
			String result = "";
			for (final byte element : macData) {
				result += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
