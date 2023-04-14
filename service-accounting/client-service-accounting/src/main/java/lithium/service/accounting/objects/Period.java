package lithium.service.accounting.objects;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Period implements Serializable {
	private static final long serialVersionUID = 3052444424004436250L;
	public static final int GRANULARITY_YEAR = 1;
	public static final int GRANULARITY_MONTH = 2;
	public static final int GRANULARITY_DAY = 3;
	public static final int GRANULARITY_WEEK = 4;
	public static final int GRANULARITY_TOTAL = 5;
	
	private long id;
	private Integer year;
	private Integer month;
	private Integer week;
	private Integer day;
	
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateStart;
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date dateEnd;
	private boolean open;
	/**
	 * There is a granularity mapping table for this field that was added as a key reference to granularity values saved
	 * in the DB. This was done to provide clarity for data warehouse purposes
	 * {Ticket} : https://jira.livescore.com/browse/PLAT-1205
	 * {TA} : https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/2563473414/WAITING+FOR+REVIEW+LSPLAT-491+PLAT-1205+-+Generate+mapping+tables+for+limits
	 */
	private int granularity;
	private Domain domain;
}