package lithium.service.geo.objects;

import java.io.Serializable;

import lithium.service.geo.data.entities.AdminLevel1;
import lithium.service.geo.data.entities.AdminLevel2;
import lithium.service.geo.data.entities.City;
import lithium.service.geo.data.entities.Country;
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

	Country country;
	AdminLevel1 level1;
	AdminLevel2 level2;
	City city;

	Long ipv4Start;
	Long ipv4End;

	Network network;
	
}
