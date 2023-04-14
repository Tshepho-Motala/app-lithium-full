package lithium.service.casino.provider.betsoft.data;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class SuiteInternal {
	@XmlAttribute(name="ID")
	private String id;
	@XmlAttribute(name="NAME")
	private String name;
	@XmlElementWrapper(name="GAMES")
	@XmlElements({@XmlElement(name="GAME")})
	private List<GameInternal> gameList;
}
