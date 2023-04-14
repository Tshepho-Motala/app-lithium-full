package lithium.service.cashier.provider.mercadonet.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlRootElement(name="Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class MnetResponse {
	public static final String STATUS_OK = "OK";
	public static final String STATUS_FAIL = "FAIL";
	
	@XmlElement(name="CustomerInfo")
	private MnetResponseData mnetResponseData;
	@XmlElement(name="Status")
	private String status;
	@XmlElement(name="TransID")
	private Integer transactionId;
	@XmlElement(name="Error")
	private String error;
	@XmlElement(name="Balance")
	private String balance;
	@XmlElement(name="Bonus")
	private String bonus;
	@XmlElement(name="Terms")
	private String terms;
}