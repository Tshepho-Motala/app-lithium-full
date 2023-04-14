package lithium.service.geo.neustar.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class LocationType {

    @JsonProperty(value = "CountryData")
    protected CountryDataType countryData;

}
