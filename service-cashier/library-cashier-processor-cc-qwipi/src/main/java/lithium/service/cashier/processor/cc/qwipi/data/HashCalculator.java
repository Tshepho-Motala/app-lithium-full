package lithium.service.cashier.processor.cc.qwipi.data;

import java.security.MessageDigest;
import java.util.Vector;

public class HashCalculator {
	private Vector<String> items;
	private String password;
	
	public HashCalculator(String password) {
		items = new Vector<String>();
		this.password = password;
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

	public String calculateHash() {
		String allItems = "";
		for (String item: items) {
			allItems += item;
		}
		
		allItems += password;
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(allItems.getBytes());
			
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
