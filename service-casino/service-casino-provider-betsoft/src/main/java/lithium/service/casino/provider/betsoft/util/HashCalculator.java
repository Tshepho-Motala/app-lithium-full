package lithium.service.casino.provider.betsoft.util;

import java.security.MessageDigest;
import java.util.Vector;

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
			
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
