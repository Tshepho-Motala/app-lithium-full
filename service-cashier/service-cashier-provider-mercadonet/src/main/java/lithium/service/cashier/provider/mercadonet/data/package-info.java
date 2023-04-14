@XmlJavaTypeAdapters({
	@XmlJavaTypeAdapter(
		type=DateTime.class, 
		value=DateTimeAdapter.class
	),
	@XmlJavaTypeAdapter(
		type=Double.class,
		value=DoubleAdaptor.class
	)
})
package lithium.service.cashier.provider.mercadonet.data;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.joda.time.DateTime;

import lithium.service.cashier.provider.mercadonet.util.DateTimeAdapter;
import lithium.service.cashier.provider.mercadonet.util.DoubleAdaptor;
