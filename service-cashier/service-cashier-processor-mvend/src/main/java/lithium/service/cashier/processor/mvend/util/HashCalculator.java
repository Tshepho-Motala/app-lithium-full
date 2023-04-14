package lithium.service.cashier.processor.mvend.util;

import java.security.MessageDigest;
import java.util.Vector;

public class HashCalculator {
	private Vector<String> items;
	private String delimiter;

	public HashCalculator(String delimeter) {
		this.delimiter = delimeter;
		items = new Vector<String>();
	}
	
	public HashCalculator addItem(String item) {
		if (item != null) items.add(item);
		return this;
	}
	
	public HashCalculator addItem(Boolean item) {
		if (item != null) items.add(item.toString());
		return this;
	}
	
	public HashCalculator addItem(Integer item) {
		if (item != null) items.add(item.toString());
		return this;
	}

	public HashCalculator addItem(Long item) {
		if (item != null) items.add(item.toString());
		return this;
	}	
	
	public HashCalculator addItem(int item) {
		items.add(new Integer(item).toString());
		return this;
	}

	public String getPayload() {
		String allItems = "";
		for (String item: items) {
			if (allItems.length() > 0) allItems += delimiter;
			allItems += item;
		}
		return allItems;
	}

	public String calculateHash() {

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(getPayload().getBytes());
			
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}
			
			return sb.toString().toUpperCase();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
