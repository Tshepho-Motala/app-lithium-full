package lithium.service.access.provider.transunion.shema.response.success;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SearchResponse",
        namespace = "http://www.callcredit.co.uk/SingleAccessPointService/ISingleAccessPointService/1.0")
@Data
public class TransUnionResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SearchResult",
            namespace = "http://www.callcredit.co.uk/SingleAccessPointService/ISingleAccessPointService/1.0")
    private SearchResultBody searchResultBody;

}
