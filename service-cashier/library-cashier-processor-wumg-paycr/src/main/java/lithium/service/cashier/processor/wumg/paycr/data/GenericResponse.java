package lithium.service.cashier.processor.wumg.paycr.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@XmlRootElement(name="string")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
@ToString
@XmlType(namespace="http://tempuri.org/")
public class GenericResponse {
	
	@XmlElement
	private WebMethodResponse WebMethodResponse;

}
