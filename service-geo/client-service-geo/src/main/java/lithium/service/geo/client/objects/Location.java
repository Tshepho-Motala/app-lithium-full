package lithium.service.geo.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location implements Serializable {

	private static final long serialVersionUID = 1L;

	Double latitude;
	Double longitude;

	CountryListEntry country;
	AdminLevel1ListEntry level1;
	AdminLevel2ListEntry level2;
	CityListEntry city;
	
	Long ipv4Start;
	Long ipv4End;
	
}
