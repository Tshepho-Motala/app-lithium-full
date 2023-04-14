package lithium.service.cashier.provider.mercadonet.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DoubleAdaptor extends XmlAdapter<String, Double> {

	@Override
	public Double unmarshal(String v) throws Exception {
		if (v.indexOf(",") == -1) {
			return new Double(v);
		} else {
			return new Double(v.replaceAll(",", ""));
		}
	}

	@Override
	public String marshal(Double v) throws Exception {
		return v.toString();
	}
	
}
