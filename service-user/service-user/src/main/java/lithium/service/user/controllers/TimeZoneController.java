package lithium.service.user.controllers;

import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.user.client.objects.TimezoneBasic;

@RestController
@RequestMapping("/timezones")
public class TimeZoneController {

	/*
	 * @GetMapping LEGACY CODE public Response<List<String>> TimeZoneDropDown() { //
	 * getting available Ids Set<String> list = ZoneId.getAvailableZoneIds();
	 * List<String> arrlist = new ArrayList<>(); arrlist.addAll(list); return
	 * Response.<List<String>>builder().data(arrlist).build(); }
	 */

	/*
	 * @GetMapping USES HASHMAP public Response<Map<String, String>>
	 * TimeZoneDropDown() { // getting available Ids Map<String, String> sortedMap =
	 * new LinkedHashMap<>();// hashmap
	 * 
	 * List<String> zoneList = new ArrayList<>(ZoneId.getAvailableZoneIds()); //
	 * list of zones
	 * 
	 * // Get all ZoneIds Map<String, String> allZoneIds = getAllZoneIds(zoneList);
	 * 
	 * // sort map by key
	 * allZoneIds.entrySet().stream().sorted(Map.Entry.comparingByKey())
	 * .forEachOrdered(e -> sortedMap.put(e.getKey(), e.getValue())); return
	 * Response.<Map<String, String>>builder().data(sortedMap).build();
	 * 
	 * }
	 */

	@GetMapping
	public Response<List<TimezoneBasic>> TimeZoneDropDown() {
		List<TimezoneBasic> list = new ArrayList<>();
		LocalDateTime dt = LocalDateTime.now();
		
		for (String s : ZoneId.getAvailableZoneIds()) {
			
			ZoneId zone = ZoneId.of(s);// sets zone variable to zone in list
			ZonedDateTime zdt = dt.atZone(zone);// sets time in accordance to specified time zone
			ZoneOffset zos = zdt.getOffset();

			// replace Z to +00:00
			String offset = zos.getId().replaceAll("Z", "+00:00");

			TimezoneBasic tzb = TimezoneBasic.builder().label(s + "  (GMT: " + offset + ")").value(s).build();
			list.add(tzb);
		}
		return Response.<List<TimezoneBasic>>builder().data(list).build();

	}

}
