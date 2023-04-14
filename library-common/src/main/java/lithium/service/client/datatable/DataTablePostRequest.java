package lithium.service.client.datatable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DataTablePostRequest extends DataTableRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, String[]> requestData = null;

    public String requestData(String key) {
        String[] values = requestDataArray(key);
        if ((values == null) || (values.length == 0)) return null;
        return values[0];
    }
    public Date requestDataDate(String key) {
        String[] date = requestDataArray(key);
        if ((date == null) || (date.length == 0)) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date[0]);
        } catch (ParseException e) {
            return null;
        }
    }
    public String[] requestDataArray(String key) {
        return requestData.getOrDefault(key, null);
    }

	public List<Long> requestDataListOfLong(String key) {
		List<Long> longList = new ArrayList<>();
		String[] values = requestDataArray(key);
		if (values != null && values.length > 0) {
			for (String strValue : values) {
				if (strValue == null || strValue.isEmpty()) {
					continue;
				}
				longList.add(Long.valueOf(strValue));
			}
		}
		return longList;
	}
}