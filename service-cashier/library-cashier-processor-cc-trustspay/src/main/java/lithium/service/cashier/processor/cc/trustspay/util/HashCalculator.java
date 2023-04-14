package lithium.service.cashier.processor.cc.trustspay.util;

import java.security.MessageDigest;

public class HashCalculator {
	private StringBuffer items = new StringBuffer();
	private String password;
	
	public HashCalculator(String password) {
		this.password = password;
	}
	
	public HashCalculator addItem(String item) {
		items.append(item);
		return this;
	}
		
	public String calculateHash() {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update((items.toString() + password.toString()).getBytes());
			
			StringBuffer sb = new StringBuffer();
			for (byte b : md.digest()) {
				sb.append(String.format("%02x", b & 0xff));
			}
			
			return sb.toString().toUpperCase();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
