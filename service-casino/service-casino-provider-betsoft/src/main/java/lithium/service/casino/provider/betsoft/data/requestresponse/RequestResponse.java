package lithium.service.casino.provider.betsoft.data.requestresponse;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lithium.service.casino.provider.betsoft.util.DateXmlAdapter;
import lombok.ToString;

@ToString
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class RequestResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "TIME")
	@XmlJavaTypeAdapter(value = DateXmlAdapter.class)
	private Date time = new Date();
	
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}