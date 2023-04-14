package lithium.service.cashier.processor.interswitch.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum PayDirectChannel {
	BANK_BRANC("Bank Branc", "paydirect"),
	ATM("ATM", "quickteller"),
	WEB("WEB", "quickteller"),
	MOBILE("Mobile", "quickteller");
	@Getter
	private final String channelName;
	@Getter
	private final String processorCode;

	public static String findByValue(String val){
		for(PayDirectChannel channel : values()){
			if (channel.getChannelName().equalsIgnoreCase(val)) {
				return channel.getProcessorCode();
			}
		}
		return null;
	}
}
