package lithium.service;

public class Counter {

	long count = 0L;
	
	synchronized public void increment() {
		count++;
	}
	
	public long getValue() { return count; };
	
	public String toString() {
		return new Long(count).toString();
	}
	
}
