package lithium.service.casino.provider.nucleus.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class GameInternal {
	@XmlAttribute(name="ID")
	private String id;
	@XmlAttribute(name="NAME")
	private String name;
	@XmlAttribute(name="IMAGEURL")
	private String imageUrl;
	@XmlAttribute(name="LANGUAGES")
	private String languages;
	
}
