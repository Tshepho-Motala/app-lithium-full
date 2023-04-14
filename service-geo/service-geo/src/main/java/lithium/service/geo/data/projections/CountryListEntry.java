
package lithium.service.geo.data.projections;

public interface CountryListEntry {

	public String getCode();
	public String getIso3();
	public Integer getIsoNr();
	public String getFips();
	public String getName();
	public String getCapital();
	public Long getSqkm();
	public Long getPopulation();
	public String getContinent();
	public String getTopLevelDomain();
	public String getCurrencyCode();
	public String getCurrencyName();
	public String getPhone();
	public String getPostalCodeFormat();
	public String getPostalCodeRegex();
	public String getLanguages();
	public String getNeighbours();
	public String getEquivalentFips();
	public Boolean getEnabled();
	
}
