package lithium.service.cashier.provider.mercadonet.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlRootElement(name="Mnet")
@XmlAccessorType(XmlAccessType.FIELD)
public class MnetRequest {
	public static final String TYPE_DEPOSIT = "Deposit";
	public static final String TYPE_PAYOUT = "Payout";
	
	@XmlElement(name="Request")
	private MnetRequestData mnetRequestData;
}