package lithium.service.cashier.provider.mercadonet.util;

import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;

public class DateTimeAdapter extends XmlAdapter<String, DateTime> {
	public DateTime unmarshal(String source) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return new DateTime(sdf.parse(source).getTime());
	}
	public String marshal(DateTime v) throws Exception {
		String marshalled = v.toString("yyyy/MM/dd");
		return marshalled;
	}
}