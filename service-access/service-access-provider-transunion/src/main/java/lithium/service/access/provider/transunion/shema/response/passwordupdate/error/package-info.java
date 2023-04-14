@XmlSchema(
        namespace = "http://www.callcredit.co.uk/SingleAccessPointService/ISingleAccessPointAdminService/1.0",
        elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {
                @XmlNs(prefix="a", namespaceURI="http://www.callcredit.co.uk/Common/Base/Error/1.0")
        })
package lithium.service.access.provider.transunion.shema.response.passwordupdate.error;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;