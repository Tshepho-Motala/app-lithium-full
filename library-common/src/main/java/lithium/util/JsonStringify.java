package lithium.util;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class JsonStringify {
	
	private static ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.ALWAYS);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper;
	}
	
	public static String objectsToString(Object... o) {
		try {
			ObjectMapper mapper = objectMapper();
			StringWriter sw = new StringWriter();
			for (Object obj:o) {
				mapper.writeValue(sw, obj);
			}
			return sw.toString();
		} catch (Exception ex) {
			return "{ 'error': '" + ex.getMessage() + "' }";
		}
	}
	
	public static String objectToString(Object o) {
		try {
			StringWriter sw = new StringWriter();
			objectMapper().writeValue(sw, o);
			return sw.toString();
		} catch (Exception ex) {
			return "{ 'error': '" + ex.getMessage() + "' }";
		}
	}
	
	public static String objectToStringFiltered(Object o) {
		return objectToStringFiltered(o, Arrays.asList("ccnumber", "cardNum", "card", "card.number", "cardNo"), "card.cvv", "cardSecurityCode", "cvv", "cvv2", "rawRequestLog", "rawResponseLog", "account_info");
	}
	
	public static String objectToStringFiltered(Object o, List<String> obscure, String... filters) {
		return objectToStringFiltered(0, o, obscure, filters);
	}
	
	public static String objectToStringFiltered(int level, Object o, List<String> obscure, String... filters) {
		try {
			StringWriter sw = new StringWriter();
			objectMapper().writeValue(sw, o);
			log.debug(sw.toString());
			Map<String, Object> map = objectMapper().readValue(sw.toString(), new TypeReference<Map<String,Object>>(){});
			String logStr = "";
			for (Entry<String, Object> entry:map.entrySet()) {
				log.debug("Key : "+entry.getKey()+" Obj : "+entry.getValue());
				boolean skip = false;
				for (String filter:filters) {
					if ((entry.getKey()!=null) && (entry.getKey().equalsIgnoreCase(filter))) skip = true; 
				}
				if (skip) continue;
				if ((entry!=null) && (entry.getValue() instanceof Map)) {
					for (int k=0; k<level; k++) logStr += "\t";
					level += 1;
					logStr += "\""+entry.getKey()+"\" : {\r\n";
					logStr += objectToStringFiltered(level, entry.getValue(), obscure, filters);
					for (int k=0; k<(level-1); k++) logStr += "\t";
					logStr += "},\r\n";
					level--;
				} else {
					for (int k=0; k<level; k++) logStr += "\t";
					Object value = entry.getValue();
					if (obscure.contains(entry.getKey())) {
						int len = (value!=null?value.toString():"").length();
						if (len > 0) {
							logStr += "\""+entry.getKey()+"\" : "+value.toString().substring(0, 6)+"******"+value.toString().substring(len-4, len)+",\n\r";
						} else {
							logStr += "\""+entry.getKey()+"\" : "+value+",\n\r";
						}
					} else {
						logStr += "\""+entry.getKey()+"\" : "+value+",\n\r";
					}
				}
			}
			log.debug(logStr);
			return logStr;
		} catch (Exception ex) {
			return "{ 'error': '" + ex.getMessage() + "' }";
		}
	}
	
	public static String listToString(List<?> list) {
		try {
			return objectMapper().writeValueAsString(list);
		} catch (Exception ex) {
			return "{ 'error': '" + ex.getMessage() + "' }";
		}
	}
}
