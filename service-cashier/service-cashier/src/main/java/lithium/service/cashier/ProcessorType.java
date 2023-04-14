package lithium.service.cashier;

public enum ProcessorType {
	DEPOSIT ("deposit"),
	WITHDRAW ("withdraw");
	
	String value;
	
	ProcessorType(String typeValue) {
		value = typeValue;
	}
	
	public String toString() {
		return value;
	}
	
	public static ProcessorType fromValue(String value) {
		for (ProcessorType pt:ProcessorType.values()) {
			if (pt.value.equalsIgnoreCase(value)) {
				return pt;
			}
		}
		return null;
	}
}