package lithium.service.casino.provider.betsoft.data;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.ToString;



@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="GAMESSUITES")
public class GamesSuitesInternal {
	@XmlElementWrapper(name="SUITES")
	@XmlElement(name="SUITE")
	private List<SuiteInternal> suiteList;
	//@XmlElements({@XmlElement(name="SUITE")})
	//List<Suite> suiteList;
}
	